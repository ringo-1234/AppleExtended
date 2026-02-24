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

package jp.apple;

import jp.apple.config.AppleConfig;
import jp.apple.log.AppleLogger;
import jp.apple.log.BlockLogHandler;
import jp.apple.util.AppleDir;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = AppleLib.MODID, name = AppleLib.NAME, version = AppleLib.VERSION)
public class AppleLib {
    public static final String MODID = "applelib";
    public static final String NAME = "AppleLib";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static AppleLib instance;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File mcRoot = event.getModConfigurationDirectory().getParentFile();
        AppleDir.init(mcRoot);
        AppleLogger.init();
        AppleConfig.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new BlockLogHandler());
    }
}