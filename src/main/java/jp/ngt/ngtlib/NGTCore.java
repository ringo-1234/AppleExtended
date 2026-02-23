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

package jp.ngt.ngtlib;

import java.io.IOException;

import org.apache.logging.log4j.Level;

import jp.ngt.ngtlib.command.CommandNGT;
import jp.ngt.ngtlib.command.CommandPermit;
import jp.ngt.ngtlib.command.CommandProtection;
import jp.ngt.ngtlib.command.CommandUsage;
import jp.ngt.ngtlib.event.NGTEventHandler;
import jp.ngt.ngtlib.item.ItemProtectionKey;
import jp.ngt.ngtlib.item.craft.RecipeManager;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.ngtlib.network.PacketNBTHandlerClient;
import jp.ngt.ngtlib.network.PacketNBTHandlerServer;
import jp.ngt.ngtlib.network.PacketNotice;
import jp.ngt.ngtlib.network.PacketNoticeHandlerClient;
import jp.ngt.ngtlib.network.PacketNoticeHandlerServer;
import jp.ngt.ngtlib.network.PacketProtection;
import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.ngtlib.util.Usage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = NGTCore.MODID, name = "NGTLib", version = NGTCore.VERSION)
@EventBusSubscriber
public class NGTCore
{
	public static final String MODID = "ngtlib";
	public static final String VERSION = "2.4.21";//major.minor.rev
	public static final int BUILD_NO = 38;

	@Instance(MODID)
	public static NGTCore instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "jp.ngt.ngtlib.ClientProxy", serverSide = "jp.ngt.ngtlib.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static String shaderModName;
	public static boolean versionCheck;
	public static boolean debugLog;

	public static ItemProtectionKey protection_key;

	public static final String TWI_KEY = "koqhu1xcba0f33bdhZEvhDa2GG4PPzO6kRHAxRYs01kz6vc462WRja0SAt6f1ymVnxy63OI6ihT7juPjNvzp8W7qLV7F5jGzVOog";
	public static final String CERTIFICATION_URL = "https://dl.dropboxusercontent.com/s/tukcqsaylqfhx7j/key.ngt";
	public static final String LIB_INSTALLERTION_URL = "https://dl.dropboxusercontent.com/s/nqjqg4qur7jriwz/lib_installation.txt";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try
		{
			cfg.load();

			Property modPro1 = cfg.get("Mod", "version check", true);
			modPro1.setComment("");
			versionCheck = modPro1.getBoolean();

			Property modPro2 = cfg.get("Mod", "shadersmod name", "ShadersModCore");
			modPro2.setComment("File name of ShadersMod");
			shaderModName = modPro2.getString();

			Property modPro3 = cfg.get("Mod", "debug log", false);
			modPro3.setComment("");
			debugLog = modPro3.getBoolean();
		}
		catch (Exception e)
		{
			FMLLog.log(Level.ERROR, e, "Error Message");
		}
		finally
		{
			cfg.save();
		}

		protection_key = NGTRegHandler.register(new ItemProtectionKey(), "protection_key", "protection_key", null, MODID);

		proxy.preInit();
		NETWORK_WRAPPER.registerMessage(PacketNoticeHandlerClient.class, PacketNotice.class, 0, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketNoticeHandlerServer.class, PacketNotice.class, 1, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketNBTHandlerClient.class,    PacketNBT.class,    2, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketNBTHandlerServer.class,    PacketNBT.class,    3, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketProtection.class,          PacketProtection.class, 4, Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(RecipeManager.INSTANCE);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
		MinecraftForge.EVENT_BUS.register(new NGTEventHandler());

		Usage.INSTANCE.add(protection_key, -1, "usage.item.protection_key");

		try
		{
			PermissionManager.INSTANCE.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandNGT());
		event.registerServerCommand(new CommandProtection());
		event.registerServerCommand(new CommandPermit());
		event.registerServerCommand(new CommandUsage());
	}
}
