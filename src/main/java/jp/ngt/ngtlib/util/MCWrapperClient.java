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

package jp.ngt.ngtlib.util;

import jp.ngt.ngtlib.NGTCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MCWrapperClient {
    public static void spawnParticle(World world, String name, double posX, double posY, double posZ, double speedX, double speedY, double speedZ) {
        world.spawnParticle(EnumParticleTypes.getByName(name), posX, posY, posZ, speedX, speedY, speedZ);
    }

    public static void playSound(World world, String name, double posX, double posY, double posZ, float volume, float pitch, boolean distanceDelay) {
        SoundEvent soundevent = (SoundEvent) SoundEvent.REGISTRY.getObject(new ResourceLocation(name));
        world.playSound(posX, posY, posZ, soundevent, SoundCategory.MASTER, volume, pitch, distanceDelay);
    }

    public static EntityPlayer getPlayer() {
        return NGTCore.proxy.getPlayer();
    }

    public static void execCommand(String command) {
        NGTUtilClient.getMinecraft().player.sendChatMessage("/" + command);//"/"なしは通常のチャットメッセージ
    }
}