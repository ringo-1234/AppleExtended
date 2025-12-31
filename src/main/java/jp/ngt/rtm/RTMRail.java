package jp.ngt.rtm;

import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.BlockLargeRailCore;
import jp.ngt.rtm.rail.BlockLargeRailSwitchBase;
import jp.ngt.rtm.rail.BlockLargeRailSwitchCore;
import jp.ngt.rtm.rail.BlockTurntableCore;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailNormalCore;
import jp.ngt.rtm.rail.TileEntityLargeRailSwitchBase;
import jp.ngt.rtm.rail.TileEntityLargeRailSwitchCore;
import jp.ngt.rtm.rail.TileEntityMarker;
import jp.ngt.rtm.rail.TileEntityTurnTableCore;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class RTMRail
{
	public static Block largeRailBase;
	public static Block largeRailCore;
	public static Block largeRailSwitchBase;
	public static Block largeRailSwitchCore;
	public static Block TURNTABLE_CORE;

	public static void init()
	{
		largeRailBase = NGTRegHandler.register(new BlockLargeRailBase(), "large_rail_base", "rtm:LRBase", null, RTMCore.MODID);
		largeRailCore = NGTRegHandler.register(new BlockLargeRailCore(), "large_rail_core", "rtm:LRCore", null, RTMCore.MODID);
		largeRailSwitchBase = NGTRegHandler.register(new BlockLargeRailSwitchBase(), "large_rail_switch_base", "rtm:LRSBase", null, RTMCore.MODID);
		largeRailSwitchCore = NGTRegHandler.register(new BlockLargeRailSwitchCore(), "large_rail_switch_core", "rtm:LRSCore", null, RTMCore.MODID);
		TURNTABLE_CORE = NGTRegHandler.register(new BlockTurntableCore(), "turntable_core", "rtm:turntable_core", null, RTMCore.MODID);

		GameRegistry.registerTileEntity(TileEntityLargeRailBase.class, 		 "TERailBase");
		GameRegistry.registerTileEntity(TileEntityLargeRailNormalCore.class, "TERailCore");
		GameRegistry.registerTileEntity(TileEntityLargeRailSwitchBase.class, "TERailSwitchBase");
		GameRegistry.registerTileEntity(TileEntityLargeRailSwitchCore.class, "TERailSwitchCore");
		GameRegistry.registerTileEntity(TileEntityTurnTableCore.class,       "TETurntableCore");
		GameRegistry.registerTileEntity(TileEntityMarker.class, 			 "TEMarker");
	}
}