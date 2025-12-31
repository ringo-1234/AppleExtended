package jp.ngt.rtm.rail;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.gui.InternalButton;
import jp.ngt.rtm.gui.InternalGUI;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.rail.util.MarkerState;
import jp.ngt.rtm.rail.util.RailMaker;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailMapBasic;
import jp.ngt.rtm.rail.util.RailMapCustom;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.rail.util.SwitchType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMarker extends TileEntityCustom implements ITickable
{
	private static final int SEARCH_COUNT = 40;

	private RailPosition rp;
	private BlockPos startPos;
	private RailMap[] railMaps;
	/**start point only*/
	private List<BlockPos> markerPosList = new ArrayList<>();
	/**start point only, {{x,y,z}}*/
	private List<int[]> grid;

	//同期不要
	public float startPlayerPitch;
	public float startPlayerYaw;
	public byte startMarkerHeight;

	//Client Only
	public int editMode;
	private int markerState;
	@SideOnly(Side.CLIENT)
	public InternalGUI gui;
	@SideOnly(Side.CLIENT)
	public InternalButton[] buttons;
	/**[map][vtx][xyz]*/
	@SideOnly(Side.CLIENT)
	public float[][][] linePos;

	public TileEntityMarker()
	{
		this.markerState = MarkerState.DISTANCE.set(this.markerState, true);
		this.markerState = MarkerState.GRID.set(this.markerState, false);
		this.markerState = MarkerState.LINE1.set(this.markerState, false);
		this.markerState = MarkerState.LINE2.set(this.markerState, false);
		this.markerState = MarkerState.ANCHOR21.set(this.markerState, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if(nbt.hasKey("RP"))
        {
        	this.rp = RailPosition.readFromNBT(nbt.getCompoundTag("RP"));
        }
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if(this.rp != null)
        {
        	nbt.setTag("RP", this.rp.writeToNBT());
        }
        return nbt;
    }

	private int count;

	@Override
	public void update()
	{
		if(this.rp == null)
		{
			byte dir = BlockMarker.getMarkerDir(this.getBlockType(), this.getBlockMetadata());
			byte type = (byte)(this.getBlockType() == RTMBlock.markerSwitch ? 1 : 0);
			this.rp = new RailPosition(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), dir, type);

			if(this.getWorld().isRemote)
			{
				for(TileEntity tileEntity : this.getWorld().loadedTileEntityList)
				{
					if(tileEntity instanceof TileEntityMarker)
					{
						((TileEntityMarker)tileEntity).searchOtherMarkers();
					}
				}
			}
		}

		if(this.getWorld().isRemote)
		{
			if(this.count >= SEARCH_COUNT)
			{
				this.updateStartPos();
				this.count = 0;
			}
			++this.count;
		}
	}

	private void searchOtherMarkers()
	{
		((BlockMarker)this.getBlockType()).makeRailMap(this, this.getX(), this.getY(), this.getZ());//ペアとなるマーカーを探す
	}

	private void updateStartPos()
	{
		if(this.startPos != null)
		{
			TileEntity tileEntity = this.getWorld().getTileEntity(this.startPos);
			if(!(tileEntity instanceof TileEntityMarker))
			{
				this.startPos = null;//ペアとなるマーカーが存在しない場合、その情報をクリア
			}
		}
	}

	public RailPosition getMarkerRP()
	{
		return this.rp;
	}

	public void setMarkerRP(RailPosition par1)
	{
		this.rp = par1;
	}

	private RailPosition getMarkerRP(BlockPos pos)
	{
		TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), pos);
		if(tile instanceof TileEntityMarker)
		{
			return ((TileEntityMarker)tile).rp;
		}
		return null;
	}

	public List<int[]> getGrid()
	{
		return this.grid;
	}

	public RailMap[] getRailMaps()
	{
		return this.railMaps;
	}

	public void onChangeRailShape()
	{
		if(!this.isCoreMarker())
		{
			TileEntityMarker marker = this.getCoreMarker();
			if(marker != null)
			{
				marker.onChangeRailShape();
			}
		}
		else
		{
			RailMap[] maps = new RailMap[this.railMaps.length];
			for(int i = 0; i < maps.length; ++i)
			{
				RailPosition rp0 = this.railMaps[i].getStartRP();
				RailPosition rp1 = this.railMaps[i].getEndRP();
				rp1.cantCenter = -rp0.cantCenter;//カント同期
				maps[i] = new RailMapBasic(rp0, rp1);//RailMap再生成
			}
			this.railMaps = maps;
			this.linePos = null;
			this.createGrids();

			for(BlockPos pos : this.markerPosList)
			{
				TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), pos);
				if(tile instanceof TileEntityMarker)
				{
					TileEntityMarker marker = (TileEntityMarker)tile;
					marker.railMaps = maps;
				}
			}
		}
	}

	/**
	 * BlockMarker.createRailから呼び出し<br>
	 * マーカーのグリッド、アンカー表示用RailMap生成
	 * */
	public void setMarkersPos(List<BlockPos> list)
	{
		BlockPos newStartPos = null;

		if(list.size() == 1)
		{
			RailPosition rp = this.getMarkerRP(list.get(0));
			if(rp != null && rp.hasScript())
			{
				RailMap rm = new RailMapCustom(rp, rp.scriptName, rp.scriptArgs);
				this.railMaps = new RailMap[]{rm};
				newStartPos = new BlockPos(rp.blockX, rp.blockY, rp.blockZ);
			}
		}
		else if(list.size() == 2)
		{
			if(list.get(0) != null && list.get(1) != null)//たまにlist.get(1)がnullになる?
			{
				RailPosition rp0 = this.getMarkerRP(list.get(0));
				RailPosition rp1 = this.getMarkerRP(list.get(1));
				if(rp0 != null && rp1 != null)
				{
					RailMap rm = new RailMapBasic(rp0, rp1);
					this.railMaps = new RailMap[]{rm};
					newStartPos = new BlockPos(rp0.blockX, rp0.blockY, rp0.blockZ);
				}
			}
		}
		else
		{
			List<RailPosition> list2 = new ArrayList<>();
			for(BlockPos pos : list)
			{
				RailPosition rp0 = this.getMarkerRP(pos);
				if(rp0 != null)
				{
					list2.add(rp0);
				}
			}

			SwitchType type = (new RailMaker(this.getWorld(), list2)).getSwitch();
			if(type != null)
			{
				this.railMaps = type.getAllRailMap();
				if(this.railMaps != null)
				{
					RailPosition rp0 = this.railMaps[0].getStartRP();
					newStartPos = new BlockPos(rp0.blockX, rp0.blockY, rp0.blockZ);
				}
			}
		}

		if(this.railMaps == null){return;}

		this.markerPosList = list;

		this.createGrids();

		if(newStartPos != null)
		{
			for(int i = 0; i < list.size(); ++i)//ConcurrentModificationException回避
			{
				BlockPos pos = list.get(i);
				TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), pos);
				if(tile instanceof TileEntityMarker)
				{
					TileEntityMarker marker = (TileEntityMarker)tile;
					marker.setStartPos(newStartPos, this.railMaps);
				}
			}
		}

		/*if(!isClient)
		{
			RTMCore.NETWORK_WRAPPER.sendToAll(new PacketMarker(this, list));
		}*/
	}

	private void createGrids()
	{
		this.grid = new ArrayList<>();
		for(RailMap rm : this.railMaps)
		{
			//boolean flag = false;
			this.grid.addAll(rm.getRailBlockList(ItemRail.getDefaultProperty(), true));
			/*double lengthX = (float)Math.abs(rm.startRP.posX - rm.endRP.posX);
			double lengthZ = (float)Math.abs(rm.startRP.posX - rm.endRP.posX);
			double d0 = lengthX <= lengthZ ? lengthX : lengthZ;
			d0 *= RailPosition.Anchor_Correction_Value;
			TileEntity tile = this.worldObj.getTileEntity(rm.startRP.blockX, rm.startRP.blockY, rm.startRP.blockZ);
			if(tile instanceof TileEntityMarker && ((TileEntityMarker)tile).rp.anchorLength < 0.0F)
			{
				((TileEntityMarker)tile).rp.anchorLength = (float)d0;
				flag = true;
			}

			tile = this.worldObj.getTileEntity(rm.endRP.blockX, rm.endRP.blockY, rm.endRP.blockZ);
			if(tile instanceof TileEntityMarker && ((TileEntityMarker)tile).rp.anchorLength < 0.0F)
			{
				((TileEntityMarker)tile).rp.anchorLength = (float)d0;
				flag = true;
			}

			if(flag){rm.rebuild();}*/
		}
	}

	private void setStartPos(BlockPos pos, RailMap[] maps)
	{
		NGTLog.debug("[Marker] Start pos %s", pos.toString());
		this.startPos = pos;
		this.railMaps = maps;//RailMap同期

		if(!this.isCoreMarker())
		{
			this.markerPosList.clear();
			this.grid = null;
		}
	}

	public boolean isCoreMarker()
	{
		if(this.startPos == null)
		{
			return false;
		}
		return this.startPos.getX() == this.getX() && this.startPos.getY() == this.getY() && this.startPos.getZ() == this.getZ();
	}

	public TileEntityMarker getCoreMarker()
	{
		if(this.startPos == null){return null;}

		TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), this.startPos);
		if(tile instanceof TileEntityMarker)
		{
			return (TileEntityMarker)tile;
		}
		return null;
	}

	public RailPosition[] getAllRP()
	{
		//GuiRailMarker用
		if(this.markerPosList.isEmpty()){return new RailPosition[]{this.rp};}

		List<RailPosition> list2 = new ArrayList<RailPosition>();
		for(BlockPos pos : this.markerPosList)
		{
			RailPosition rp0 = this.getMarkerRP(pos);
			if(rp0 != null)
			{
				list2.add(rp0);
			}
		}
		return list2.toArray(new RailPosition[list2.size()]);
	}

	public boolean getState(MarkerState state)
	{
		return state.get(this.markerState);
	}

	public void flipState(MarkerState state)
	{
		boolean data = state.get(this.markerState);
		this.setState(state, (data ^ true));
	}

	public void setState(MarkerState state, boolean data)
	{
		if(!this.isCoreMarker())
		{
			TileEntityMarker marker = this.getCoreMarker();
			if(marker != null)
			{
				marker.setState(state, data);
			}
			else if(state == MarkerState.DISTANCE)//距離表示のみマーカー単体でも変更可
			{
				this.markerState = state.set(this.markerState, data);
			}
		}
		else
		{
			this.markerState = state.set(this.markerState, data);

			for(BlockPos pos : this.markerPosList)
			{
				TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), pos);
				if(tile instanceof TileEntityMarker)
				{
					TileEntityMarker marker = (TileEntityMarker)tile;
					marker.markerState = this.markerState;//state全部上書き
				}
			}
		}
	}

	public String getStateString(MarkerState state)
	{
		boolean data = state.get(this.markerState);
		return String.format("%s : %s", state.toString(), (data ? "ON" : "OFF"));
	}
}