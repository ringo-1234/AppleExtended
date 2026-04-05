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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.cfg.ModelConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityScaffold extends TileEntityOrnament {
    private byte dir;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.dir = nbt.getByte("direction");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("direction", this.dir);
        return nbt;
    }

    public byte getDir() {
        return this.dir;
    }

    public void setDir(byte par1) {
        this.dir = par1;
        this.sendPacket();
    }

    public Vec3 getMotionVec() {
        float speed = this.getResourceState().getResourceSet().getConfig().conveyorSpeed;
        if (speed != 0.0F) {
            Vec3 vec = this.getVec(speed);
            vec = vec.rotateAroundY(180.0F - (this.getDir() * 90.0F));
            return vec;
        }
        return Vec3.ZERO;
    }

    protected Vec3 getVec(float par1) {
        return PooledVec3.create(0.0F, 0.0F, par1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        ModelConfig cfg = this.getResourceState().getResourceSet().getConfig();
        float[] fa = cfg.renderAABB;
        BlockPos pos = this.getPos();
        AxisAlignedBB bb = new AxisAlignedBB(
                pos.getX() + fa[0],
                pos.getY() + fa[1],
                pos.getZ() + fa[2],
                pos.getX() + fa[3],
                pos.getY() + fa[4],
                pos.getZ() + fa[5]);
        return bb;
    }

    @Override
    protected ResourceType getSubType() {
        return RTMResource.ORNAMENT_SCAFFOLD;
    }
}