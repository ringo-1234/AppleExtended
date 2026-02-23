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

import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.block.tileentity.TileEntityPlantOrnament;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPlant extends BlockContainerCustom
{
	public BlockPlant()
	{
		super(Material.PLANTS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityPlantOrnament();
	}

	@Override
	protected boolean onBlockActivated(jp.ngt.ngtlib.block.BlockArgHolder holder, float hitX, float hitY, float hitZ) {
		return com.anatawa12.fixRtm.rtm.block.BlockOrnamentMain.onBlockActivated(holder);
	}

	@Override
	public net.minecraft.item.ItemStack getPickBlock(net.minecraft.block.state.IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, net.minecraft.util.math.BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
		return com.anatawa12.fixRtm.rtm.block.BlockOrnamentMain.getPickBlock(world, pos, jp.ngt.rtm.item.ItemInstalledObject.IstlObjType.PLANT);
	}
}