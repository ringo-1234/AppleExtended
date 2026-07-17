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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class PlayerCameraTrain {
    private static final double SEARCH_RANGE = 6.0D;

    private static EntityTrainBase lastTrain = null;
    private static float lastAppliedTrainYaw = Float.NaN;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || player.isRiding()) {
            lastAppliedTrainYaw = Float.NaN;
            lastTrain = null;
            return;
        }

        EntityTrainBase train = findNearestTrainPlayerIsOn(player);
        if (train == null) {
            lastAppliedTrainYaw = Float.NaN;
            lastTrain = null;
            return;
        }

        if (train != lastTrain) {
            lastTrain = train;
            lastAppliedTrainYaw = Float.NaN;
        }

        float partialTicks = (float) Minecraft.getMinecraft().getRenderPartialTicks();
        float interpolatedYaw = train.prevRotationYaw
                + MathHelper.wrapDegrees(train.rotationYaw - train.prevRotationYaw) * partialTicks;

        if (Float.isNaN(lastAppliedTrainYaw)) {
            lastAppliedTrainYaw = interpolatedYaw;
            return;
        }

        float frameDYaw = MathHelper.wrapDegrees(interpolatedYaw - lastAppliedTrainYaw);
        if (frameDYaw != 0.0F) {
            player.rotationYaw -= frameDYaw;
        }
        lastAppliedTrainYaw = interpolatedYaw;
    }

    private static EntityTrainBase findNearestTrainPlayerIsOn(EntityPlayer player) {
        AxisAlignedBB box = player.getEntityBoundingBox().grow(SEARCH_RANGE, 2.0D, SEARCH_RANGE);
        List<Entity> list = player.world.getEntitiesWithinAABB(EntityTrainBase.class, box);
        EntityTrainBase nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        for (Entity e : list) {
            EntityTrainBase train = (EntityTrainBase) e;
            double feetY = player.getEntityBoundingBox().minY;
            double trainTopY = train.posY + EntityTrainBase.TRAIN_HEIGHT;
            if (Math.abs(feetY - trainTopY) > 1.0D) continue;
            double distSq = train.getDistanceSq(player);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = train;
            }
        }
        return nearest;
    }
}
