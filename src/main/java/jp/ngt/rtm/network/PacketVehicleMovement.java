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

package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMEntity;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketVehicleMovement implements IMessage, IMessageHandler<PacketVehicleMovement, IMessage> {
    private static final double SAMPLING = 256.0D;
    private static final double DIV_32 = 1.0D / SAMPLING;

    public int entityId;
    /**
     * 0:Other, 1:Train, 2:Vehicle
     */
    public byte type;
    public int vehicleX, vehicleY, vehicleZ;
    public float vehicleYaw, vehiclePitch, vehicleRoll, vehicleSpeed;
    public long serverTick;

    public PacketVehicleMovement() {
    }

    public PacketVehicleMovement(Entity par1, boolean onDead) {
        this.entityId = par1.getEntityId();
        this.type = 0;
        this.vehicleX = NGTMath.floor(par1.posX * SAMPLING);
        this.vehicleY = NGTMath.floor(par1.posY * SAMPLING);
        this.vehicleZ = NGTMath.floor(par1.posZ * SAMPLING);
        this.vehicleYaw = par1.rotationYaw;
        this.vehiclePitch = par1.rotationPitch;
        this.serverTick = par1.ticksExisted;
        if (par1 instanceof EntityVehicleBase) {
            this.type = 2;
            this.vehicleRoll = ((EntityVehicleBase) par1).getRoll();
            this.vehicleSpeed = ((EntityVehicleBase) par1).getSpeed();
        } else {
            this.vehicleRoll = ((EntityBogie) par1).rotationRoll;
        }

        if (onDead) {
            this.vehicleY = -1;
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeByte(this.type);
        buffer.writeInt(this.vehicleX);
        buffer.writeInt(this.vehicleY);
        buffer.writeInt(this.vehicleZ);
        buffer.writeFloat(this.vehicleYaw);
        buffer.writeFloat(this.vehiclePitch);
        buffer.writeFloat(this.vehicleRoll);
        buffer.writeFloat(this.vehicleSpeed);
        buffer.writeLong(this.serverTick);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.type = buffer.readByte();
        this.vehicleX = buffer.readInt();
        this.vehicleY = buffer.readInt();
        this.vehicleZ = buffer.readInt();
        this.vehicleYaw = buffer.readFloat();
        this.vehiclePitch = buffer.readFloat();
        this.vehicleRoll = buffer.readFloat();
        this.vehicleSpeed = buffer.readFloat();
        this.serverTick = buffer.readLong();
    }

    @Override
    public IMessage onMessage(PacketVehicleMovement message, MessageContext ctx) {
        World world = NGTUtil.getClientWorld();
        if (world == null) {
            return null;
        }

        if (message.vehicleY < 0) {
            this.deleteEntity(world, message.entityId);
            return null;
        }

        Entity entity = world.getEntityByID(message.entityId);
        if (entity != null && !entity.isDead) {
            double x = (double) message.vehicleX * DIV_32;
            double y = (double) message.vehicleY * DIV_32;
            double z = (double) message.vehicleZ * DIV_32;
            entity.serverPosX = EntityTracker.getPositionLong(x);
            entity.serverPosY = EntityTracker.getPositionLong(y);
            entity.serverPosZ = EntityTracker.getPositionLong(z);
            if (entity instanceof EntityVehicleBase) {
                ((EntityVehicleBase) entity).setPositionAndRotationDirect(x, y, z, message.vehicleYaw, message.vehiclePitch,
                        RTMEntity.FREQ_VEHICLE, false, message.serverTick);
                ((EntityVehicleBase) entity).setRollAndSpeed(message.vehicleSpeed, message.vehicleRoll);
            } else {
                entity.setPositionAndRotationDirect(x, y, z, message.vehicleYaw, message.vehiclePitch,
                        RTMEntity.FREQ_VEHICLE, false);
                ((EntityBogie) entity).setRoll(message.vehicleRoll);
            }
        } else {
            NGTLog.debug("[PVM] Entity is null or dead %d", message.entityId);
        }
        return null;
    }

    private void deleteEntity(World world, int id) {
        Entity entity = world.getEntityByID(id);
        if (entity == null) {
            for (Entity entity2 : world.weatherEffects) {
                if (entity2.getEntityId() == id) {
                    entity = entity2;
                    break;
                }
            }
        }

        if (entity != null) {

            entity.setDead();
        }
    }
}