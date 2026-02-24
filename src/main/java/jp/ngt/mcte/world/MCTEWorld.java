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

package jp.ngt.mcte.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.block.TileEntityMiniature;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MCTEWorld extends NGTWorld
{
	public boolean updated;
	private long tickCount;
	private List<BUEntry> updateList = new ArrayList<BUEntry>();
	private Map<BlockPos, ITickable> tickables = new HashMap<>();

	public MCTEWorld(World par1, NGTObject par2, int x2, int y2, int z2)
	{
		super(par1, par2, x2, y2, z2);
		this.initWorld();
	}

	public MCTEWorld(World par1, NGTObject par2)
	{
		super(par1, par2);
		this.initWorld();
	}

	protected void initWorld()
	{
		this.updated = true;

		for(int i = 0; i < this.blockObject.xSize; ++i)
		{
			for(int j = 0; j < this.blockObject.ySize; ++j)
			{
				for(int k = 0; k < this.blockObject.zSize; ++k)
				{
					BlockPos pos = new BlockPos(i, j, k);
					TileEntity tileEntity = this.getTileEntity(pos);
					if(tileEntity != null)
					{
						this.setTileEntity(pos, tileEntity);
					}
				}
			}
		}
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
    {
		if(!this.isRemote)
		{
			int meta = newState.getBlock().getMetaFromState(newState);
			if(this.blockObject.setBlockSet(pos.getX(), pos.getY(), pos.getZ(), newState.getBlock(), meta))
			{
				this.notifyNeighborsRespectDebug(pos, newState.getBlock(), true);
				TileEntityMiniature miniature = (TileEntityMiniature)BlockUtil.getTileEntity(this.world, this.posX, this.posY, this.posZ);
				miniature.getUpdatePacket();
				this.updated = true;

				if(newState.getBlock().hasComparatorInputOverride(newState))
                {
                    this.updateComparatorOutputLevel(pos, newState.getBlock());
                }

				return true;
			}
		}
		return false;
    }

	@Override
	public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntity)
    {
		tileEntity.setWorld(this);
		this.worldTileEntities.put(pos, tileEntity);

		if(tileEntity instanceof ITickable)
		{
			this.tickables.put(pos, (ITickable)tileEntity);
		}
    }

	@Override
	public void removeTileEntity(BlockPos pos)
    {
		this.worldTileEntities.remove(pos);
		this.tickables.remove(pos);
    }

	/*@Override
	public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType)
    {
		if(this.blockObject.isValidPos(pos.getX(), pos.getY(), pos.getZ()))
		{
			if(!this.isAirBlock(pos))
			{
				super.notifyNeighborsOfStateChange(pos, blockType);
			}
		}
    }*/

	@Override
	public void scheduleBlockUpdate(BlockPos pos, Block block, int delay, int priority)
	{
		this.updateBlockTick(pos, block, delay, priority);
	}

	@Override
	public void scheduleUpdate(BlockPos pos, Block blockIn, int delay)
    {
        this.updateBlockTick(pos, blockIn, delay, 0);
    }

	@Override
	public void updateBlockTick(BlockPos pos, Block block, int delay, int priority)
    {
		this.updateList.add(new BUEntry(pos.getX(), pos.getY(), pos.getZ(), block,
				(long)delay + this.getTotalWorldTime(), priority));
    }

	@Override
	public boolean isBlockTickPending(BlockPos pos, Block blockType)
    {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		for(int i = 0; i < this.updateList.size(); ++i)
		{
			BUEntry entry = this.updateList.get(i);
			if(entry.x == x && entry.y == y && entry.z == z)
			{
				return true;
			}
		}
		return false;
    }

	@Override
	public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {
        this.getBlockState(pos).onBlockEventReceived(this, pos, eventID, eventParam);
    }

	@Override
	public long getTotalWorldTime()
    {
		return this.tickCount;
    }

	//TileEntity.update()から呼ぶ
	@Override
	public void tick()
    {
		++this.tickCount;
		this.tickUpdates(false);
		this.updateBlocks();
		this.updateEntities();
    }

	@Override
	public boolean tickUpdates(boolean flag)
    {
		if(!this.updateList.isEmpty())
		{
			List<BUEntry> delList = new ArrayList<BUEntry>();
			for(int i = 0; i < this.updateList.size(); ++i)
			{
				BUEntry entry = this.updateList.get(i);
				if(this.getTotalWorldTime() >= entry.time)
				{
					BlockPos pos = new BlockPos(entry.x, entry.y, entry.z);
					IBlockState state = this.getBlockState(pos);
					if(Block.isEqualTo(state.getBlock(), entry.block))
					{
						state.getBlock().updateTick(this, pos, state, this.rand);
						this.syncFlag = true;
					}
					delList.add(entry);
				}
			}
			this.updateList.removeAll(delList);
		}
		return true;
    }

	private boolean syncFlag = false;

	public boolean needsSync()
	{
		boolean flag = this.syncFlag;
		this.syncFlag = false;
		return flag;
	}

	/**ランダム更新*/
	@Override
	protected void updateBlocks()
    {
		;
    }

    @Override
    public void updateEntities()
    {
    	for(Entry<BlockPos, ITickable> entry : this.tickables.entrySet())
    	{
    		entry.getValue().update();
    	}
    }

	/**出力ポートに信号が入った*/
	public void onPortChanged(int x, int y, int z)
	{
		//this.world.notifyBlockOfStateChange(new BlockPos(this.posX, this.posY, this.posZ), MCTE.miniature);
		this.world.notifyNeighborsOfStateChange(new BlockPos(this.posX, this.posY, this.posZ), MCTE.miniature, true);
	}

	private class BUEntry
	{
		public final long time;
		public final int x, y, z;
		public final Block block;
		public final int priority;

		public BUEntry(int p2, int p3, int p4, Block pBlock, long pTime, int pPriority)
		{
			this.x = p2;
			this.y = p3;
			this.z = p4;
			this.block = pBlock;
			this.time = pTime;
			this.priority = pPriority;
		}
	}
}