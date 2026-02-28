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

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.item.ItemBlockCustom;
import jp.ngt.ngtlib.item.ItemColoredBlock;
import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.block.*;
import jp.ngt.rtm.block.BlockMirror.MirrorType;
import jp.ngt.rtm.block.tileentity.*;
import jp.ngt.rtm.electric.*;
import jp.ngt.rtm.electric.TileEntitySignalConverter.*;
import jp.ngt.rtm.rail.*;
import jp.ngt.rtm.rail.BlockMarker.MarkerType;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class RTMBlock {
    public static BlockMarker marker;
    public static BlockMarker markerSwitch;

    public static Block fluorescent;
    public static Block ironPillar;
    public static Block insulator;
    public static Block signal;
    public static Block crossingGate;
    public static Block linePole;
    public static Block connector;
    public static Block signalConverter;
    public static Block solidifiedPigIron;
    public static Block railroadSign;
    public static BlockTurnstile turnstile;
    public static Block point;
    public static Block signboard;
    public static Block trainWorkBench;
    public static Block ticketVendor;
    public static Block mirror;
    public static Block mirrorCube;
    public static Block stationCore;
    public static Block movingMachine;
    public static Block light;
    public static Block plant_ornament;
    public static Block speaker;
    public static Block mechanism;

    public static Block fireBrick;
    public static Block hotStoveBrick;
    public static Block pipe;
    public static Block slot;
    public static Block converterCore;
    public static Block converterBase;
    public static Block steelSlab;
    public static Block steelMaterial;
    public static Block scaffold;
    public static Block scaffoldStairs;
    public static Block framework;
    public static Block paint;
    public static Block flag;
    public static Block decoration;

    public static Block effect;

    public static void init() {
        insulator = NGTRegHandler.register(new BlockInsulator(), "insulator", "rtm:insulator", null, RTMCore.MODID).setHardness(1.0F).setResistance(5.0F);
        signal = NGTRegHandler.register(new BlockSignal(), "signal", "rtm:signal", null, RTMCore.MODID).setHardness(1.0F).setResistance(5.0F);
        crossingGate = NGTRegHandler.register(new BlockCrossingGate(), "crossing", "rtm:crossing", null, RTMCore.MODID).setHardness(1.0F).setResistance(5.0F);
        connector = NGTRegHandler.register(new BlockConnector(), "connector", "rtm:connector", null, RTMCore.MODID);
        railroadSign = NGTRegHandler.register(new BlockRailroadSign(), "rrs", "rtm:rrs", null, RTMCore.MODID).setHardness(1.0F).setResistance(5.0F);
        turnstile = NGTRegHandler.register(new BlockTurnstile(), "turnstile", "rtm:turnstile", null, RTMCore.MODID);
        point = NGTRegHandler.register(new BlockPoint(), "point", "rtm:point", null, RTMCore.MODID).setHardness(3.0F).setResistance(20.0F);
        signboard = NGTRegHandler.register(new BlockSignBoard(), "signboard", "rtm:signboard", null, RTMCore.MODID);
        ticketVendor = NGTRegHandler.register(new BlockTicketVendor(), "ticket_vendor", "rtm:ticket_vendor", null, RTMCore.MODID);
        light = NGTRegHandler.register(new BlockLight(), "light_block", "rtm:right", null, RTMCore.MODID);
        flag = NGTRegHandler.register(new BlockFlag(), "flag", "flag", null, RTMCore.MODID);
        fluorescent = NGTRegHandler.register(new BlockFluorescent(), "fluorescent", "rtm:fluorescent", null, RTMCore.MODID);
        linePole = NGTRegHandler.register(new BlockLinePole(), "linepole", "rtm:linepole", null, RTMCore.MODID);
        scaffold = NGTRegHandler.register(new BlockScaffold(), "scaffold", "rtm:scaffold", null, RTMCore.MODID);
        scaffoldStairs = NGTRegHandler.register(new BlockScaffoldStairs(scaffold), "scaffold_stairs", "rtm:scaffold_stairs", null, RTMCore.MODID);
        framework = NGTRegHandler.register(new BlockLinePole(), "framework", "rtm:framework", null, RTMCore.MODID);
        pipe = NGTRegHandler.register(new BlockPipe(), "pipe", "rtm:pipe", null, RTMCore.MODID);
        plant_ornament = NGTRegHandler.register(new BlockPlant(), "plant_ornament", "plant_ornament", null, RTMCore.MODID);
        speaker = NGTRegHandler.register(new BlockSpeaker(), "speaker", "speaker", null, RTMCore.MODID);
        mechanism = NGTRegHandler.register(new BlockMechanism(), "mechanism", "mechanism", null, RTMCore.MODID);

        converterCore = NGTRegHandler.register(new BlockConverter(true), "converter_core", "rtm:converter_core", null, RTMCore.MODID);
        converterBase = NGTRegHandler.register(new BlockConverter(false), "converter_base", "rtm:converter_base", null, RTMCore.MODID);
        paint = NGTRegHandler.register(new BlockPaint(), "paint", "rtm:paint", null, RTMCore.MODID);
        effect = NGTRegHandler.register(new BlockEffect(), "effect", "rtm:effect", CreativeTabRTM.TOOLS, RTMCore.MODID);
        mirror = NGTRegHandler.register(new BlockMirror(MirrorType.Mono_Panel), "mirror", "rtm:mirror", null, RTMCore.MODID);
        mirrorCube = NGTRegHandler.register(new BlockMirror(MirrorType.Hexa_Cube), "mirror_cube", "rtm:mirror", null, RTMCore.MODID);
        decoration = NGTRegHandler.register(new BlockDecoration(), "decoration", "decoration", null, RTMCore.MODID);

        ironPillar = NGTRegHandler.register(new BlockIronPillar(), "iron_pillar", "rtm:iron_pillar", CreativeTabRTM.RAILWAY, RTMCore.MODID);
        steelSlab = NGTRegHandler.register(new BlockMetalSlab(), "steel_slab", "rtm:steel_slab", null, RTMCore.MODID).setHardness(2.0F).setResistance(10.0F);
        steelMaterial = NGTRegHandler.register(new Block(Material.IRON, MapColor.IRON), "steel_material", "rtm:steel_material", CreativeTabRTM.INDUSTRY, RTMCore.MODID).setHardness(2.0F).setResistance(10.0F);
        stationCore = NGTRegHandler.register(new BlockStation(), "station_core", "rtm:station_core", CreativeTabRTM.RAILWAY, RTMCore.MODID);

        marker = NGTRegHandler.register(new BlockMarker(MarkerType.STANDARD), "marker", "rtm:marker", CreativeTabRTM.RAILWAY, ItemColoredBlock.class, RTMCore.MODID);
        markerSwitch = NGTRegHandler.register(new BlockMarker(MarkerType.SWITCH), "marker_switch", "rtm:marker_switch", CreativeTabRTM.RAILWAY, ItemColoredBlock.class, RTMCore.MODID);
        signalConverter = NGTRegHandler.register(new BlockSignalConverter(), "signal_converter", "rtm:signal_converter", CreativeTabRTM.RAILWAY, ItemBlockCustom.class, RTMCore.MODID);
        trainWorkBench = NGTRegHandler.register(new BlockTrainWorkBench(), "train_workbench", "rtm:train_workbench", CreativeTabRTM.RAILWAY, ItemBlockCustom.class, RTMCore.MODID);
        slot = NGTRegHandler.register(new BlockSlot(), "slot", "rtm:slot", CreativeTabRTM.INDUSTRY, RTMCore.MODID);
        fireBrick = NGTRegHandler.register(new BlockFireBrick(false), "fire_brick", "rtm:fire_brick", CreativeTabRTM.INDUSTRY, RTMCore.MODID);
        hotStoveBrick = NGTRegHandler.register(new BlockFireBrick(true), "hot_stove_brick", "rtm:hot_stove_brick", CreativeTabRTM.INDUSTRY, RTMCore.MODID);
        movingMachine = NGTRegHandler.register(new BlockMovingMachine(), "moving_machine", "rtm:moving_machine", CreativeTabRTM.RAILWAY, ItemBlockCustom.class, RTMCore.MODID);

        RTMRail.init();
        RTMFluid.init();

        GameRegistry.registerTileEntity(TileEntityFluorescent.class, "fluorescent");
        GameRegistry.registerTileEntity(TileEntityInsulator.class, "TEInsulator");
        GameRegistry.registerTileEntity(TileEntitySignal.class, "TESignal");
        GameRegistry.registerTileEntity(TileEntityCrossingGate.class, "TECrossingGate");
        GameRegistry.registerTileEntity(TileEntityConnectorIn.class, "TEConnectorIn");
        GameRegistry.registerTileEntity(TileEntityConnectorOut.class, "TEConnectorOut");

        GameRegistry.registerTileEntity(TileEntitySC_RSIn.class, "TESC_RSIn");
        GameRegistry.registerTileEntity(TileEntitySC_RSOut.class, "TESC_RSOut");
        GameRegistry.registerTileEntity(TileEntitySC_Increment.class, "TESC_Inc");
        GameRegistry.registerTileEntity(TileEntitySC_Decrement.class, "TESC_Dec");
        GameRegistry.registerTileEntity(TileEntitySC_Wireless.class, "TESC_Wireless");

        GameRegistry.registerTileEntity(TileEntityRailroadSign.class, "TERailroadSign");
        GameRegistry.registerTileEntity(TileEntityTurnstile.class, "TETurnstile");
        GameRegistry.registerTileEntity(TileEntityPoint.class, "TEPoint");
        GameRegistry.registerTileEntity(TileEntitySignBoard.class, "TESignBoard");
        GameRegistry.registerTileEntity(TileEntityTrainWorkBench.class, "TETrainWorkBench");
        GameRegistry.registerTileEntity(TileEntityTicketVendor.class, "TETicketVendor");
        GameRegistry.registerTileEntity(TileEntityMirror.class, "TEMirror");
        GameRegistry.registerTileEntity(TileEntityStation.class, "TEStation");
        GameRegistry.registerTileEntity(TileEntityMovingMachine.class, "TEMovingMachine");
        GameRegistry.registerTileEntity(TileEntityLight.class, "TELight");
        GameRegistry.registerTileEntity(TileEntitySpeaker.class, "TESpeaker");
        GameRegistry.registerTileEntity(TileEntityMechanism.class, "TEMechanism");

        GameRegistry.registerTileEntity(TileEntityPipe.class, "TEPipe");
        GameRegistry.registerTileEntity(TileEntityConverter.class, "TEConverter");
        GameRegistry.registerTileEntity(TileEntityConverterCore.class, "TEConverterCore");
        GameRegistry.registerTileEntity(TileEntitySlot.class, "TESlot");
        GameRegistry.registerTileEntity(TileEntityScaffold.class, "TEScaffold");
        GameRegistry.registerTileEntity(TileEntityScaffoldStairs.class, "TEScaffoldStairs");
        GameRegistry.registerTileEntity(TileEntityPaint.class, "TEPaint");
        GameRegistry.registerTileEntity(TileEntityFlag.class, "TEFlag");
        GameRegistry.registerTileEntity(TileEntityPole.class, "TEPole");
        GameRegistry.registerTileEntity(TileEntityPlantOrnament.class, "TEPlantOrnament");
        GameRegistry.registerTileEntity(TileEntityDecoration.class, "TEDecoration");

        GameRegistry.registerTileEntity(TileEntityEffect.class, "TEEffect");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        registerBlockModel(ironPillar, 0, "iron_pillar");
        registerBlockModel(fireBrick, 0, "fire_brick");
        registerBlockModel(hotStoveBrick, 0, "hot_stove_brick");
        registerBlockModel(steelSlab, 0, "steel_slab");
        registerBlockModel(steelMaterial, 0, "steel_material");
        registerBlockModel(stationCore, 0, "station_core");

        registerBlockModel(marker, 0, "marker0");
        registerBlockModel(marker, 1, "marker1");
        registerBlockModel(marker, 2, "marker2");
        registerBlockModel(marker, 3, "marker3");
        registerBlockModel(marker, 4, "marker20");
        registerBlockModel(marker, 5, "marker21");
        registerBlockModel(marker, 6, "marker22");
        registerBlockModel(marker, 7, "marker23");
        registerBlockModel(marker, 8, "marker_fixrtm_merged");
        registerBlockModel(markerSwitch, 0, "marker0");
        registerBlockModel(markerSwitch, 1, "marker1");
        registerBlockModel(markerSwitch, 2, "marker2");
        registerBlockModel(markerSwitch, 3, "marker3");
        registerBlockModel(markerSwitch, 4, "marker20");
        registerBlockModel(markerSwitch, 5, "marker21");
        registerBlockModel(markerSwitch, 6, "marker22");
        registerBlockModel(markerSwitch, 7, "marker23");
        registerBlockModel(markerSwitch, 8, "marker_fixrtm_merged");
        registerBlockModels(signalConverter, "signal_converter", 0, 1, 2, 3, 4);
        registerBlockModels(trainWorkBench, "train_workbench", 0, 1);
        registerBlockModel(slot, 0, "slot");
        registerBlockModel(movingMachine, 0, "moving_machine");
        registerBlockModel(movingMachine, 1, "vehicle_generator");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailNormalCore.class, RenderLargeRail.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailSwitchCore.class, RenderLargeRail.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurnTableCore.class, new RenderTurntable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInsulator.class, RenderElectricalWiring.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConnector.class, RenderElectricalWiring.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySignal.class, new RenderSignal());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailroadSign.class, new RenderRailroadSign());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySignBoard.class, new RenderSignBoard());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEffect.class, new RenderEffect());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMarker.class, RenderMarkerBlock.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStation.class, new RenderStation());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMovingMachine.class, new RenderMovingMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMechanism.class, new RenderMechanism());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurnstile.class, RenderMachine.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoint.class, RenderMachine.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrossingGate.class, RenderMachine.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTicketVendor.class, RenderMachine.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLight.class, RenderMachine.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpeaker.class, RenderMachine.INSTANCE);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluorescent.class, new RenderOrnament<TileEntityFluorescent>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderOrnament<TileEntityPipe>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityScaffoldStairs.class, new RenderOrnament<TileEntityScaffoldStairs>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityScaffold.class, new RenderOrnament<TileEntityScaffold>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPole.class, new RenderOrnament<TileEntityPole>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlantOrnament.class, new RenderOrnament<TileEntityPlantOrnament>());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConverterCore.class, new RenderConverter());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaint.class, new RenderPaint());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class, new RenderFlag());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecoration.class, new RenderDecoration());

        RTMFluid.initClient();

        NGTUtilClient.registerBuildinModel(RTMRail.TURNTABLE_CORE, true);
        NGTUtilClient.registerBuildinModel(RTMRail.largeRailCore, true);
        NGTUtilClient.registerBuildinModel(RTMRail.largeRailSwitchCore, true);
        NGTUtilClient.registerBuildinModel(RTMRail.largeRailBase, true);
        NGTUtilClient.registerBuildinModel(RTMRail.largeRailSwitchBase, true);

        NGTUtilClient.registerBuildinModel(RTMBlock.fluorescent, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.turnstile, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.railroadSign, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.scaffold, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.insulator, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.scaffoldStairs, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.mirrorCube, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.point, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.effect, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.decoration, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.plant_ornament, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.pipe, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.signboard, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.ticketVendor, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.linePole, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.mirror, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.signal, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.crossingGate, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.connector, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.light, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.converterBase, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.converterCore, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.framework, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.flag, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.paint, true);
        NGTUtilClient.registerBuildinModel(RTMBlock.mechanism, true);
    }

    @SideOnly(Side.CLIENT)
    public static void initClient2() {
        BlockColors colors = NGTUtilClient.getMinecraft().getBlockColors();

        colors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            int meta = BlockUtil.getMetadata(world, pos);
            int c0 = (meta << 4) & 255;
            int c1 = 0xFFFFFF - (c0 << 8) - c0;
            return c1;
        }, hotStoveBrick);

        colors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            BlockMarker block = (BlockMarker) state.getBlock();
            return block.markerType.color;
        }, marker, markerSwitch);

        colors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            int meta = BlockUtil.getMetadata(world, pos);
            int c0 = (meta << 4) & 255;
            int c1 = 0xffffff - (c0 << 8) - c0;
            return c1;
        }, steelSlab);
    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockModel(Block block, int meta, String name) {
        RTMItem.registerItemModel(Item.getItemFromBlock(block), meta, name);
    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockModels(Block block, String name, int... metas) {
        for (int i : metas) {
            RTMItem.registerItemModel(Item.getItemFromBlock(block), i, name + i);
        }
    }
}