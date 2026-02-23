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

import jp.ngt.ngtlib.block.BlockLiquidBase;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMFluid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockMeltedMetal extends BlockLiquidBase
{
	public BlockMeltedMetal(Fluid fluid)
	{
		super(fluid, Material.LAVA);
		this.setLightLevel(1.0F);
	}

	@Override
	public int tickRate(World world)
    {
		return 10;
    }

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
		super.updateTick(world, pos, state, random);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(this == RTMFluid.liquefiedSteel)
		{
			if(BlockUtil.getMetadata(world, x, y, z) == 0 && random.nextInt(5) == 0)
			{
				if(!world.isRemote)
				{
					boolean flag0 = false;//air
					boolean flag1 = true;//meltedMetal
					for(int i = 0; i < BlockUtil.facing.length; ++i)
					{
						int x0 = x + BlockUtil.facing[i][0];
						int y0 = y + BlockUtil.facing[i][1];
						int z0 = z + BlockUtil.facing[i][2];
						Block block1 = BlockUtil.getBlock(world, x0, y0, z0);
						if(block1 == Blocks.AIR)
						{
							flag0 = true;
						}

						if(block1 instanceof BlockLiquidBase && BlockUtil.getMetadata(world, x0, y0, z0) > 0)
						{
							flag1 = false;
						}
					}

					if(flag0 && flag1 && random.nextInt(5) == 0)
					{
						BlockUtil.setBlock(world, x, y, z, RTMBlock.steelSlab, 15, 2);
					}
				}
			}
		}
    }

	@Override
	protected int canFlowLiquid(World world, int x, int y, int z)
	{
		Block block = BlockUtil.getBlock(world, x, y, z);
		if(block == RTMFluid.furnaceFire || block == RTMFluid.exhaustGas)
		{
			return 15;
		}
		return super.canFlowLiquid(world, x, y, z);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
		if(!world.isRemote)
		{
			int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();

			for(int i = 0; i < BlockUtil.facing.length; ++i)
			{
				int x0 = x + BlockUtil.facing[i][0];
				int y0 = y + BlockUtil.facing[i][1];
				int z0 = z + BlockUtil.facing[i][2];
				IBlockState state2 = BlockUtil.getBlockState(world, x0, y0, z0);
				if(state2.getMaterial() == Material.WATER)
				{
					BlockUtil.setAir(world, x0, y0, z0);
					BlockUtil.setAir(world, x, y, z);
					world.createExplosion(null, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, 8.0F, true);
				}
			}
		}

		super.neighborChanged(state, world, pos, block, fromPos);
    }

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
		entity.motionY = 0.20000000298023224D;
		entity.motionX = (double)((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
		entity.motionZ = (double)((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
		entity.playSound(SoundEvents.BLOCK_LAVA_POP, 0.4F, 2.0F + world.rand.nextFloat() * 0.4F);
		entity.attackEntityFrom(DamageSource.LAVA, 1.0F);
		entity.setFire(5);
    }
}