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

package jp.apple.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class AppleConfig {
    public static final String CATEGORY_CONTROL = "control";
    public static final String CATEGORY_SECURITY = "security";

    public static int notchRepeatInterval = 2;
    public static boolean enableBlockChangeLog = true;

    public static void init(File file) {
        Configuration cfg = new Configuration(file);
        try {
            cfg.load();
            notchRepeatInterval = cfg.getInt("notchRepeatInterval", CATEGORY_CONTROL, 1, 1, 20, "Interval for A/D notch repeat (ticks)");
            enableBlockChangeLog = cfg.getBoolean(
                    "enableBlockChangeLog",
                    CATEGORY_SECURITY,
                    true,
                    "Enable or disable the block change logger (AppleLib/log)"
            );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cfg.hasChanged()) cfg.save();
        }
    }
}