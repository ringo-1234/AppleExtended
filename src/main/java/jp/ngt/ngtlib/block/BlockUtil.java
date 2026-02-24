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

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockUtil
{
	//util.Facing
	public static final int[][] facing = {{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
	public static final int[][] field_01 = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};

	/*public static Block[] getConnectedBlock(IBlockAccess world, int x, int y, int z)
	{
		return new Block[]{world.getBlock(x+1, y, z),world.getBlock(x-1, y, z),world.getBlock(x, y+1, z),world.getBlock(x, y-1, z),world.getBlock(x, y, z+1),world.getBlock(x, y, z-1)};
	}

	public static Object[][] getConnectedBlockAndMetadata(IBlockAccess world, int x, int y, int z)
	{
		return new Object[][]{{world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z)},
				{world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z)},
				{world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z)},
				{world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z)},
				{world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1)},
				{world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1)}};
	}

	public static boolean[] isConnectedBlock(IBlockAccess world, Block[] blocks, int x, int y, int z)
	{
		boolean[] b0 = new boolean[6];
		Block[] connected = getConnectedBlock(world, x, y, z);
		for(int i0 = 0; i0 < 6; ++i0)
		{
			int i2 = 0;
			for(int i1 = 0; i1 < blocks.length; ++i1)
			{
				i2 += (connected[i0] == blocks[i1] ? 1 : 0);
			}
			b0[i0] = (i2 > 0);
		}
		return b0;
	}*/

	public static boolean[] isConnectedBlock(IBlockAccess world, Object[][] blocks, int x, int y, int z)
	{
		boolean[] b0 = new boolean[6];
		for(int i0 = 0; i0 < 6; ++i0)
		{
			int i2 = 0;
			for(int i1 = 0; i1 < blocks.length; ++i1)
			{
				IBlockState state = world.getBlockState(new BlockPos(x + field_01[i0][0], y + field_01[i0][1], z + field_01[i0][2]));
				boolean flag1 = blocks[i1][0].equals(state.getBlock());
				boolean flag2 = blocks[i1][1].equals(-1) || blocks[i1][1].equals(state.getBlock().getMetaFromState(state));
				if(flag1 && flag2)
				{
					b0[i0] = true;break;
				}
			}
		}
		return b0;
	}

	public static boolean[] isConnectedSolid(IBlockAccess world, int x, int y, int z)
	{
		return new boolean[]{
				world.isSideSolid(new BlockPos(x + 1, y, z), EnumFacing.WEST, true),
				world.isSideSolid(new BlockPos(x - 1, y, z), EnumFacing.EAST, true),
				world.isSideSolid(new BlockPos(x, y + 1, z), EnumFacing.DOWN, true),
				world.isSideSolid(new BlockPos(x, y - 1, z), EnumFacing.UP, true),
				world.isSideSolid(new BlockPos(x, y, z + 1), EnumFacing.NORTH, true),
				world.isSideSolid(new BlockPos(x, y, z - 1), EnumFacing.SOUTH, true)};
	}

	public static List<int[]> getBlockList(IBlockAccess world, int x, int y, int z, Block block, int range)
	{
		List<int[]> array = new ArrayList<int[]>();
		int r2 = range * 2;
		for(int i0 = 0; i0 < r2; ++i0)
		{
			for(int i1 = 0; i1 < r2; ++i1)
			{
				for(int i2 = 0; i2 < r2; ++i2)
				{
					Block block2 = world.getBlockState(new BlockPos(x - range + i0, y - range + i1, z - range + i2)).getBlock();
					if(block2 == block && !(i0 == range && i1 == range && i2 == range))
					{
						array.add(new int[]{x - range + i0, y - range + i1, z - range + i2});
					}
				}
			}
		}
		return array;
	}

	public static int[] rotateBlockPos(byte rotation, int x, int y, int z)
	{
		if(rotation == 1)
		{
			return new int[]{-z, y, x};
		}
		else if(rotation == 2)
		{
			return new int[]{-x, y, -z};
		}
		else if(rotation == 3)
		{
			return new int[]{z, y, -x};
		}
		return new int[]{x, y, z};
	}

	/**
	 * @return 視線の先にあるブロック(null有り)
	 * @param player
	 * @param distance 視線の距離(default:5.0)
	 * @param liquid 液体を含める
	 */
	public static RayTraceResult getMOPFromPlayer(EntityPlayer player, double distance, boolean liquid)
    {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62D - (double)player.getYOffset();
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3d vec3 = new Vec3d(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - NGTMath.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - NGTMath.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3d vec31 = vec3.addVector((double)f7 * distance, (double)f6 * distance, (double)f8 * distance);
        return player.world.rayTraceBlocks(vec3, vec31, liquid);
    }

	public static boolean isAir(IBlockAccess world, int x, int y, int z)
	{
		return getBlock(world, new BlockPos(x, y, z)) == Blocks.AIR;
	}

	public static IBlockState getBlockState(IBlockAccess world, int x, int y, int z)
	{
		return world.getBlockState(new BlockPos(x, y, z));
	}

	public static Block getBlock(IBlockAccess world, int x, int y, int z)
	{
		return getBlock(world, new BlockPos(x, y, z));
	}

	public static Block getBlock(IBlockAccess world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock();
	}

	public static int getMetadata(IBlockAccess world, int x, int y, int z)
	{
		return getMetadata(world, new BlockPos(x, y, z));
	}

	public static int getMetadata(IBlockAccess world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getMetaFromState(state);
	}

	public static TileEntity getTileEntity(IBlockAccess world, int x, int y, int z)
	{
		return getTileEntity(world, new BlockPos(x, y, z));
	}

	public static TileEntity getTileEntity(IBlockAccess world, BlockPos pos)
	{
		//高速化のため
		IBlockState state = world.getBlockState(pos);
		if(!state.getBlock().hasTileEntity(state)){return null;}

		TileEntity tile = world.getTileEntity(pos);
		if(tile == null && world instanceof World)
		{
			List<TileEntity> list = ((World)world).loadedTileEntityList;
			for(int i = 0; i < list.size(); ++i)
			{
				if(pos.equals(list.get(i).getPos()))
				{
					return list.get(i);
				}
			}
		}
		return tile;
	}

	public static boolean setAir(World world, int x, int y, int z)
	{
		return setBlock(world, x, y, z, Blocks.AIR, 0, 3);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block, int meta, int flag)
	{
		return setBlock(world, new BlockPos(x, y, z), block, meta, flag);
	}

	public static boolean setBlock(World world, BlockPos pos, Block block, int meta, int flag)
	{
		IBlockState newState = block.getStateFromMeta(meta);
		boolean b = world.setBlockState(pos, newState, flag);
		return b;
	}

	/**Client側ブロックの更新*/
	public static void markBlockForUpdate(World world, int x, int y, int z)
	{
		markBlockForUpdate(world, new BlockPos(x, y, z));
	}

	/**Client側ブロックの更新*/
	public static void markBlockForUpdate(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 2);
	}

	public static int[] toArray(BlockPos pos)
	{
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public static void playBlockBreakSound(World world, BlockPos pos, Block block, int meta)
	{
		world.playEvent(2001, pos, Block.getIdFromBlock(block) + (meta << 12));
	}
}