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

import jp.ngt.ngtlib.block.BlockCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMFluid;
import jp.ngt.rtm.RTMMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFireBrick extends BlockCustomWithMeta {
    private boolean changeColor;

    public BlockFireBrick(boolean randomTick) {
        super(RTMMaterial.fireproof);
        this.changeColor = randomTick;
        this.setTickRandomly(randomTick);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return this.changeColor ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote && placer instanceof EntityPlayer) {
            if (this == RTMBlock.fireBrick) {
                //((EntityPlayer)placer).addStat(RTMAchievement.startIronMaking, 1);
            } else if (this == RTMBlock.hotStoveBrick) {
                //((EntityPlayer)placer).addStat(RTMAchievement.startIronMaking2, 1);
            }
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (this == RTMBlock.hotStoveBrick) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                int meta = BlockUtil.getMetadata(world, x, y, z);
                int n = 16 - meta;

                for (int i = 0; i < 6; ++i) {
                    int x0 = x + BlockUtil.field_01[i][0];
                    int y0 = y + BlockUtil.field_01[i][1];
                    int z0 = z + BlockUtil.field_01[i][2];
                    Block block = BlockUtil.getBlock(world, x0, y0, z0);
                    if (block == Blocks.AIR) {
                        if (meta > 0 && world.rand.nextInt(n) == 0) {
                            BlockUtil.setBlock(world, x0, y0, z0, RTMFluid.furnaceFire, 15, 2);
                            BlockUtil.setBlock(world, x, y, z, this, 0, 2);
                            break;
                        }
                    } else if (block == RTMFluid.exhaustGas) {
                        int m0 = BlockUtil.getMetadata(world, x0, y0, z0);
                        if (meta < 15 && m0 > 0) {
                            BlockUtil.setBlock(world, x, y, z, this, meta + 1, 2);
                            BlockUtil.setBlock(world, x0, y0, z0, RTMFluid.exhaustGas, m0 - 1, 2);
                            break;
                        }
                    } else if (block == Blocks.LAVA) {
                        if (meta < 15) {
                            BlockUtil.setBlock(world, x, y, z, this, meta + 1, 2);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getHarvestTool(IBlockState state)//Material != rockのとき必須？
    {
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 0;
    }
}