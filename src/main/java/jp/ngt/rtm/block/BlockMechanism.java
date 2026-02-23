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
import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityMechanism;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMechanism extends BlockContainerCustom
{
	public BlockMechanism()
	{
		super(Material.CIRCUITS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityMechanism();
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
	{
		Item item = holder.getPlayer().getHeldItemMainhand().getItem();
		if(item != RTMItem.installedObject && item != RTMItem.itemVehicle)
		{
			TileEntity tileEntity = holder.getWorld().getTileEntity(holder.getBlockPos());
			if(tileEntity instanceof TileEntityMechanism)
			{
				((TileEntityMechanism)tileEntity).onRightClick(holder.getPlayer());
			}
			return true;
		}
		return false;
	}

	@Override
	public net.minecraft.item.ItemStack getPickBlock(net.minecraft.block.state.IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, net.minecraft.util.math.BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
		return com.anatawa12.fixRtm.rtm.block.BlockOrnamentMain.getPickBlock(world, pos, jp.ngt.rtm.item.ItemInstalledObject.IstlObjType.MECHANISM);
	}
}