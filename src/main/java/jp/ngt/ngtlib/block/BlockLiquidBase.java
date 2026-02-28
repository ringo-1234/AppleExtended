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

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.RTMMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import java.util.Random;

public abstract class BlockLiquidBase extends BlockFluidClassic {
    public BlockLiquidBase(Fluid fluid, Material par1) {
        super(fluid, par1);
    }

    @Override
    public int tickRate(World world)//water:5, lava:30(10)
    {
        return 5;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return hitIfLiquid;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            int meta = BlockUtil.getMetadata(world, x, y, z);
            int prevMeta = meta;
            meta = this.setLiquid(world, x, y, z, x, y - 1, z, meta);
            if (meta == prevMeta) {
                if ((meta >= 0 && this.canFlowLiquid(world, x - 1, y - 1, z) > 0) || this.canFlowLiquid(world, x - 1, y, z) + meta > 15) {
                    meta = this.setLiquid(world, x, y, z, x - 1, y, z, meta);
                }

                if ((meta >= 0 && this.canFlowLiquid(world, x + 1, y - 1, z) > 0) || this.canFlowLiquid(world, x + 1, y, z) + meta > 15) {
                    meta = this.setLiquid(world, x, y, z, x + 1, y, z, meta);
                }

                if ((meta >= 0 && this.canFlowLiquid(world, x, y - 1, z - 1) > 0) || this.canFlowLiquid(world, x, y, z - 1) + meta > 15) {
                    meta = this.setLiquid(world, x, y, z, x, y, z - 1, meta);
                }

                if ((meta >= 0 && this.canFlowLiquid(world, x, y - 1, z + 1) > 0) || this.canFlowLiquid(world, x, y, z + 1) + meta > 15) {
                    meta = this.setLiquid(world, x, y, z, x, y, z + 1, meta);
                }
            }

            if (this == BlockUtil.getBlock(world, x, y, z)) {
                this.meltNeighborBlocks(world, x, y, z, random);
            }

            if (meta != prevMeta) {
                world.scheduleBlockUpdate(pos, this, this.tickRate(world), 0);
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (this == BlockUtil.getBlock(world, pos)) {
            world.scheduleBlockUpdate(pos, this, this.tickRate(world), 0);
        }

        //溶岩では、水との反応の処理を行う
    }

    /**
     * @return 流し込み可能な量(0~15, -1で不可)
     */
    protected int canFlowLiquid(World world, int x, int y, int z) {
        if (world.isAirBlock(new BlockPos(x, y, z))) {
            return 15;
        } else if (this == BlockUtil.getBlock(world, x, y, z)) {
            return 14 - BlockUtil.getMetadata(world, x, y, z);
        }
        return -1;
    }

    /**
     * ※BlockUpdateのスケジュール無し
     *
     * @return 処理後の自身のメタデータ(流れ切った場合は-1)
     */
    protected int setLiquid(World world, int x, int y, int z, int targetX, int targetY, int targetZ, int myMetadata) {
        int i0 = this.canFlowLiquid(world, targetX, targetY, targetZ);
        if (i0 >= 0) {
            BlockUtil.setBlock(world, targetX, targetY, targetZ, this, this.clampMetadata(15 - i0), 2);
            world.scheduleBlockUpdate(new BlockPos(targetX, targetY, targetZ), this, this.tickRate(world), 0);
            if (myMetadata > 0) {
                --myMetadata;
                BlockUtil.setBlock(world, x, y, z, this, this.clampMetadata(myMetadata), 2);
                return myMetadata;
            } else {
                world.setBlockToAir(new BlockPos(x, y, z));
                return -1;
            }
        }
        return myMetadata;
    }

    /**
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     * @param amount     流し込む量(1~16)
     * @param checkBlock ブロックがあるかどうかチェック
     * @return 余った量(流し切った場合は0)
     */
    public static int addLiquid(World world, int x, int y, int z, Block block, int amount, boolean checkBlock) {
        Block block0 = BlockUtil.getBlock(world, x, y, z);
        if (!checkBlock || (block0 == Blocks.AIR || (block0 == block && block0 instanceof BlockLiquidBase))) {
            int i0 = BlockUtil.getMetadata(world, x, y, z) + amount;
            int i1 = i0 & 15;
            BlockUtil.setBlock(world, x, y, z, block, i1, 2);
            return i0 > i1 ? i0 - i1 : 0;
        }
        return amount;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, this.tickRate(world), 0);
    }

