/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.rtm.electric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.world.IChunkLoader;
import jp.ngt.rtm.world.RTMChunkManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntitySignalConverter extends TileEntityCustom implements IProvideElectricity, ITickable
{
	protected ComparatorType comparator = ComparatorType.EQUAL;
	/**条件がtrueの時の信号強度*/
	protected int signalOnTrue;
	/**条件がfalseの時の信号強度*/
	protected int signalOnFalse;

	protected int signal;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        int index = nbt.getInteger("comparatorIndex");
        ComparatorType type = ComparatorType.getType(index);
        int i0 = nbt.getInteger("signal_0");
        int i1 = nbt.getInteger("signal_1");
        this.setSignalProp(i0, i1, type);
    }

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("comparatorIndex", this.comparator.id);
        nbt.setInteger("signal_0", this.signalOnTrue);
        nbt.setInteger("signal_1", this.signalOnFalse);
        return nbt;
    }

	@Override
	public void update(){}

	public ComparatorType getComparator()
	{
		return this.comparator;
	}

	public int[] getSignalLevel()
	{
		return new int[]{this.signalOnTrue, this.signalOnFalse};
	}

	public void setSignalProp(int par1, int par2, ComparatorType par3)
	{
		this.signalOnTrue = par1;
		this.signalOnFalse = par2;
		this.comparator = par3;
		this.markDirty();
	}

	@Override
	public void markDirty()
    {
		super.markDirty();
		this.sendPacket();
    }

	/**ブロックのRS出力レベル*/
	public abstract int getRSOutput();

	@Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 256.0D;
    }

	public static TileEntitySignalConverter createTileEntity(int par1)
	{
		switch(SignalConverterType.getType(par1))
		{
		case RSIn:      return new TileEntitySC_RSIn();
		case RSOut:     return new TileEntitySC_RSOut();
		case Increment: return new TileEntitySC_Increment();
		case Decrement: return new TileEntitySC_Decrement();
		case Wireless:  return new TileEntitySC_Wireless();
		default:        return new TileEntitySC_RSIn();
		}
	}

	public static class TileEntitySC_RSIn extends TileEntitySignalConverter
	{
		@Override
		public int getRSOutput()
		{
			return 0;
		}

		@Override
		public int getElectricity()
		{
			//いずれかの面からRS入力がある場合
			for(EnumFacing side : EnumFacing.values())
			{
				//getStrongPowerだと直接入力
				if(this.world.isSidePowered(this.getPos().offset(side), side))
				{
					return this.signalOnTrue;
				}
			}
			return this.signalOnFalse;
		}

		@Override
		public void setElectricity(int x, int y, int z, int level)
		{
			;
		}
	}

	public static class TileEntitySC_RSOut extends TileEntitySignalConverter
	{
		@Override
		public int getRSOutput()
		{
			return this.signal == 1 ? 15 : 0;//signalはフラグ代わり
		}

		@Override
		public int getElectricity()
		{
			return 0;
		}

		@Override
		public void setElectricity(int x, int y, int z, int level)
		{
			int i0 = -1;

			switch(this.comparator)
			{
			case EQUAL:
				i0 = (level == this.signalOnTrue) ? 1 : 0;
				break;
			case GREATER_EQUAL:
				i0 = (level >= this.signalOnTrue) ? 1 : 0;
				break;
			case GREATER_THAN:
				i0 = (level > this.signalOnTrue) ? 1 : 0;
				break;
			case LESS_EQUAL:
				i0 = (level <= this.signalOnTrue) ? 1 : 0;
				break;
			case LESS_THAN:
				i0 = (level < this.signalOnTrue) ? 1 : 0;
				break;
			case NOT_EQUAL:
				i0 = (level != this.signalOnTrue) ? 1 : 0;
				break;
			default:
				break;
			}

			if(i0 >= 0 && i0 != this.signal)
			{
				this.signal = i0;
				this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
			}
		}
	}

	public static class TileEntitySC_Increment extends TileEntitySignalConverter
	{
		@Override
		public int getRSOutput()
		{
			return 0;
		}

		@Override
		public int getElectricity()
		{
			return this.signal;
		}

		@Override
		public void setElectricity(int x, int y, int z, int level)
		{
			this.signal = level > 0 ? ++level : 0;
		}
	}

	public static class TileEntitySC_Decrement extends TileEntitySignalConverter
	{
		@Override
		public int getRSOutput()
		{
			return 0;
		}

		@Override
		public int getElectricity()
		{
			return this.signal;
		}

		@Override
		public void setElectricity(int x, int y, int z, int level)
		{
			this.signal = level > 1 ? --level : level;
		}
	}

	public static class TileEntitySC_Wireless extends TileEntitySignalConverter implements IChunkLoader
	{
		private static Map<Integer, List<TileEntitySC_Wireless>> ADAPTER_MAP = new HashMap<Integer, List<TileEntitySC_Wireless>>();

		private int prevChannel = 0;

		public TileEntitySC_Wireless()
		{
			List<TileEntitySC_Wireless> list = this.getList(this.prevChannel);
			list.add(this);
		}

		private List<TileEntitySC_Wireless> getList(int par1)
		{
			List<TileEntitySC_Wireless> list = ADAPTER_MAP.get(par1);
			if(list == null)
			{
				list = new ArrayList<TileEntitySC_Wireless>();
				ADAPTER_MAP.put(par1, list);
			}
			return list;
		}

		private void updateAntennaList()
		{
			List<TileEntitySC_Wireless> list = this.getList(this.prevChannel);
	        if(list.contains(this))
	        {
	        	list.remove(this);
	        }
	        List<TileEntitySC_Wireless> list2 = this.getList(this.getChannel());
	        list2.add(this);
	        this.prevChannel = this.getChannel();
		}

		@Override
		public void update()
		{
			super.update();

			if(!this.world.isRemote)
			{
				this.updateChunks();
			}
		}

		@Override
		public void invalidate()
		{
			super.invalidate();
			if(!this.world.isRemote)
			{
				this.releaseTicket();
			}
		}

		@Override
		public void validate()
		{
			super.validate();

			if(!this.world.isRemote)
			{
				this.updateChunks();
			}
		}

		public int getChannel()
		{
			return this.signalOnTrue;
		}

		public int getChunkLoadRange()
		{
			return this.signalOnFalse;
		}

		@Override
		public void setSignalProp(int par1, int par2, ComparatorType par3)
		{
			super.setSignalProp(par1, par2, par3);
			if(this.world == null || !this.world.isRemote)
			{
				this.updateAntennaList();
			}
		}

		@Override
		public int getRSOutput()
		{
			return 0;
		}

		@Override
		public int getElectricity()
		{
			return this.signal;
		}

		@Override
		public void setElectricity(int x, int y, int z, int level)
		{
			List<TileEntitySC_Wireless> list = this.getList(this.getChannel());
			for(TileEntitySC_Wireless tile : list)
			{
				tile.setWirelessSignal(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), level);
			}
		}

		private void setWirelessSignal(int x, int y, int z, int level)
		{
			this.signal = level;
		}

		//**ChunkLoader*******************************************************************************/

		private Ticket ticket;
		private final Set<ChunkPos> loadedChunks = new HashSet();
		private boolean finishSetup;

	    /**ServerTickごとに呼び出し*/
	    private void updateChunks()
	    {
	    	if(this.isChunkLoaderEnable())
			{
	    		this.forceChunkLoading();
			}
			else
			{
				this.releaseTicket();
			}
	    }

	    @Override
	    public boolean isChunkLoaderEnable()
	    {
	    	return this.getChunkLoadRange() > 0;
	    }

	    private void releaseTicket()
	    {
	    	this.loadedChunks.clear();
	    	if(this.ticket != null)
	    	{
	    		ForgeChunkManager.releaseTicket(this.ticket);
	            this.ticket = null;
	    	}
	    }

	    private boolean requestTicket()
	    {
	    	Ticket chunkTicket = RTMChunkManager.INSTANCE.getNewTicket(this.world, Type.NORMAL);
	        if(chunkTicket != null)
	        {
	        	int depth = this.getChunkLoadRange();
	        	chunkTicket.getModData();
	            chunkTicket.setChunkListDepth(depth);
	            RTMChunkManager.writeData(chunkTicket, this);
	            this.setChunkTicket(chunkTicket);
	            return true;
	        }
	        NGTLog.debug("[RTM] Failed to get ticket (Chunk Loader)");
	        return false;
	    }

	    @Override
	    public void setChunkTicket(Ticket par1)
	    {
	    	if(this.ticket != par1)
	    	{
	            ForgeChunkManager.releaseTicket(this.ticket);
	        }
	    	this.ticket = par1;
	    	this.finishSetup = false;
	    }

	    @Override
	    public void forceChunkLoading()
	    {
	    	int cX = this.getPos().getX() >> 4;
	    	int cZ = this.getPos().getZ() >> 4;
	    	this.forceChunkLoading(cX, cZ);
	    }

	    @Override
	    public void forceChunkLoading(int x, int z)
	    {
	    	if(!this.world.isRemote)
	    	{
	    		if(this.ticket == null)
	            {
	        		if(!this.requestTicket()){return;}
	            }

	        	if(!this.finishSetup)
	        	{
	        		this.setupChunks(x, z);
	        		this.finishSetup = true;
	        	}

	            for(ChunkPos chunk : this.loadedChunks)
	            {
	            	ForgeChunkManager.forceChunk(this.ticket, chunk);
	                //ForgeChunkManager.reorderChunk(this.ticket, chunk);//並び替え
	            }
	            ChunkPos myChunk = new ChunkPos(x, z);//省くと機能しない
	            ForgeChunkManager.forceChunk(this.ticket, myChunk);
	    	}
	    }

	    private void setupChunks(int xChunk, int zChunk)
	    {
	    	int range = this.getChunkLoadRange();
			RTMChunkManager.INSTANCE.getChunksAround(this.loadedChunks, xChunk, zChunk, range);
	    }
	}

	/**"==", ">", ">=", "<", "<=", "!="*/
	public enum ComparatorType
	{
		EQUAL(0, "=="),
		GREATER_THAN(1, ">"),
		GREATER_EQUAL(2, ">="),
		LESS_THAN(3, "<"),
		LESS_EQUAL(4, "<="),
		NOT_EQUAL(5, "!=");

		public final byte id;
		public final String operator;

		private ComparatorType(int par1, String par2)
		{
			this.id = (byte)par1;
			this.operator = par2;
		}

		public static ComparatorType getType(int par1)
		{
			for(ComparatorType type : ComparatorType.values())
			{
				if(type.id == par1)
				{
					return type;
				}
			}
			return EQUAL;
		}
	}
}