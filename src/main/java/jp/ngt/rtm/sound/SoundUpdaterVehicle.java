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
import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.entity.vehicle.IUpdateVehicle;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SoundUpdaterVehicle<T extends EntityVehicleBase> implements IUpdateVehicle {
    protected final SoundHandler soundHandler;
    protected final T vehicle;

    protected boolean silent;
    protected String prevSoundResource;
    protected MovingSoundEntity prevSound;

    protected final List<MovingSoundEntity> playingSounds = new ArrayList<>();

    public SoundUpdaterVehicle(SoundHandler par1, T par2) {
        this.soundHandler = par1;
        this.vehicle = par2;
    }

    @Override
    public void update() {
        ModelSetVehicleBase modelset = (ModelSetVehicleBase) this.vehicle.getResourceState().getResourceSet();
        if (modelset.soundSE != null) {
            ScriptUtil.doScriptIgnoreError(modelset.soundSE, "onUpdate", this);
        } else if (this.vehicle.isDead) {
            if (this.prevSound != null) {
                this.prevSound.stop();
            }
        } else {
            String newSound = this.getSound(modelset);
            if (this.prevSoundResource == null || newSound == null || !newSound.equals(this.prevSoundResource)) {
                if (this.prevSound != null) {
                    this.prevSound.stop();
                }
                this.prevSoundResource = newSound;
                this.silent = true;
            }

            if (this.silent && !this.soundHandler.isSoundPlaying(this.prevSound) && this.prevSoundResource != null) {
                MovingSoundEntity sound = MovingSoundMaker.create(
                        this.vehicle, this.prevSoundResource, true);
                if (sound != null) {
                    if (sound instanceof MovingSoundTrain) {
                        ((MovingSoundTrain) sound).changePitch = this.changePitch();
                    }
                    this.soundHandler.playSound(sound);
                    this.prevSound = sound;
                    this.silent = false;
                }
            }
        }
    }

    protected String getSound(ModelSetVehicleBase modelset) {
        float speed = this.vehicle.getSpeed();
        if (speed > 0) {
            return modelset.getConfig().sound_Acceleration;
        }
        return modelset.getConfig().sound_Stop;
    }

    protected boolean changePitch() {
        return true;
    }

    public float getSpeed() {
        return this.vehicle.getSpeed() * 72.0F;
    }

    public boolean inTunnel() {
        World world = this.vehicle.world;
        int x = NGTMath.floor(this.vehicle.posX);
        int y = NGTMath.floor(this.vehicle.posY);
        int z = NGTMath.floor(this.vehicle.posZ);
        return !world.canSeeSky(new BlockPos(x + 1, y, z + 1)) &&
                !world.canSeeSky(new BlockPos(x - 1, y, z + 1)) &&
                !world.canSeeSky(new BlockPos(x + 1, y, z - 1)) &&
                !world.canSeeSky(new BlockPos(x - 1, y, z - 1));
    }

    /**
     * リピートあり
     */
    @Deprecated
    public void playSound(String domain, String path, float volume, float pitch) {
        this.playSound(domain, path, volume, pitch, true);
    }

    @Deprecated
    public void playSound(String domain, String path, float volume, float pitch, boolean repeat) {
        MovingSoundEntity sound = this.getPlayingSound(domain, path);
        boolean isNewSound = (sound == null);
        if (isNewSound) {
            sound = MovingSoundMaker.create(this.vehicle, domain + ":" + path, repeat);
        }

        if (sound != null) {
            sound.setVolume(volume);
            sound.setPitch(pitch);

            if (isNewSound) {
                this.soundHandler.playSound(sound);
                this.playingSounds.add(sound);
            }
        }
    }

    @Deprecated
    public void stopSound(String domain, String path) {
        MovingSoundEntity sound = this.getPlayingSound(domain, path);
        if (sound != null) {
            sound.stop();
            this.playingSounds.remove(sound);
        }
    }

    public void stopAllSounds() {
        for (MovingSoundEntity sound : this.playingSounds) {
            sound.stop();
        }
        this.playingSounds.clear();

        if (this.prevSound != null) {
            this.prevSound.stop();
            this.prevSound = null;
        }

        this.prevSoundResource = null;
    }

    private MovingSoundEntity getPlayingSound(String domain, String path) {
        for (MovingSoundEntity sound : this.playingSounds) {
            ResourceLocation resource = sound.getSoundLocation();
            if (resource.equals(new ResourceLocationCustom(domain, path))) {
                return sound;
            }
        }
        return null;
    }

    @Override
    public void onModelChanged() {
        this.stopAllSounds();
    }

    @Deprecated
    public Object getData(int id) {
        return this.getEntity().getResourceState().dataMap.getDouble("SU" + id);
    }

    @Deprecated
    public void setData(int id, Object value) {
        double val2 = 0.0D;
        if (value instanceof Double) {
            val2 = (Double) value;
        } else if (value instanceof Integer) {
            val2 = (Integer) value;
        }
        this.getEntity().getResourceState().dataMap.setDouble("SU" + id, val2, 0);
    }

    public T getEntity() {
        return this.vehicle;
    }
}