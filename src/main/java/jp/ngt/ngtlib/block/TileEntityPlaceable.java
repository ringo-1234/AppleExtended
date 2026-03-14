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

package jp.ngt.ngtlib.block;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityPlaceable extends TileEntityCustom {
    private float rotation; //  Y
    private float rotationX; // X
    private float rotationZ; // Z
    private float scale = 1.0F;
    private float offsetX, offsetY, offsetZ;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.setOffset(
                nbt.getFloat("offsetX"),
                nbt.getFloat("offsetY"),
                nbt.getFloat("offsetZ"),
                false
        );
        this.setRotation(nbt.getFloat("Yaw"), false);
        this.rotationX = nbt.hasKey("apRotX") ? nbt.getFloat("apRotX") : 0.0F;
        this.rotationZ = nbt.hasKey("apRotZ") ? nbt.getFloat("apRotZ") : 0.0F;
        this.scale = nbt.hasKey("apScale") ? nbt.getFloat("apScale") : 1.0F;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setFloat("Yaw", this.rotation);
        nbt.setFloat("apRotX", this.rotationX);
        nbt.setFloat("apRotZ", this.rotationZ);
        nbt.setFloat("apScale", this.scale);
        nbt.setFloat("offsetX", this.offsetX);
        nbt.setFloat("offsetY", this.offsetY);
        nbt.setFloat("offsetZ", this.offsetZ);
        return nbt;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getRotationX() {
        return this.rotationX;
    }

    public float getRotationZ() {
        return this.rotationZ;
    }

    public void setRotationXYZS(float x, float y, float z, float s, boolean sync) {
        this.rotationX = x % 360.0F;
        this.rotation = y % 360.0F;
        this.rotationZ = z % 360.0F;
        this.scale = s;
        if (sync) {
            this.sendPacket();
            this.markDirty();
        }
    }

    public void setRotation(float par1, boolean synch) {
        this.rotation = par1 % 360.0F;
        if (synch) {
            this.sendPacket();
            this.markDirty();
        }
    }

    public void setRotation(EntityPlayer player, float rotationInterval, boolean synch) {
        int yaw = NGTMath.floor(NGTMath.normalizeAngle(-player.rotationYaw + 180.0D + (rotationInterval / 2.0D)) / (double) rotationInterval);
        this.setRotation((float) yaw * rotationInterval, synch);
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float par1, boolean sync) {
        this.scale = par1;
        if (sync) {
            this.sendPacket();
            this.markDirty();
        }
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public void setOffset(float offsetX, float offsetY, float offsetZ, boolean sync) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        if (sync) {
            this.sendPacket();
            this.markDirty();
        }
    }
}