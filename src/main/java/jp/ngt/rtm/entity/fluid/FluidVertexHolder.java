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

package jp.ngt.rtm.entity.fluid;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.ModelSolid;

public final class FluidVertexHolder {
    public static final int SPLIT_H = 4;
    public static final int SPLIT_W = 8;
    /**
     * [vtx][x, y, z, color]
     */
    public final float[][] buffer = new float[(SPLIT_H + 1) * SPLIT_W][4];

    public FluidVertexHolder() {
        ;
    }

    public void update(EntityFluid fluid) {
        boolean isSolid = (fluid.getFluidType().type == FluidType.Type.SOLID);
        int splitW = isSolid ? 4 : 8;
        for (int i = 0; i < FluidVertexHolder.SPLIT_H; ++i)//縦
        {
            for (int j = 0; j < splitW; ++j)//横
            {
                int i2 = (i + 1) * 2;
                int j3 = j * (16 / splitW);
                this.addVertex(fluid, (i + 1) * SPLIT_W + j, ModelSolid.SPHERE[i2 * 16 + j3]);
                i2 = i * 2;
                this.addVertex(fluid, i * SPLIT_W + j, ModelSolid.SPHERE[i2 * 16 + j3]);
            }
        }
    }

    private void addVertex(EntityFluid entity, int bufIndex, float[] vtx) {
        float scale = EntityFluid.R;
        double thresholdSq = NGTMath.pow(EntityFluid.SIZE * 1.2D, 2);
        float metaballCoef = scale * 0.2F;

        float orgX = vtx[0] * scale;
        float orgY = vtx[1] * scale;
        float orgZ = vtx[2] * scale;
        float x = orgX;
        float y = orgY;
        float z = orgZ;

        if (entity.getFluidType().type != FluidType.Type.SOLID) {
            for (int i = 0; i < entity.nearFluids.size(); ++i) {
                EntityFluid target = entity.nearFluids.get(i);
                double dx = target.posX - (entity.posX + orgX);
                double dy = target.posY - (entity.posY + orgY);
                double dz = target.posZ - (entity.posZ + orgZ);
                double distanceSq = dx * dx + dy * dy + dz * dz;
                if (distanceSq < thresholdSq) {
                    double d0 = metaballCoef / distanceSq;
                    //normal * (1/距離) * 係数
                    x += (target.posX - entity.posX) * d0;
                    y += (target.posY - entity.posY) * d0;
                    z += (target.posZ - entity.posZ) * d0;
                }
            }

            double len = NGTMath.firstSqrt(x * x + y * y + z * z);
            if (len > EntityFluid.SIZE)//サイズの急激な変化防止
            {
                x *= EntityFluid.SIZE / len;
                y *= EntityFluid.SIZE / len;
                z *= EntityFluid.SIZE / len;
                len = EntityFluid.SIZE;
            }

            //if(y <= orgY)//端を下げる
			/*{
				float min = -1.0F * scale;
				float f0 = ((x * x + z * z) / (scale * scale)) + 0.25F;
				y -= f0 * scale * 0.5F;
				y = y < min ? min : y;
			}*/
        }

        float colorF = ((y / scale) + 1.0F) * 0.5F;
        colorF = colorF < 0.0F ? 0.0F : (colorF > 1.0F ? 1.0F : colorF);//0.0~1.0
        if (entity.posDif > 0.0F) {
            float f0 = entity.posDif / EntityFluid.R;
            f0 = (f0 > 1.0F ? 1.0F : f0) * 0.9F;
            colorF = colorF * (1.0F - f0) + f0;
            //colorF = 1.0F;
        }

        this.buffer[bufIndex][0] = x;
        this.buffer[bufIndex][1] = y;
        this.buffer[bufIndex][2] = z;
        this.buffer[bufIndex][3] = colorF;
    }
}
