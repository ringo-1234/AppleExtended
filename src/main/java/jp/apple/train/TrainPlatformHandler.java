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

package jp.apple.train;

import jp.ngt.rtm.entity.train.EntityTrainBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class TrainPlatformHandler {
    private static final double SURFACE_MARGIN = 0.2D;
    private static final double RIDE_MARGIN = 1.0D;
    private static final double SIDE_MARGIN = 0.15D;
    private static final double Y_CORRECTION_FACTOR = 0.3D;
    private static final double MAX_Y_CORRECTION = 0.5D;

    private static final Set<UUID> ridingPlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public static void updateStandingEntities(EntityTrainBase train) {
        double dx = train.posX - train.prevPosX;
        double dy = train.posY - train.prevPosY;
        double dz = train.posZ - train.prevPosZ;
        float dYaw = MathHelper.wrapDegrees(train.rotationYaw - train.prevRotationYaw);

        if (dx == 0.0D && dy == 0.0D && dz == 0.0D && dYaw == 0.0F) {
            return;
        }

        double halfLength = getHalfLength(train);
        double halfWidth = EntityTrainBase.TRAIN_WIDTH / 2.0D + SIDE_MARGIN;

        double searchExtent = halfLength + halfWidth;
        AxisAlignedBB searchBox = new AxisAlignedBB(
                train.posX - searchExtent, train.posY - 0.5D, train.posZ - searchExtent,
                train.posX + searchExtent, train.posY + EntityTrainBase.TRAIN_HEIGHT + 0.5D, train.posZ + searchExtent
        );

        List<Entity> nearby = train.world.getEntitiesWithinAABBExcludingEntity(train, searchBox);
        for (Entity entity : nearby) {
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }
            if (entity.isRiding()) {
                continue;
            }

            UUID uuid = entity.getUniqueID();
            boolean alreadyRiding = ridingPlayers.contains(uuid);
            double margin = alreadyRiding ? RIDE_MARGIN : SURFACE_MARGIN;

            boolean standing = isStandingOnTop(train, halfLength, halfWidth, entity, margin, alreadyRiding);
            if (standing) {
                ridingPlayers.add(uuid);
            } else if (alreadyRiding) {
                ridingPlayers.remove(uuid);
            }
        }
    }

    private static final Set<UUID> ridingPlayersClient = Collections.newSetFromMap(new WeakHashMap<>());

    @SideOnly(Side.CLIENT)
    public static void updateStandingEntitiesClient(EntityTrainBase train) {
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        if (player == null || player.isRiding()) return;

        double dx = train.posX - train.prevPosX;
        double dy = train.posY - train.prevPosY;
        double dz = train.posZ - train.prevPosZ;
        float dYaw = MathHelper.wrapDegrees(train.rotationYaw - train.prevRotationYaw);

        double halfLength = getHalfLength(train);
        double halfWidth = EntityTrainBase.TRAIN_WIDTH / 2.0D + SIDE_MARGIN;
        boolean alreadyRiding = ridingPlayersClient.contains(player.getUniqueID());
        double margin = alreadyRiding ? RIDE_MARGIN : SURFACE_MARGIN;

        if (isStandingOnTop(train, halfLength, halfWidth, player, margin, alreadyRiding)) {
            ridingPlayersClient.add(player.getUniqueID());
            moveLocalPlayer(player, dx, dy, dz, dYaw, train);
        } else {
            ridingPlayersClient.remove(player.getUniqueID());
        }
    }

    private static final Map<UUID, double[]> pendingOffsetClient = new WeakHashMap<>();

    @SideOnly(Side.CLIENT)
    private static void moveLocalPlayer(EntityPlayer player, double dx, double dy, double dz, float dYaw, EntityTrainBase train) {
        double addX;
        double addZ;

        if (dYaw != 0.0F) {
            double relX = player.posX - train.prevPosX;
            double relZ = player.posZ - train.prevPosZ;
            double rad = Math.toRadians(dYaw);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double rotX = relX * cos + relZ * sin;
            double rotZ = relZ * cos - relX * sin;
            addX = (train.posX + rotX) - player.posX;
            addZ = (train.posZ + rotZ) - player.posZ;
        } else {
            addX = dx;
            addZ = dz;
        }

        double trainTopY = train.posY + EntityTrainBase.TRAIN_HEIGHT;
        double yError = trainTopY - player.posY;
        double yCorrection = MathHelper.clamp(yError * Y_CORRECTION_FACTOR, -MAX_Y_CORRECTION, MAX_Y_CORRECTION);

        double[] pending = pendingOffsetClient.computeIfAbsent(player.getUniqueID(), k -> new double[3]);
        pending[0] += addX;
        pending[1] += dy + yCorrection;
        pending[2] += addZ;

        double smoothing = 0.6D;
        double stepX = pending[0] * smoothing;
        double stepY = pending[1] * smoothing;
        double stepZ = pending[2] * smoothing;

        player.move(net.minecraft.entity.MoverType.SELF, stepX, stepY, stepZ);
        pending[0] -= stepX;
        pending[1] -= stepY;
        pending[2] -= stepZ;

        player.motionY = 0.0D;
        player.fallDistance = 0.0F;
        player.onGround = true;
    }

    private static double getHalfLength(EntityTrainBase train) {
        jp.ngt.rtm.modelpack.cfg.TrainConfig cfg = train.getResourceState().getResourceSet().getConfig();
        if (cfg == null) {
            return EntityTrainBase.TRAIN_WIDTH / 2.0D;
        }
        return cfg.trainDistance;
    }

    private static boolean isStandingOnTop(EntityTrainBase train, double halfLength, double halfWidth, Entity entity, double yMargin, boolean alreadyRiding) {
        double relX = entity.posX - train.posX;
        double relZ = entity.posZ - train.posZ;

        double rad = Math.toRadians(train.rotationYaw);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double localLength = relX * sin + relZ * cos;
        double localWidth = relX * cos - relZ * sin;

        double widthMargin = alreadyRiding ? halfWidth + 0.5D : halfWidth;
        double lengthMargin = alreadyRiding ? halfLength + 0.3D : halfLength;

        boolean horizontalOverlap = Math.abs(localWidth) <= widthMargin && Math.abs(localLength) <= lengthMargin;
        if (!horizontalOverlap) {
            return false;
        }

        double feetY = entity.getEntityBoundingBox().minY;
        double trainTopY = train.posY + EntityTrainBase.TRAIN_HEIGHT;

        return feetY >= trainTopY - yMargin && feetY <= trainTopY + 0.5D;
    }

    private static void carryEntity(Entity entity, double dx, double dy, double dz, float dYaw, EntityTrainBase train) {
        double addX;
        double addZ;

        if (dYaw != 0.0F) {
            double relX = entity.posX - train.prevPosX;
            double relZ = entity.posZ - train.prevPosZ;

            double rad = Math.toRadians(dYaw);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);

            double rotX = relX * cos + relZ * sin;
            double rotZ = relZ * cos - relX * sin;

            addX = (train.posX + rotX) - entity.posX;
            addZ = (train.posZ + rotZ) - entity.posZ;
        } else {
            addX = dx;
            addZ = dz;
        }

        double trainTopY = train.posY + EntityTrainBase.TRAIN_HEIGHT;
        double yError = trainTopY - entity.posY;
        double yCorrection = MathHelper.clamp(yError * Y_CORRECTION_FACTOR, -MAX_Y_CORRECTION, MAX_Y_CORRECTION);

        entity.motionX = addX;
        entity.motionY = dy + yCorrection;
        entity.motionZ = addZ;
        entity.velocityChanged = true;

        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) entity;
            playerMP.connection.sendPacket(new SPacketEntityVelocity(playerMP));
        }
    }
}
