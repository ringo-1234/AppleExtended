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
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TrainTickHandler {

    @SubscribeEvent
    public void onEntityUpdate(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }

        for (Entity entity : event.world.loadedEntityList) {
            if (entity instanceof EntityTrainBase) {
                TrainSafetyManager.handleDerailment((EntityTrainBase) entity);
            }
        }
    }

    @SubscribeEvent
    public void onEntityRemove(EntityEvent.EnteringChunk event) {

        if (event.getEntity().isDead && event.getEntity() instanceof EntityTrainBase) {
            TrainSafetyManager.cleanup((EntityTrainBase) event.getEntity());
        }
    }
}