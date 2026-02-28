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

package jp.ngt.rtm.entity.ai;

import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.entity.npc.NPCAIEnterStation;
import jp.ngt.rtm.entity.npc.NPCAILeaveStation;
import jp.ngt.rtm.entity.npc.NPCAIRideTrain;
import jp.ngt.rtm.entity.train.parts.EntityFloor;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAITravelByTrain extends EntityAIBase {
    public static final int WAIT_COEFFICIENT = 20;
    public static final int TO_MINUTES = 60 * 20 / 3;

    protected final NPCAIEnterStation aiEnterStation;
    protected final NPCAIRideTrain aiRideTrain;
    protected final EntityAIBase aiLeaveStation;

    private EntityNPC npc;
    private float moveSpeed;

    private EntityAIBase activeTask;
    private int count;

    public EntityAITravelByTrain(EntityNPC par1, float par2) {
        this.npc = par1;
        this.moveSpeed = par2;
        this.setMutexBits(1);

        this.aiEnterStation = new NPCAIEnterStation(par1, par2);
        this.aiRideTrain = new NPCAIRideTrain(par1, par2);
        this.aiLeaveStation = new NPCAILeaveStation(par1, par2);
    }

    @Override
    public boolean shouldExecute() {
        if (this.count > 0) {
            --this.count;
            return false;
        }

        if (this.activeTask == null) {
            this.activeTask = this.aiEnterStation;
            if (this.npc.isRiding()) {
                this.npc.startRiding(null);
            }
        } else if (this.activeTask == this.aiRideTrain) {
            if (this.npc.isRiding() && ((EntityFloor) this.npc.getRidingEntity()).getVehicle().getSpeed() == 0.0F) {
                this.dismount();

                if (this.npc.getRNG().nextInt(4) == 0) {
                    this.activeTask = this.aiRideTrain;
                } else {
                    this.activeTask = this.aiLeaveStation;
                    this.aiRideTrain.targetTrain = null;
                }
            }
        }

        return this.activeTask.shouldExecute();
    }

    private void dismount() {
        EntityFloor floor = ((EntityFloor) this.npc.getRidingEntity());
        assert floor != null;
        this.npc.dismountRidingEntity();
        floor.onDismount(this.npc);
    }

    @Override
    public boolean shouldContinueExecuting() {
        boolean flag = this.activeTask.shouldContinueExecuting();
        if (!flag) {
            if (this.activeTask == this.aiEnterStation) {
                if (this.aiEnterStation.openedTurnstile) {
                    this.activeTask = this.aiRideTrain;
                    if (this.activeTask.shouldExecute()) {
                        this.activeTask.startExecuting();
                        return true;
                    }
                }
            } else if (this.activeTask == this.aiRideTrain) {
                if (this.npc.isRiding()) {
                    this.count = (this.npc.getRNG().nextInt(WAIT_COEFFICIENT) + 1) * TO_MINUTES;
                    return false;
                }
            } else if (this.activeTask == this.aiLeaveStation) {
                this.count = (this.npc.getRNG().nextInt(WAIT_COEFFICIENT) + 1) * TO_MINUTES;
                this.activeTask = null;
                return false;
            }
        }
        return flag;
    }

    @Override
    public void startExecuting() {
        this.activeTask.startExecuting();
    }

    @Override
    public void updateTask() {
        this.activeTask.updateTask();
    }
}