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

package jp.ngt.rtm.render;

import jp.ngt.ngtlib.math.Axis;
import jp.ngt.rtm.block.tileentity.TileEntityMechanism;
import jp.ngt.rtm.modelpack.modelset.ModelSetMechanism;

public class MechanismPartsRenderer extends TileEntityPartsRenderer<ModelSetMechanism> {
    public MechanismPartsRenderer(String... par1) {
        super(par1);
    }

    public float getRotation(TileEntityMechanism entity, Axis axis) {
        if (entity != null) {
            float f0 = 0.0F;
            if ((entity.getX() % 2 == 0) ^ (entity.getY() % 2 == 0) ^ (entity.getZ() % 2 == 0)) {
                int teeth = entity.getResourceState().getResourceSet().getConfig().teethCount;
                if (teeth > 0) {
                    f0 = (360.0F / (float) teeth) * 0.5F;//隣接歯車から1/2ピッチずらす
                }
            }
            return entity.rotations[axis.face.getIndex()] + f0;
        }
        return 0.0F;
    }

    public boolean isPowered(TileEntityMechanism entity) {
        if (entity != null) {
            return entity.isPowered;
        }
        return false;
    }
}
