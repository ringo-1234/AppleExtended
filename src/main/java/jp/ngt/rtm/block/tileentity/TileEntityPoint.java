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

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPoint extends TileEntityMachineBase
{
    private boolean activated = false;
    private float move = 24.0F;

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.activated = nbt.getBoolean("Activated");
        this.move = nbt.getFloat("Move");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("Activated", this.activated);
        nbt.setFloat("Move", this.move);
        return nbt;
    }

    public boolean isActivated()
    {
        return this.activated;
    }

    public void setActivated(boolean par1)
    {
        this.activated = par1;
        this.sendPacket();
        this.markDirty();
    }

    public float getMove()
    {
        return this.move;
    }

    public void setMove(float par1)
    {
        this.move = par1;
        this.sendPacket();
        this.markDirty();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = new AxisAlignedBB(this.getPos().add(-1, 0, -1), this.getPos().add(2, 1, 2));
        return bb.offset(getOffsetX(), getOffsetY(), getOffsetZ());
    }

    @Override
    public ResourceType getSubType()
    {
        return RTMResource.MACHINE_POINT;
    }
}