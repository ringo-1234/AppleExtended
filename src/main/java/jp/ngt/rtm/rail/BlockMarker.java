package jp.ngt.rtm.rail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMRail;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.util.RailMaker;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailMapBasic;
import jp.ngt.rtm.rail.util.RailMapCustom;
import jp.ngt.rtm.rail.util.RailMapTurntable;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.rail.util.SwitchType;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMarker extends BlockContainerCustomWithMeta
{
	/**0:normal, 1:switch*/
	public final MarkerType markerType;

	public BlockMarker(MarkerType type)
	{
		super(Material.GLASS);
		this.markerType = type;
		this.setLightOpacity(1);
		this.setLightLevel(1.0F);
		this.setHardness(1.0F);
		this.setResistance(5.0F);
		this.setSoundType(SoundType.GLASS);
		this.setAABB(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@SideOnly(Side.CLIENT)
	@Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;//色変更
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
		switch(this.markerType)
		{
		case STANDARD:
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 4));
			break;
		case SWITCH:
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 4));
			break;
		}
    }

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityMarker();
    }

	/**0~3*/
	public static int getFacing(EntityLivingBase placer, boolean isDiagonal)
	{
		if(isDiagonal)
		{
			return (NGTMath.floor(NGTMath.normalizeAngle(placer.rotationYaw + 180.0D) / 90.0D) & 3);//斜め
		}
		else
		{
			return (NGTMath.floor((NGTMath.normalizeAngle(placer.rotationYaw + 180.0D) / 90.0D) + 0.5D) & 3);
		}
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		int meta = stack.getItemDamage();
		int playerFacing = getFacing(placer, meta >= 4);
		int i = meta / 4;
		BlockUtil.setBlock(world, pos, this, playerFacing + i * 4, 2);
	}

    @Override
    protected boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
	{
    	World world = holder.getWorld();
    	EntityPlayer player = holder.getPlayer();
    	int x = holder.getBlockPos().getX();
    	int y = holder.getBlockPos().getY();
    	int z = holder.getBlockPos().getZ();
    	ItemStack item = player.inventory.getCurrentItem();
    	if(item != null)
    	{
    		TileEntity tile = world.getTileEntity(holder.getBlockPos());
    		if(!(tile instanceof TileEntityMarker)){return true;}
    		TileEntityMarker marker = (TileEntityMarker)tile;

    		if(item.getItem() == Item.getItemFromBlock(RTMBlock.marker) || item.getItem() == Item.getItemFromBlock(RTMBlock.markerSwitch))
    		{
    			if(world.isRemote)
    	    	{
    				player.openGui(RTMCore.instance, RTMCore.guiIdRailMarker, world, x, y, z);
    	    	}
    			return true;
    		}
    	}

    	if(!world.isRemote)
    	{
    		if(this.onMarkerActivated(world, x, y, z, player, true))
    		{
    			//player.addStat(RTMAchievement.layRail, 1);
    			if(!player.capabilities.isCreativeMode)
    			{
    				item.shrink(1);
    			}
    		}
    	}

    	return true;
    }

    /**
     * TileEntity側から呼ぶこと<br>
     * 形状のみ決定、ブロック設置はしない
     * */
    public void makeRailMap(TileEntityMarker marker, int x, int y, int z)
    {
    	this.onMarkerActivated(marker.getWorld(), x, y, z, null, false);
    }

	public boolean onMarkerActivated(World world, int x, int y, int z, EntityPlayer player, boolean makeRail)
    {
		ResourceStateRail prop = this.hasRail(player, makeRail);
		if(prop != null)
		{
			boolean isCreative = (player == null) || player.capabilities.isCreativeMode;
			List<RailPosition> rps = this.searchAllMarker(world, x, y, z);
			for(RailPosition rp : rps)
			{
				//Itemに設定した高さの適用
				rp.addHeight(prop.blockHeight - ResourceStateRail.INIT_HEIGHT);
			}
			return createRail(world, x, y, z, rps, prop, makeRail, isCreative);
		}
		return false;
    }

	private List<RailPosition> searchAllMarker(World world, int x, int y, int z)
	{
		List<RailPosition> list = new ArrayList<>();
		int dis1 = RTMCore.railGeneratingDistance;
		int dis2 = dis1 * 2;
		int hei1 = RTMCore.railGeneratingHeight;
		int hei2 = hei1 * 2;

		for(int i = 0; i < dis2; ++i)
		{
			for(int j = 0; j < hei2; ++j)
			{
				for(int k = 0; k < dis2; ++k)
				{
					int x0 = x - dis1 + i;
					int y0 = y - hei1 + j;
					int z0 = z - dis1 + k;
					RailPosition rp = this.getRailPosition(world, x0, y0, z0);
					if(rp != null)
					{
						list.add(rp);
					}
				}
			}
		}

		//分岐RPを先頭にもってくる
		list.sort((arg0, arg1)->{
			if(arg0.switchType != arg1.switchType)
			{
				return arg1.switchType - arg0.switchType;//分岐のほうが順序前
			}
			else if(arg0.blockY != arg1.blockY)
			{
				return arg0.blockY - arg1.blockY;//Y低いほうが前
			}
			return arg0.hashCode() - arg1.hashCode();
		});

		return list;
	}

	public static boolean createRail(World world, int x, int y, int z, List<RailPosition> rps, ResourceStateRail state, boolean makeRail, boolean isCreative)
	{
		if(rps.size() == 1)
		{
			RailPosition rp = rps.get(0);
			if(rp.hasScript())
			{
				createCustomRail(world, rp, state, makeRail, isCreative);
			}
		}
		else if(rps.size() == 2)
		{
			RailPosition rp1 = rps.get(0);
			RailPosition rp2 = rps.get(1);
			if(rp1.switchType == 1 && rp2.switchType == 1)
			{
				createTurntable(world, rp1, rp2, state, makeRail, isCreative);
			}
			else
			{
				RailPosition start = rp2.blockY >= rp1.blockY ? rp1 : rp2;
				RailPosition end = rp2.blockY >= rp1.blockY ? rp2 : rp1;
				createNormalRail(world, start, end, state, makeRail, isCreative);
			}
		}
		else if(rps.size() > 2)
		{
			createSwitchRail(world, x, y, z, rps, state, makeRail, isCreative);
		}
		return false;
	}

	/**
	 * 通常のレール<br>
	 * start.y <= end.yでなければならない
	 */
	private static boolean createNormalRail(World world, RailPosition start, RailPosition end, ResourceStateRail prop, boolean makeRail, boolean isCreative)
	{
		RailMap railMap = new RailMapBasic(start, end);
		if(makeRail && railMap.canPlaceRail(world, isCreative, prop))
		{
			railMap.setRail(world, RTMRail.largeRailBase, start.blockX, start.blockY, start.blockZ, prop);

			BlockUtil.setBlock(world, start.blockX, start.blockY, start.blockZ, RTMRail.largeRailCore, 0, 3);
			TileEntityLargeRailCore tile = (TileEntityLargeRailCore)BlockUtil.getTileEntity(world, start.blockX, start.blockY, start.blockZ);
			tile.setRailPositions(new RailPosition[]{start, end});
			tile.getResourceState().readFromNBT(prop.writeToNBT());
			tile.setStartPoint(start.blockX, start.blockY, start.blockZ);

			tile.createRailMap();
			tile.sendPacket();

			if(BlockUtil.getBlock(world, end.blockX, end.blockY, end.blockZ) instanceof BlockMarker)
			{
				BlockUtil.setAir(world, end.blockX, end.blockY, end.blockZ);
			}

			return true;
		}
		else
		{
			TileEntity tile = BlockUtil.getTileEntity(world, start.blockX, start.blockY, start.blockZ);
			if(tile instanceof TileEntityMarker)
			{
				List<BlockPos> list = new ArrayList<>();
				list.add(new BlockPos(start.blockX, start.blockY, start.blockZ));
				list.add(new BlockPos(end.blockX, end.blockY, end.blockZ));
				((TileEntityMarker)tile).setMarkersPos(list);
			}
			return false;
		}
	}

	private static boolean createCustomRail(World world, RailPosition rp, ResourceStateRail prop, boolean makeRail, boolean isCreative)
	{
		RailMap railMap = new RailMapCustom(rp, rp.scriptName, rp.scriptArgs);
		if(makeRail && railMap.canPlaceRail(world, isCreative, prop))
		{
			railMap.setRail(world, RTMRail.largeRailBase, rp.blockX, rp.blockY, rp.blockZ, prop);

			BlockUtil.setBlock(world, rp.blockX, rp.blockY, rp.blockZ, RTMRail.largeRailCore, 0, 3);
			TileEntityLargeRailCore tile = (TileEntityLargeRailCore)BlockUtil.getTileEntity(world, rp.blockX, rp.blockY, rp.blockZ);
			tile.setRailPositions(new RailPosition[]{rp, railMap.getEndRP()});
			tile.getResourceState().readFromNBT(prop.writeToNBT());
			tile.setStartPoint(rp.blockX, rp.blockY, rp.blockZ);

			tile.createRailMap();
			tile.sendPacket();

			return true;
		}
		else
		{
			TileEntity tile = BlockUtil.getTileEntity(world, rp.blockX, rp.blockY, rp.blockZ);
			if(tile instanceof TileEntityMarker)
			{
				List<BlockPos> list = new ArrayList<>();
				list.add(new BlockPos(rp.blockX, rp.blockY, rp.blockZ));
				((TileEntityMarker)tile).setMarkersPos(list);
			}
			return false;
		}
	}

	/**
	 * 分岐レール<br>
	 * @param list {x, y, z}
	 */
	private static boolean createSwitchRail(World world, int x, int y, int z, List<RailPosition> list, ResourceStateRail prop, boolean makeRail, boolean isCreative)
	{
		RailMaker railMaker = new RailMaker(world, list);
		SwitchType st = railMaker.getSwitch();
		if(st == null){return false;}
		RailMap[] railMaps = st.getAllRailMap();
		if(railMaps == null){return false;}

		boolean flag = false;
		for(RailMap rm : railMaps)
		{
			if(!rm.canPlaceRail(world, isCreative, prop)){flag = true;}
		}

		if(!makeRail || flag)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
			if(tile instanceof TileEntityMarker)
			{
				List<BlockPos> posList = new ArrayList<>();
				for(int i = 0; i < list.size(); ++i)
				{
					RailPosition rp = list.get(i);
					posList.add(new BlockPos(rp.blockX, rp.blockY, rp.blockZ));
				}
				((TileEntityMarker)tile).setMarkersPos(posList);
			}
			return false;//障害物あり
		}

		//分岐&Y低いのを始点とする
		RailPosition rps = list.get(0);
		x = rps.blockX;
		y = rps.blockY;
		z = rps.blockZ;

		for(RailMap rm : railMaps)
		{
			rm.setRail(world, RTMRail.largeRailBase, x, y, z, prop);
		}

		for(RailPosition rp : list)
		{
			BlockUtil.setBlock(world, rp.blockX, rp.blockY, rp.blockZ, RTMRail.largeRailSwitchBase, 0, 3);
			TileEntityLargeRailSwitchBase tile = (TileEntityLargeRailSwitchBase)BlockUtil.getTileEntity(world, rp.blockX, rp.blockY, rp.blockZ);
			tile.setStartPoint(x, y, z);
		}

		BlockUtil.setBlock(world, x, y, z, RTMRail.largeRailSwitchCore, 0, 3);
		TileEntityLargeRailSwitchCore tile = (TileEntityLargeRailSwitchCore)BlockUtil.getTileEntity(world, x, y, z);
		tile.setRailPositions(list.toArray(new RailPosition[list.size()]));
		tile.getResourceState().readFromNBT(prop.writeToNBT());
		tile.setStartPoint(x, y, z);

		tile.createRailMap();
		tile.sendPacket();
		return true;
	}

	private static boolean createTurntable(World world, RailPosition start, RailPosition end, ResourceStateRail prop, boolean makeRail, boolean isCreative)
	{
		int cx = 0;
		int cy = start.blockY;
		int cz = 0;
		int r = 0;

		if(start.blockX == end.blockX && (start.blockZ - end.blockZ) % 2 == 0)
		{
			cx = start.blockX;
			cz = (start.blockZ + end.blockZ) / 2;
			r = Math.abs(start.blockZ - end.blockZ) / 2;
		}

		if(start.blockZ == end.blockZ && (start.blockX - end.blockX) % 2 == 0)
		{
			cx = (start.blockX + end.blockX) / 2;
			cz = start.blockZ;
			r = Math.abs(start.blockX - end.blockX) / 2;
		}

		if(r == 0){return false;}

		RailMapTurntable railMap = new RailMapTurntable(start, end, cx, cy, cz, r);
		if(makeRail && railMap.canPlaceRail(world, isCreative, prop))
		{
			railMap.setRail(world, RTMRail.largeRailBase, cx, cy, cz, prop);

			BlockUtil.setBlock(world, cx, cy, cz, RTMRail.TURNTABLE_CORE, 0, 3);
			TileEntityTurnTableCore tile = (TileEntityTurnTableCore)BlockUtil.getTileEntity(world, cx, cy, cz);
			tile.setRailPositions(new RailPosition[]{start, end});
			tile.getResourceState().readFromNBT(prop.writeToNBT());
			tile.setStartPoint(cx, cy, cz);

			tile.createRailMap();
			tile.sendPacket();

			return true;
		}

		return false;
	}

	public static byte getMarkerDir(Block block, int meta)
	{
		int i0 = meta & 3;
		int i1 = ((6 - i0) & 3) * 2;
		if((block == RTMBlock.marker || block == RTMBlock.markerSwitch) && meta >= 4)
		{
			i1 = (i1 + 7) & 7;
		}
		return (byte)i1;
	}

	private RailPosition getRailPosition(World world, int x, int y, int z)
	{
		TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
		if(tile instanceof TileEntityMarker)
		{
			return ((TileEntityMarker)tile).getMarkerRP();
		}
		return null;
	}

	public ResourceStateRail hasRail(@Nullable EntityPlayer player, boolean par2)
	{
		if(player == null)
		{
			return ItemRail.getDefaultProperty();
		}
		else if(PermissionManager.INSTANCE.hasPermission(player, RTMCore.EDIT_RAIL))
		{
			ItemStack item = player.inventory.getCurrentItem();
			if(item.getItem() == RTMItem.itemLargeRail)
			{
				return ((ItemRail)RTMItem.itemLargeRail).getModelState(item);
			}

			if(player.capabilities.isCreativeMode || !par2)
			{
				return ItemRail.getDefaultProperty();
			}
		}
		return null;
	}

	public enum MarkerType
	{
		STANDARD(0xFF0000),
		SWITCH(0x0000FF);

		public final int color;

		private MarkerType(int par1)
		{
			this.color = par1;
		}
	}
}