    protected int clampMetadata(int par1) {
        return NGTMath.clamp(par1, 0, 15);
    }

	/*@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int par2, int par3, int par4)
    {
        this.setBlockBounds(blockAccess.getBlockMetadata(par2, par3, par4) & 15);
    }

	protected void setBlockBounds(int par1)
    {
        int j = par1;
		float f = (float)(1 + j) / 16.0F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }*/

    private void meltNeighborBlocks(World world, int x, int y, int z, Random random) {
        int i = random.nextInt(BlockUtil.facing.length);
        int x0 = x + BlockUtil.facing[i][0];
        int y0 = y + BlockUtil.facing[i][1];
        int z0 = z + BlockUtil.facing[i][2];
        this.meltBlock(world, x0, y0, z0);
    }

    protected void meltBlock(World world, int x, int y, int z) {
        IBlockState state = world.getBlockState(new BlockPos(x, y, z));
        Block block = BlockUtil.getBlock(world, x, y, z);
        Material material = block.getMaterial(state);
        if (material == RTMMaterial.fireproof || material == RTMMaterial.melted || material == Material.AIR || material == Material.LAVA || material == Material.WATER) {
            return;
        } else if (material == Material.GROUND) {
            //world.setBlock(x, y, z, RTMCore.slag, 15, 2);
            this.setFire(world, x, y, z);
        } else if (block == Blocks.BEDROCK) {
            return;
        } else if ((material == Material.ROCK || material == Material.IRON || material == Material.ANVIL)) {
            if (block.getBlockHardness(state, world, new BlockPos(x, y, z)) < 3.5F) {
                //world.setBlock(x, y, z, RTMCore.slag, 15, 2);
                this.setFire(world, x, y, z);
            }
        } else if (material == Material.SAND || material == Material.CLAY) {
            return;
        } else if (block == Blocks.TNT) {
            BlockPos pos = new BlockPos(x, y, z);
            world.setBlockToAir(pos);
            Blocks.TNT.onBlockDestroyedByPlayer(world, pos, Blocks.TNT.getDefaultState());
        } else if (material.getCanBurn()) {
            this.setFire(world, x, y, z);
        } else if (material == Material.GRASS || material == Material.CIRCUITS || material == Material.SPONGE || material == Material.PLANTS || material == Material.CORAL || material == Material.CACTUS || material == Material.WEB || material == Material.GOURD) {
            this.setFire(world, x, y, z);
        } else if (material == Material.FIRE || material == Material.GLASS || material == Material.REDSTONE_LIGHT || material == Material.ICE || material == Material.PACKED_ICE || material == Material.SNOW || material == Material.CRAFTED_SNOW || material == Material.CAKE) {
            world.setBlockToAir(new BlockPos(x, y, z));
        }
        //dragonEgg,portal
    }

    private void setFire(World world, int x, int y, int z) {
        for (int i = 0; i < BlockUtil.facing.length; ++i) {
            int x0 = x - BlockUtil.facing[i][0];
            int y0 = y - BlockUtil.facing[i][1];
            int z0 = z - BlockUtil.facing[i][2];
            if (BlockUtil.isAir(world, x0, y0, z0)) {
                BlockUtil.setBlock(world, x0, y0, z0, Blocks.FIRE, i, 2);
            }
        }
    }
}