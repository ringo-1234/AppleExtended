package jp.ngt.mcte;

import org.apache.logging.log4j.Level;

import jp.ngt.mcte.block.BlockMinesweeper;
import jp.ngt.mcte.block.BlockMiniature;
import jp.ngt.mcte.block.BlockPort;
import jp.ngt.mcte.block.RSPortSet.PortType;
import jp.ngt.mcte.block.TileEntityMinesweeper;
import jp.ngt.mcte.block.TileEntityMiniature;
import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.mcte.gui.MCTEGuiHandler;
import jp.ngt.mcte.item.ItemEditor;
import jp.ngt.mcte.item.ItemGenerator;
import jp.ngt.mcte.item.ItemMiniature;
import jp.ngt.mcte.item.ItemPainter;
import jp.ngt.mcte.network.PacketEditor;
import jp.ngt.mcte.network.PacketEditorBlockData;
import jp.ngt.mcte.network.PacketExportData;
import jp.ngt.mcte.network.PacketFilter;
import jp.ngt.mcte.network.PacketGenerator;
import jp.ngt.mcte.network.PacketMCTEKey;
import jp.ngt.mcte.network.PacketRenderBlocks;
import jp.ngt.mcte.network.PacketResetSlot;
import jp.ngt.mcte.world.MCTEGenerator;
import jp.ngt.mcte.world.WorldProviderMegaCity;
import jp.ngt.mcte.world.WorldTypePictorial;
import jp.ngt.mcte.world.WorldTypeScript;
import jp.ngt.ngtlib.util.IMod;
import jp.ngt.ngtlib.util.NGTRegHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MCTE.MODID, name  = "MCTerrainEditor", version = MCTE.VERSION)
public class MCTE implements IMod
{
	public static final String MODID = "mcte";
	public static final String VERSION = "2.4.12";//major.minor.rev
	public static final int BUILD_NO = 26;

	@Instance(MODID)
	public static MCTE instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "jp.ngt.mcte.ClientProxy", serverSide = "jp.ngt.mcte.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static short guiIdEditor        = 1700;
	public static short guiIdGenerator     = 1701;
	public static short guiIdPainter       = 1702;
	public static short guiIdItemMiniature = 1703;

	public static final byte KEY_EditMode = 0;
	public static final byte KEY_Clear = 6;
	public static final byte KEY_EditMenu = 7;
	public static final byte KEY_Undo = 8;

	public static final int DIMID_MEGA_CITY = 100;
	public static DimensionType MEGA_CITY;

	public static final String USE_EDITOR = "useEditor";

	public static Block minesweeper;
	public static Block miniature;
	public static Block portIn;
	public static Block portOut;

	public static Item editor;
	public static Item generator;
	public static Item painter;
	public static Item itemMiniature;

	public static float rotationInterval;
	public static int numberOfUndo;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try
		{
			cfg.load();

			Property prop1 = cfg.get("Block", "MiniatureRotationInterval", 15.0D);
			Property prop2 = cfg.get("Editor", "NumberOfUndo", 5);

			rotationInterval = (float)prop1.getDouble();
			numberOfUndo = prop2.getInt();
		}
		catch (Exception e)
		{
			FMLLog.log(Level.ERROR, e, "Error Message");
		}
		finally
		{
			cfg.save();
		}

		minesweeper = NGTRegHandler.register(new BlockMinesweeper(), "minesweeper", "mcte:minesweeper", null, MODID);
		miniature =   NGTRegHandler.register(new BlockMiniature(), "miniature", "mcte:miniature", null, MODID);
		portIn =      NGTRegHandler.register(new BlockPort(PortType.IN), "port_in", "mcte:port_in", CreativeTabs.TOOLS, MODID);
		portOut =     NGTRegHandler.register(new BlockPort(PortType.OUT), "port_out", "mcte:port_out", CreativeTabs.TOOLS, MODID);

		editor =        NGTRegHandler.register(new ItemEditor(), "editor", "mcte:editor", CreativeTabs.TOOLS, MODID);
		generator =     NGTRegHandler.register(new ItemGenerator(), "generator", "mcte:generator", CreativeTabs.TOOLS, MODID);
		painter =       NGTRegHandler.register(new ItemPainter(), "painter", "mcte:painter", CreativeTabs.TOOLS, MODID);
		itemMiniature = NGTRegHandler.register(new ItemMiniature(), "item_miniature", "mcte:itemMiniature", CreativeTabs.TOOLS, MODID);

		GameRegistry.registerTileEntity(TileEntityMinesweeper.class, "TEMinesweeper");
		GameRegistry.registerTileEntity(TileEntityMiniature.class,   "TEMiniature");

		NGTRegHandler.register(EntityEditor.class, "editor", "mcte.e.editor", 0, 256, 3, false, this);

		proxy.preInit();

		NETWORK_WRAPPER.registerMessage(PacketMCTEKey.class,		PacketMCTEKey.class,		0, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketEditor.class,			PacketEditor.class,			1, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketResetSlot.class,		PacketResetSlot.class,		2, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketGenerator.class,		PacketGenerator.class,		3, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketRenderBlocks.class,	PacketRenderBlocks.class,	4, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketEditorBlockData.class,			PacketEditorBlockData.class,			5, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketExportData.class,		PacketExportData.class,		6, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketFilter.class,			PacketFilter.class,			7, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MCTEGuiHandler());

		WorldTypePictorial.init();
		WorldTypeScript.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();

		MEGA_CITY = DimensionType.register("MEGA_CITY", "_mega", DIMID_MEGA_CITY, WorldProviderMegaCity.class, false);
		DimensionManager.registerDimension(DIMID_MEGA_CITY, MEGA_CITY);
		GameRegistry.registerWorldGenerator(new MCTEGenerator(), 5);

		MCTETooltip.init();

		//MinecraftForge.EVENT_BUS.register(StructureManager.INSTANCE);
	}

	@EventHandler
	public void handleServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMCTE());
	}

	@Override
	public String getId()
	{
		return MODID;
	}
}