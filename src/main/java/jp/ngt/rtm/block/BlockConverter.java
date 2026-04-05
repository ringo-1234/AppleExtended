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
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityConverter;
import jp.ngt.rtm.block.tileentity.TileEntityConverterCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockConverter extends BlockContainerCustom {
    private static int[][] pos_iron = {{-3, 0, -1}, {-3, 0, 0}, {-3, 0, 1}, {3, 0, -1}, {3, 0, 0}, {3, 0, 1},
            {-3, 1, -1}, {-3, 1, 0}, {-3, 1, 1}, {-1, 1, -1}, {-1, 1, 0}, {-1, 1, 1}, {0, 1, -1}, {0, 1, 0}, {0, 1, 1}, {1, 1, -1}, {1, 1, 0}, {1, 1, 1}, {3, 1, -1}, {3, 1, 0}, {3, 1, 1},
            {-3, 2, -1}, {-3, 2, 0}, {-3, 2, 1}, {-2, 2, -1}, {-2, 2, 0}, {-2, 2, 1}, {-1, 2, -2}, {-1, 2, 2}, {0, 2, -2}, {0, 2, 2}, {1, 2, -2}, {1, 2, 2}, {2, 2, -1}, {2, 2, 0}, {2, 2, 1}, {3, 2, -1}, {3, 2, 0}, {3, 2, 1},
            {-3, 3, -1}, {-3, 3, 0}, {-3, 3, 1}, {-2, 3, -1}, {-2, 3, 0}, {-2, 3, 1}, {-1, 3, -2}, {-1, 3, 2}, {0, 3, -2}, {0, 3, 2}, {1, 3, -2}, {1, 3, 2}, {2, 3, -1}, {2, 3, 0}, {2, 3, 1}, {3, 3, -1}, {3, 3, 0}, {3, 3, 1},
            {-3, 4, 0}, {-2, 4, -1}, {-2, 4, 0}, {-2, 4, 1}, {-1, 4, -2}, {-1, 4, 2}, {0, 4, -2}, {0, 4, 2}, {1, 4, -2}, {1, 4, 2}, {2, 4, -1}, {2, 4, 0}, {2, 4, 1}, {3, 4, 0},
            {-2, 5, -1}, {-2, 5, 0}, {-2, 5, 1}, {-1, 5, -2}, {-1, 5, 2}, {0, 5, -2}, {0, 5, 2}, {0, 5, 3}, {1, 5, -2}, {1, 5, 2}, {2, 5, -1}, {2, 5, 0}, {2, 5, 1},
            {-2, 6, -1}, {-2, 6, 0}, {-2, 6, 1}, {-1, 6, -2}, {-1, 6, 2}, {0, 6, -2}, {0, 6, 2}, {1, 6, -2}, {1, 6, 2}, {2, 6, -1}, {2, 6, 0}, {2, 6, 1},
            {-1, 7, -1}, {-1, 7, 0}, {-1, 7, 1}, {0, 7, -1}, {0, 7, 1}, {1, 7, -1}, {1, 7, 0}, {1, 7, 1}};

    private static int[][] pos_brick = {{-1, 0, -1}, {-1, 0, 0}, {-1, 0, 1}, {0, 0, -1}, {0, 0, 1}, {1, 0, -1}, {1, 0, 0}, {1, 0, 1}};

    public final boolean isCore;

    public BlockConverter(boolean par1) {
        super(Material.ROCK);
        this.isCore = par1;
        this.setLightLevel(par1 ? 1.0F : 0.0F);
        if (par1) {
            this.setAABB(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F));
        }
        this.setHardness(2.0F);
        this.setResistance(10.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        if (this.isCore) {
            return new TileEntityConverterCore();
        } else {
            return new TileEntityConverter();
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
        if (this == RTMBlock.converterCore) {
            entity.attackEntityFrom(DamageSource.LAVA, 1.0F);
            entity.setFire(1);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileEntity tile0 = BlockUtil.getTileEntity(world, pos);
            if (tile0 instanceof TileEntityConverter) {
                TileEntityConverterCore tile1 = ((TileEntityConverter) tile0).getCore();
                int tx = tile1.getX();
                int ty = tile1.getY();
                int tz = tile1.getZ();
                if (tile1 != null && BlockUtil.getBlock(world, tx, ty, tz) == RTMBlock.converterCore) {
                    createConverter(world, tx, ty - 4, tz, tile1.getDirection(), true);
                    //104x2
                    spawnAsEntity(world, new BlockPos(tx, ty - 4, tz), new ItemStack(RTMItem.steel_ingot, 64, 0));
                    spawnAsEntity(world, new BlockPos(tx, ty - 4, tz), new ItemStack(RTMItem.steel_ingot, 64, 0));
                    spawnAsEntity(world, new BlockPos(tx, ty - 4, tz), new ItemStack(RTMItem.steel_ingot, 64, 0));
                    spawnAsEntity(world, new BlockPos(tx, ty - 4, tz), new ItemStack(RTMItem.steel_ingot, 16, 0));
                }
            }
        }
    }

    /**
     * 転炉を生成できるかチェック
     *
     * @return 0~3 (-1で不可)
     */
    public static byte shouldCreateConverter(World world, int x, int y, int z) {
        if (BlockUtil.getBlock(world, x, y + 2, z) != RTMBlock.fireBrick) {
            return -1;
        }

        for (int i = 2; i < 7; ++i)//check:Brick
        {
            for (int j = 0; j < pos_brick.length; ++j) {
                if (BlockUtil.getBlock(world, x + pos_brick[j][0], y + i, z + pos_brick[j][2]) != RTMBlock.fireBrick) {
                    return -1;
                }
            }
        }

        boolean flag;
        for (int i = 0; i < 4; ++i)//check:Iron
        {
            flag = true;
            for (int j = 0; j < pos_iron.length; ++j) {
                int[] p0 = BlockUtil.rotateBlockPos((byte) i, pos_iron[j][0], pos_iron[j][1], pos_iron[j][2]);
                if (BlockUtil.getBlock(world, x + p0[0], y + p0[1], z + p0[2]) != RTMBlock.steelMaterial) {
                    flag = false;
                }
            }

            if (flag) {
                return (byte) i;
            }
        }

        return -1;
    }

    public static void createConverter(World world, int x, int y, int z, byte dir, boolean setAir) {
        if (setAir) {
            BlockUtil.setAir(world, x, y + 4, z);
            BlockUtil.setAir(world, x, y + 2, z);
        } else {
            BlockUtil.setBlock(world, x, y + 4, z, RTMBlock.converterCore, 0, 2);
            TileEntityConverterCore tile = (TileEntityConverterCore) BlockUtil.getTileEntity(world, x, y + 4, z);
            tile.setDirection(dir);

            setConverterBlock(world, x, y + 2, z, x, y + 4, z);
        }

        for (int i = 2; i < 7; ++i) {
            for (int j = 0; j < pos_brick.length; ++j) {
                if (setAir) {
                    BlockUtil.setAir(world, x + pos_brick[j][0], y + i, z + pos_brick[j][2]);
                } else {
                    setConverterBlock(world, x + pos_brick[j][0], y + i, z + pos_brick[j][2], x, y + 4, z);
                }
            }
        }

        for (int j = 0; j < pos_iron.length; ++j) {
            int[] p0 = BlockUtil.rotateBlockPos(dir, pos_iron[j][0], pos_iron[j][1], pos_iron[j][2]);
            if (setAir) {
                BlockUtil.setAir(world, x + p0[0], y + p0[1], z + p0[2]);
            } else {
                setConverterBlock(world, x + p0[0], y + p0[1], z + p0[2], x, y + 4, z);
            }
        }
    }

    private static void setConverterBlock(World world, int x, int y, int z, int coreX, int coreY, int coreZ) {
        BlockUtil.setBlock(world, x, y, z, RTMBlock.converterBase, 0, 2);
        TileEntityConverter tile = (TileEntityConverter) BlockUtil.getTileEntity(world, x, y, z);
        tile.setCorePos(coreX, coreY, coreZ);
    }
}