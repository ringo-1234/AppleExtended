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
    public static final String CATEGORY_SOUND = "sound";
    public static final String CATEGORY_SOUND_RANGE = "sound_range";
    public static final String CATEGORY_LOAD = "load";

    public static float runningSoundRange = 16.0F;
    public static float crossingSoundRange = 64.0F;
    public static float hornSoundRange = 16.0F;

    public static int notchRepeatInterval = 1;
    public static boolean enableBlockChangeLog = true;

    public static boolean enableJointSound = true;
    public static boolean enableNotchSound = true;
    public static int cachedModelMemoryLimitMiB = 256;
    public static int cachedModelProtectSeconds = 10;

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
            enableJointSound = cfg.getBoolean("enableJointSound", CATEGORY_SOUND, true, "Whether to enable RTM standard joint sounds");
            enableNotchSound = cfg.getBoolean("enableNotchSound", CATEGORY_SOUND, true, "Whether to enable the RTM standard notch operation sound");

            runningSoundRange = cfg.getFloat("runningSoundRange", CATEGORY_SOUND_RANGE,
                    16.0F, 1.0F, 1024.0F, "Set the range in which sound can be heard.");
            crossingSoundRange = cfg.getFloat("crossingSoundRange", CATEGORY_SOUND_RANGE,
                    64.0F, 1.0F, 1024.0F, "Set the range in which sound can be heard.");
            hornSoundRange = cfg.getFloat("hornSoundRange", CATEGORY_SOUND_RANGE,
                    16.0F, 1.0F, 1024.0F, "Set the range in which sound can be heard.");

            cachedModelMemoryLimitMiB = cfg.getInt(
                    "cachedModelMemoryLimitMiB",
                    CATEGORY_LOAD,
                    256,
                    16,
                    8192,
                    "Maximum in-memory size of cached polygon models (MiB)."
            );
            cachedModelProtectSeconds = cfg.getInt(
                    "cachedModelProtectSeconds",
                    CATEGORY_LOAD,
                    10,
                    0,
                    600,
                    "Do not evict cached polygon models used within this number of seconds."
            );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cfg.hasChanged()) cfg.save();
        }
    }
}
