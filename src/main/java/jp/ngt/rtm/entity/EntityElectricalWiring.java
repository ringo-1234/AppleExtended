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

package jp.ngt.rtm.entity;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.electric.TileEntityDummyEW;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EntityElectricalWiring extends EntityInstalledObject {
    public final TileEntityDummyEW tileEW;

    public EntityElectricalWiring(World world) {
        super(world);
        this.tileEW = new TileEntityDummyEW(this);
        this.tileEW.setWorld(world);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.tileEW.readFromNBT(nbt);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        this.tileEW.writeToNBT(nbt);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.tileEW.getPos().getY() <= 0) {
            this.setTilePos();
        }

        this.tileEW.update();
    }

    private void setTilePos() {
        int x = NGTMath.floor(this.posX);
        int y = NGTMath.floor(this.posY);
        int z = NGTMath.floor(this.posZ);
        this.tileEW.setPos(new BlockPos(x, y, z));
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (this.world.isRemote) {
            return true;
        } else if (this.tileEW.onRightClick(player)) {
            return true;
        }
        return super.processInitialInteract(player, hand);
    }

    public abstract int getElectricity();

    public abstract void setElectricity(int par1);
}