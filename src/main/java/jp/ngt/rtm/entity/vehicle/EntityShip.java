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

package jp.ngt.rtm.entity.vehicle;

import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityShip extends EntityVehicle {
    public EntityShip(World world) {
        super(world);
        this.stepHeight = 0.5F;
    }

    @Override
    protected boolean shouldUpdateMotion() {
        return this.inWater;
    }

    @Override
    protected void updateMotion(EntityLivingBase entity, float moveStrafe, float moveForward) {
        super.updateMotion(entity, moveStrafe, moveForward);

        if (this.speed > 0.0D && this.inWater) {
            this.rotationRoll = moveStrafe * (float) (this.speed / this.getResourceState().getResourceSet().getConfig().getMaxSpeed(this.onGround)) * -5.0F;
        }
    }

    @Override
    protected void updateFallState() {
        if (!this.inWater && !this.onGround) {
            this.motionY -= 0.05D;
        } else {
            AxisAlignedBB aabb = this.getEntityBoundingBox();
            aabb = new AxisAlignedBB(aabb.minX, aabb.minY + 0.0625D, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
            if (this.world.isMaterialInBB(aabb, Material.WATER)) {
                this.motionY += 0.05D;
            } else {
                this.motionY = 0.0D;
            }
        }
    }

    @Override
    protected ItemStack getVehicleItem() {
        return new ItemStack(RTMItem.itemVehicle, 1, 1);
    }

    @Override
    protected ResourceType getSubType() {
        return RTMResource.VEHICLE_SHIP;
    }
}