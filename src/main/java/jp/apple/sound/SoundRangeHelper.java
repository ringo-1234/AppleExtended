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

package jp.apple.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class SoundRangeHelper {

    private SoundRangeHelper() {
    }

    public static float calcVolume(float baseVolume, float soundRange,
                                   float x, float y, float z) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return baseVolume;

        float dx = (float) player.posX - x;
        float dy = (float) player.posY - y;
        float dz = (float) player.posZ - z;
        float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist >= soundRange) return 0.0F;

        float defaultRange = 16.0F;

        if (soundRange >= defaultRange) {

            if (dist <= defaultRange) {
                return baseVolume;
            }
            return baseVolume * (soundRange - dist) / (soundRange - defaultRange);
        } else {


            return baseVolume * (soundRange - dist) / soundRange;
        }
    }
}