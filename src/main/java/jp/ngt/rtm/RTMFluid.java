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

package jp.ngt.rtm;

import jp.ngt.ngtlib.block.BlockLiquidBase;
import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.block.BlockFurnaceFire;
import jp.ngt.rtm.block.BlockMeltedMetal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class RTMFluid {
    public static Fluid fluidFurnaceFire;
    public static Fluid fluidExhaustGas;
    public static Fluid fluidLiquefiedPigIron;
    public static Fluid fluidLiquefiedSteel;
    public static Fluid fluidSlag;

    public static BlockLiquidBase furnaceFire;
    public static BlockLiquidBase exhaustGas;
    public static BlockLiquidBase liquefiedPigIron;
    public static BlockLiquidBase liquefiedSteel;
    public static BlockLiquidBase slag;

    public static void init() {
        fluidFurnaceFire = registerFluid("furnace_fire", "blocks/furnace_fire", "blocks/furnaceF_fire");
        fluidExhaustGas = registerFluid("exhaust_gas", "blocks/exhaust_gas", "blocks/exhaust_gas");
        fluidLiquefiedPigIron = registerFluid("pig_iron_l", "blocks/pig_iron_l", "blocks/pig_iron_l");
        fluidLiquefiedSteel = registerFluid("steel_l", "blocks/steel_l", "blocks/steel_l");
        fluidSlag = registerFluid("slag", "blocks/slag", "blocks/slag");

        furnaceFire = NGTRegHandler.register(new BlockFurnaceFire(fluidFurnaceFire, true), "furnace_fire", "rtm:furnace_fire", null, RTMCore.MODID);
        exhaustGas = NGTRegHandler.register(new BlockFurnaceFire(fluidExhaustGas, false), "exhaust_gas", "rtm:exhaust_gas", null, RTMCore.MODID);
        liquefiedPigIron = NGTRegHandler.register(new BlockMeltedMetal(fluidLiquefiedPigIron), "pig_iron_l", "rtm:pig_iron_l", null, RTMCore.MODID);
        liquefiedSteel = NGTRegHandler.register(new BlockMeltedMetal(fluidLiquefiedSteel), "steel_l", "rtm:steel_l", null, RTMCore.MODID);
        slag = NGTRegHandler.register(new BlockMeltedMetal(fluidSlag), "slag", "rtm:slag", null, RTMCore.MODID);
    }

    public static Fluid registerFluid(String name, String stillIcon, String flowIcon) {
        Fluid fluid = new Fluid(name, new ResourceLocation(RTMCore.MODID, stillIcon), new ResourceLocation(RTMCore.MODID, flowIcon));
        FluidRegistry.registerFluid(fluid);
        return fluid;
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        registerState(furnaceFire, new ModelResourceLocation(RTMCore.MODID + ":furnace_fire", "fluid"));
        registerState(exhaustGas, new ModelResourceLocation(RTMCore.MODID + ":exhaust_gas", "fluid"));
        registerState(liquefiedPigIron, new ModelResourceLocation(RTMCore.MODID + ":pig_iron_l", "fluid"));
        registerState(liquefiedSteel, new ModelResourceLocation(RTMCore.MODID + ":steel_l", "fluid"));
        registerState(slag, new ModelResourceLocation(RTMCore.MODID + ":slag", "fluid"));

        NGTUtilClient.registerNoModelBlockAsItem(RTMFluid.furnaceFire);
        NGTUtilClient.registerNoModelBlockAsItem(RTMFluid.exhaustGas);
        NGTUtilClient.registerNoModelBlockAsItem(RTMFluid.liquefiedPigIron);
        NGTUtilClient.registerNoModelBlockAsItem(RTMFluid.liquefiedSteel);
        NGTUtilClient.registerNoModelBlockAsItem(RTMFluid.slag);
    }

    @SideOnly(Side.CLIENT)
    public static void registerState(Block block, final ModelResourceLocation resourceLocation) {
        ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState par1) {
                return resourceLocation;
            }
        });
    }
}