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

package jp.ngt.rtm.item;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.entity.fluid.EntityFluid;
import jp.ngt.rtm.entity.fluid.FluidType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public final class ItemPaddle extends ItemCustom {
    /**
     * [[煙突始点],[煙突終点],[天井始点],[天井終点],[熱源始点],[熱源終点]]
     */
    private static final int[][][] FURNACE_PATTERN = {
            {{-3, 1, 0}, {-2, 1, 0}, {0, 1, 0}, {3, 1, 0}, {2, -1, 0}, {3, 0, 0}},
            {{2, 1, 0}, {3, 1, 0}, {-3, 1, 0}, {0, 1, 0}, {-3, -1, 0}, {-2, 0, 0}},
            {{0, 1, -3}, {0, 1, -2}, {0, 1, 0}, {0, 1, 3}, {0, -1, 2}, {0, 0, 3}},
            {{0, 1, 2}, {0, 1, 3}, {0, 1, -3}, {0, 1, 0}, {0, -1, -3}, {0, 0, -2}}
    };
    //▲▲□※□■■
    //▲▲□□□□□
    //※銑鉄、▲熱源、■煙突

    public ItemPaddle() {
        super();
        this.maxStackSize = 1;
        this.setMaxDamage(ToolMaterial.IRON.getMaxUses());
    }

    @Override
    protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder) {
        return holder.setItemStack(holder.getItemStack().copy()).success();//サバイバルで右クリック動作を強制させる
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockState block) {
        return false;
    }

    private static boolean stir(EntityPlayer player, EntityFluid fluid) {
        BlockPos pos = fluid.getPosition();
        float temp = checkFurnaceEnv(fluid.world, player, pos.getX(), pos.getY(), pos.getZ());
        if (temp > 0.0F) {
            if (temp > fluid.getTemperature()) {
                fluid.setTemperture(temp);
            }

            if (temp >= FluidType.STEEL.meltingPoint) {
                if (player.getEntityWorld().rand.nextInt(4) == 0) {
                    NGTLog.sendChatMessage(player, "message.paddle.get_steel");
                    fluid.setFluidType(FluidType.STEEL);
                    return true;
                }
            } else {
                NGTLog.sendChatMessage(player, "Low temperature (%5.1f)", temp);
            }
        }
        //NGTLog.sendChatMessage(player, "message.paddle.incorrect_design", new Object[]{});
        return false;
    }

    /**
     * 炉が規定構造にマッチしているか
     */
    private static float checkFurnaceEnv(World world, EntityPlayer player, int x, int y, int z) {
        float temp = 0.0F;
        int chimney = -1;
        int ceiling = -1;
        int coke = -1;
        int heatSource = -1;
        for (int i = 0; i < FURNACE_PATTERN.length; ++i) {
            boolean fChimney = checkChimney(world, x, y, z, FURNACE_PATTERN[i][0], FURNACE_PATTERN[i][1]);
            chimney = (chimney == 1) ? chimney : (fChimney ? 1 : 0);
            if (fChimney) {
                boolean fCeiling = checkCeiling(world, x, y, z, FURNACE_PATTERN[i][2], FURNACE_PATTERN[i][3]);
                ceiling = (ceiling == 1) ? ceiling : (fCeiling ? 1 : 0);
                if (fCeiling) {
                    boolean fCoke = checkCokeNotAdjacent(world, x, y, z);
                    coke = (coke == 1) ? coke : (fCoke ? 1 : 0);
                    if (fCoke) {
                        float tempNew = checkHeatSource(world, x, y, z, FURNACE_PATTERN[i][4], FURNACE_PATTERN[i][5]);
                        boolean fHeatSource = tempNew > 0;
                        heatSource = (heatSource == 1) ? heatSource : (fHeatSource ? 1 : 0);
                        if (fHeatSource) {
                            if (tempNew > temp) {
                                temp = tempNew;
                            }
                        }
                    }
                }
            }
        }

        NGTLog.sendChatMessage(player, "Chimney:%s, Ceiling:%s, Coke:%s, HeatSource:s%",
                getMessage(chimney), getMessage(ceiling), getMessage(coke), getMessage(heatSource));
        return temp;
    }

    private static String getMessage(int state) {
        return state == 1 ? "OK" : (state == 0 ? "NG" : "-");
    }

    /**
     * コークスに接していないか
     */
    private static boolean checkCokeNotAdjacent(World world, int x, int y, int z) {
        List<EntityFluid> list = world.getEntitiesWithinAABB(EntityFluid.class,
                new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
        for (EntityFluid fluid : list) {
            if (fluid.getFluidType() == FluidType.COKE) {
                return false;
            }
        }
        return true;
    }

    /**
     * いずれか1地点が空に面しているか
     */
    private static boolean checkChimney(World world, int x, int y, int z, int[] start, int[] end) {
        boolean flag = false;
        for (int i = start[0]; i <= end[0]; ++i) {
            for (int j = start[1]; j <= end[1]; ++j) {
                for (int k = start[2]; k <= end[2]; ++k) {
                    flag |= world.canSeeSky(new BlockPos(x + i, y + j, z + k));
                }
            }
        }
        return flag;
    }

    /**
     * すべての地点が空に面していないか
     */
    private static boolean checkCeiling(World world, int x, int y, int z, int[] start, int[] end) {
        for (int i = start[0]; i <= end[0]; ++i) {
            for (int j = start[1]; j <= end[1]; ++j) {
                for (int k = start[2]; k <= end[2]; ++k) {
                    if (world.canSeeSky(new BlockPos(x + i, y + j, z + k))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 熱源の温度の平均値、ない場合は0.0
     */
    private static float checkHeatSource(World world, int x, int y, int z, int[] start, int[] end) {
        List<EntityFluid> list = world.getEntitiesWithinAABB(EntityFluid.class,
                new AxisAlignedBB(x + start[0], y + start[1], z + start[2], x + end[0] + 1, y + end[1] + 1, z + end[2] + 1));
        double sum = 0.0D;
        int count = 0;
        for (EntityFluid fluid : list) {
            if (fluid.getFluidType() == FluidType.COKE) {
                sum += fluid.getTemperature();
                ++count;
            }
        }
        return count > 0 ? (float) (sum / (double) count) : 0.0F;
    }

    /**
     * @param dir +1で押す、-1で引く
     *
     */
    public static void pushPull(EntityPlayer player, EntityFluid fluid, float dir) {
        if (fluid.getFluidType() == FluidType.PIG_IRON) {
            if (stir(player, fluid)) {
                return;
            }
        }
        double x = fluid.posX - player.posX;
        double z = fluid.posZ - player.posZ;
        double len = NGTMath.firstSqrt(x * x + z * z);
        float f0 = dir * 0.1F;
        fluid.motionX += (x / len) * f0;
        fluid.motionZ += (z / len) * f0;
    }
}