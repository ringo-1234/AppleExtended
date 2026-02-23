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

import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityFluorescent;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFluorescent extends BlockContainerCustomWithMeta
{
	public BlockFluorescent()
	{
		super(Material.GLASS);
		this.setSoundType(SoundType.GLASS);
		this.setLightLevel(1.0F);
		this.setResistance(5.0F);
		this.setLightOpacity(0);
		this.setHardness(1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityFluorescent();
	}

	@Override
	protected ItemStack getItem(int damage)
	{
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.FLUORESCENT.id);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(BlockUtil.getMetadata(world, pos) == 2)
		{
			switch(NGTMath.RANDOM.nextInt(4))
			{
				case 0: return 0;
				case 1: return 4;
				case 2: return 8;
				case 3: return 12;
			}
		}
		return 15;
	}

	@Override
	protected boolean onBlockActivated(jp.ngt.ngtlib.block.BlockArgHolder holder, float hitX, float hitY, float hitZ) {
		return com.anatawa12.fixRtm.rtm.block.BlockOrnamentMain.onBlockActivated(holder);
	}

	@Override
	public ItemStack getPickBlock(net.minecraft.block.state.IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, net.minecraft.util.math.BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
		return com.anatawa12.fixRtm.rtm.block.BlockOrnamentMain.getPickBlock(world, pos, jp.ngt.rtm.item.ItemInstalledObject.IstlObjType.FLUORESCENT);
	}
}