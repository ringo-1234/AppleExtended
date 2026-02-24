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

package jp.ngt.rtm;

import org.apache.logging.log4j.Level;

import jp.ngt.ngtlib.util.IMod;
import jp.ngt.rtm.command.RTMCommand;
import jp.ngt.rtm.event.RTMEventHandler;
import jp.ngt.rtm.gui.RTMGuiHandler;
import jp.ngt.rtm.msims.MSIMS;
import jp.ngt.rtm.world.RTMChunkManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = RTMCore.MODID, name = "RealTrainMod", version = RTMCore.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public final class RTMCore implements IMod
{
	public static final String MODID = "rtm";
	public static final String VERSION = "2.4.24";
	public static final int BUILD_NO = 43;

	@Instance(MODID)
	public static RTMCore instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "jp.ngt.rtm.ClientProxy", serverSide = "jp.ngt.rtm.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static short guiIdSelectEntityModel       = getNextGuiID();
	public static short guiIdSelectTileEntityModel   = getNextGuiID();
	public static short guiIdSelectItemModel         = getNextGuiID();
	public static short guiIdSelectTileEntityTexture = getNextGuiID();
	public static short guiIdSelectItemTexture       = getNextGuiID();
	public static short guiIdSignboard = getNextGuiID();
	public static short guiIdFreightCar = getNextGuiID();
	public static short guiIdItemContainer = getNextGuiID();
	public static short guiIdTrainControlPanel = getNextGuiID();
	public static short guiIdTrainWorkBench = getNextGuiID();
	public static short guiIdSignalConverter = getNextGuiID();
	public static short guiIdTicketVendor = getNextGuiID();
	public static short guiIdStation = getNextGuiID();
	public static short guiIdPaintTool = getNextGuiID();
	public static short guiIdMovingMachine = getNextGuiID();
	public static short guiIdNPC = getNextGuiID();
	public static short guiIdMotorman = getNextGuiID();
	public static short guiIdRailMarker = getNextGuiID();
	public static short guiIdCamera = getNextGuiID();
	public static short guiIdConvertModel = getNextGuiID();
	public static short guiIdDecoration = getNextGuiID();
	public static short guiIdSpeaker = getNextGuiID();

	public static final byte KEY_Forward = 0;
	public static final byte KEY_Back = 1;
	public static final byte KEY_Horn = 2;
	public static final byte KEY_Chime = 3;
	public static final byte KEY_ControlPanel = 4;
	public static final byte KEY_Fire = 5;
	public static final byte KEY_ATS = 6;
	public static final byte KEY_LEFT = 7;
	public static final byte KEY_RIGHT = 8;
	public static final byte KEY_JUMP = 9;
	public static final byte KEY_SNEAK = 10;
	public static final byte KEY_EB = 11;

	public static final String EDIT_VEHICLE = "editVehicle";
	public static final String USE_RAZER = "useRazer";
	public static final String USE_GUN = "useGun";
	public static final String USE_CANNON = "useCannon";
	public static final String EDIT_RAIL = "editRail";
	public static final String DRIVE_TRAIN = "driveTrain";
	public static final String CHANGE_MODEL = "changeModel";
	public static final String EDIT_ORNAMENT = "editOrnament";

	public static float trainSoundVol;
	public static float gunSoundVol;
	public static short railGeneratingDistance;
	public static short railGeneratingHeight;
	public static short markerDisplayDistance;
	public static boolean gunBreakBlock;
	public static boolean deleteBat;
	public static boolean useServerModelPack;
	public static boolean versionCheck;
	public static int mirrorTextureSize;
	public static boolean smoothing;
	public static byte mirrorRenderingFrequency;
	public static int connectorSearchRange;

	public static final int PACKET_SIZE = 512;
	public static final int ATOMIC_BOM_META = 2;

	public static final String AD_URL = "https://dl.dropboxusercontent.com/s/7rpcd4ycxjfnwrr/advertisement.json";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try
		{
			cfg.load();
			Property soundPro1 = cfg.get("Sound", "sound train", 100);
			soundPro1.setComment("Train sound volume. (0 ~ 100)");
			Property soundPro3 = cfg.get("Sound", "sound gun", 100);
			soundPro3.setComment("Gun sound volume. (0 ~ 100)");

			Property railPro1 = cfg.get("Rail", "GeneratingDistance", 64);
			railPro1.setComment("Distance for generating a rail. (default:64, recomended max value:256, It depends on server side)");
			Property railPro2 = cfg.get("Rail", "GeneratingHeight", 8);
			railPro2.setComment("Height for generating a rail. (default:8, recomended max value:256)");
			Property railPro3 = cfg.get("Rail", "MarkerDisplayDistance", 100);
			railPro3.setComment("(default length:100)");

			Property itemPro1 = cfg.get("Item", "Gun Break Block", true);
			Property entityPro1 = cfg.get("Entity", "delete bat", false);
			entityPro1.setComment("Delete bat");
			Property modelPro1 = cfg.get("Model", "use ServerModelPack", false);
			modelPro1.setComment("Download ModelPacks from Server (or Permit download ModelPacks).");
			Property modelPro2 = cfg.get("Model", "do smoothing", true);
			Property modPro1 = cfg.get("Mod", "version check", true);
			modPro1.setComment("");
			Property blockPro1 = cfg.get("Block", "mirror texture size", 512);
			blockPro1.setComment("FrameBuffer size for mirror. (Recomended size : 256~2048)");
			Property blockPro2 = cfg.get("Block", "mirror render frequency", 1);
			blockPro2.setComment("Frequency of rendering mirror. (1 : Full tick)");
			Property blockPro3 = cfg.get("Block", "connector search range", 32);
			blockPro3.setComment("Range of searching connector on right click.");

			trainSoundVol			 = (float)soundPro1.getInt() / 100.0F;
			gunSoundVol				 = (float)soundPro3.getInt() / 100.0F;
			railGeneratingDistance	 = (short)railPro1.getInt();
			railGeneratingHeight	 = (short)railPro2.getInt();
			markerDisplayDistance	 = (short)railPro3.getInt();
			gunBreakBlock			 = itemPro1.getBoolean();
			deleteBat				 = entityPro1.getBoolean();
			useServerModelPack		 = modelPro1.getBoolean();
			smoothing				 = modelPro2.getBoolean();
			versionCheck			 = modPro1.getBoolean();
			mirrorTextureSize		 = blockPro1.getInt();
			mirrorRenderingFrequency = (byte)blockPro2.getInt();
			connectorSearchRange     = blockPro3.getInt();
		}
		catch (Exception e)
		{
			FMLLog.log(Level.ERROR, e, "Error Message");
		}
		finally
		{
			cfg.save();
		}

		RTMResource.init();
		RTMBlock.init();
		RTMItem.init();
		RTMEntity.init(this);
		RTMRecipe.init();
		RTMPacket.init();
		RTMAdvancement.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new RTMGuiHandler());

		proxy.preInit();

		ForgeChunkManager.setForcedChunkLoadingCallback(this, RTMChunkManager.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RTMChunkManager.INSTANCE);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
		RTMTooltip.init();

		MinecraftForge.EVENT_BUS.register(new RTMEventHandler());
	}

	@EventHandler
	public void complete(FMLLoadCompleteEvent event)
	{
		proxy.complete();
		MSIMS.INSTANCE.loadData();
	}

	@EventHandler
	public void handleServerStarting(FMLServerStartingEvent event)
	{
		RTMCommand.init(event);
	}

	private static short guiId;

	private static short getNextGuiID()
	{
		return guiId++;
	}

	@Override
	public String getId()
	{
		return MODID;
	}
}