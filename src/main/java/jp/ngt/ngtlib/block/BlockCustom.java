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

package jp.ngt.ngtlib.block;

import java.util.List;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.util.Usage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustom extends Block{

	protected AxisAlignedBB blockAABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

	public BlockCustom(Material mat)
	{
		super(mat);
	}

	/*描画関連******************************************************/

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

	@SideOnly(Side.CLIENT)
	@Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
		return true;
    }

	@SideOnly(Side.CLIENT)
	@Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return this.blockAABB.offset(pos);
    }

	/*AABB関連******************************************************/

	protected void setAABB(AxisAlignedBB par1)
	{
		this.blockAABB = par1;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return this.blockAABB;
    }

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, this.getCollisionBoundingBox(state, world, pos));
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return this.getBoundingBox(state, world, pos);
    }

	/*Block関連******************************************************/

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
		this.neighborChanged(new BlockArgHolder().setBlockState(state).setWorld(world).setBlockPos(pos));
    }

	protected void neighborChanged(BlockArgHolder holder)
    {
		;
    }

	@Override
    public boolean canProvidePower(IBlockState state)
    {
        return false;
    }

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side)
    {
		return this.getWeakPower(new BlockArgHolder().setBlockState(state).setBlockAccess(access).setBlockPos(pos).setFacing(side));
    }

	protected int getWeakPower(BlockArgHolder holder)
    {
		return 0;
    }

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side)
    {
		return this.getStrongPower(new BlockArgHolder().setBlockState(state).setBlockAccess(access).setBlockPos(pos).setFacing(side));
    }

	protected int getStrongPower(BlockArgHolder holder)
    {
		return 0;
    }

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return this.shouldCheckWeakPower(new BlockArgHolder().setBlockState(state).setBlockAccess(world).setBlockPos(pos).setFacing(side));
    }

	/**隣接ブロックからのの信号を使うか*/
	protected boolean shouldCheckWeakPower(BlockArgHolder holder)
    {
		return holder.getBlockState().isNormalCube();
    }

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
		return this.canConnectRedstone(new BlockArgHolder().setBlockState(state).setBlockAccess(world).setBlockPos(pos).setFacing(side));
    }

	protected boolean canConnectRedstone(BlockArgHolder holder)
    {
		return holder.getBlockState().canProvidePower() && holder.getFacing() != null;
    }

	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
		Usage.INSTANCE.addTooltip(this, stack.getItemDamage(), tooltip);
    }
}