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

package jp.ngt.rtm.sound;

import jp.ngt.ngtlib.io.ResourceLocationCustom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Deprecated
public class SoundObj {
    private final String name;
    private final ResourceLocation location;
    private SoundEvent sound;

    /**
     * RTMSoundManager.register()からのみ生成
     *
     * @param par1 "domain:name"
     */
    public SoundObj(String par1) {
        this.name = par1;
        String[] sa = par1.split(":");
        this.location = new ResourceLocationCustom(sa[0], sa[1]);
        this.sound = new SoundEvent(this.location);
    }

    /**
     * SoundEventの登録
     */
    public void init() {
        this.sound.setRegistryName(this.location);
        ForgeRegistries.SOUND_EVENTS.register(this.sound);
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getResourceLocation() {
        return this.location;
    }
}