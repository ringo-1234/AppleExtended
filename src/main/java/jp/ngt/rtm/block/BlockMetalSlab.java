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

import java.util.Random;

import jp.ngt.ngtlib.block.BlockCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMMaterial;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMetalSlab extends BlockCustomWithMeta
{
	public BlockMetalSlab()
	{
		super(RTMMaterial.fireproof);
		this.setLightOpacity(0);
		this.setTickRandomly(true);
		this.setAABB(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
		if(BlockUtil.getMetadata(world, pos) > 0)
		{
			entity.attackEntityFrom(DamageSource.LAVA, 1.0F);
			entity.setFire(1);
		}
    }

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
		if(!world.isRemote)
		{
			int meta = BlockUtil.getMetadata(world, pos);
			if(meta > 0)
			{
				BlockUtil.setBlock(world, pos, this, --meta, 2);
			}
		}
    }

	@Override
	public String getHarvestTool(IBlockState state)//Material != rockのとき必須？
    {
		return "";
    }

	@Override
	public int getHarvestLevel(IBlockState state)
    {
		return -1;
    }
}