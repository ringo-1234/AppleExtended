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

package jp.ngt.rtm.entity.npc;

public class NPCAIEnterStation extends NPCAISerachTurnstile {
    public static final double MAX_LEAVE_DISTANCE = 3.0D;

    private boolean enterStation;

    public NPCAIEnterStation(EntityNPC par1, float par2) {
        super(par1, par2);
    }

    @Override
    public boolean shouldExecute() {
        this.enterStation = false;
        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.enterStation) {
            return !this.npc.getNavigator().noPath();
        } else {
            boolean flag = super.shouldContinueExecuting();
            if (!flag && this.openedTurnstile) {
                double vecX = (double) this.targetBlockPos[0] + 0.5D - this.npc.posX;
                double vecY = (double) this.targetBlockPos[1] - this.npc.posY;
                double vecZ = (double) this.targetBlockPos[2] + 0.5D - this.npc.posZ;
                double d0 = MAX_LEAVE_DISTANCE;
                while (d0 > 2.0D) {
                    this.entityPathNavigate = this.npc.getNavigator().getPathToXYZ(
                            this.npc.posX + vecX * d0,
                            this.npc.posY + vecY * d0,
                            this.npc.posZ + vecZ * d0);
                    if (this.entityPathNavigate != null) {
                        this.enterStation = true;
                        this.startExecuting();
                        return true;
                    }
                    d0 -= 0.25D;
                }
            }
            return flag;
        }
    }
}