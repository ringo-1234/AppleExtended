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

package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityMovingMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMovingMachine extends BlockContainerCustomWithMeta
{
	public BlockMovingMachine()
	{
		super(Material.IRON);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityMovingMachine();
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		EntityPlayer player = holder.getPlayer();
		TileEntity tile0 = BlockUtil.getTileEntity(world, pos);
		int meta = BlockUtil.getMetadata(world, pos);
		TileEntityMovingMachine tile = (TileEntityMovingMachine)tile0;
		if(NGTUtil.isEquippedItem(player, RTMItem.crowbar))
    	{
			if(!world.isRemote)
			{
				if(meta == 0)
				{
					if(!tile.hasPair())
					{
						tile.searchMM(pos.getX(), pos.getY(), pos.getZ());
					}
				}
				else if(meta == 1)
				{
					tile.generateVehicle(player);
				}
			}
		}
		else
		{
			if(world.isRemote)
			{
				TileEntityMovingMachine core = tile.getCore();
				player.openGui(RTMCore.instance, RTMCore.guiIdMovingMachine, world, core.getPos().getX(), core.getPos().getY(), core.getPos().getZ());
			}
		}
        return true;
    }

	@Override
	protected void neighborChanged(BlockArgHolder holder)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		if(!world.isRemote)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, pos);
			int meta = BlockUtil.getMetadata(world, pos);
			if(meta == 0)
			{
				((TileEntityMovingMachine)tile).onBlockChanged();
			}
		}
    }

	@Override
	public boolean removedByPlayer(BlockArgHolder holder, boolean willHarvest)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		if(!world.isRemote)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, pos);
			int meta = BlockUtil.getMetadata(world, pos);
			if(meta == 0)
			{
				((TileEntityMovingMachine)tile).reset(true);
			}
		}
		return super.removedByPlayer(holder, willHarvest);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tabs, NonNullList<ItemStack> list)
    {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
    }

	@Override
	protected ItemStack getItem(int damage)
    {
		return new ItemStack(RTMBlock.movingMachine, 1, damage);
    }
}