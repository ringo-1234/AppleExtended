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

package jp.ngt.rtm.block;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSpark extends Particle {
    public ParticleSpark(World worldIn, double xIn, double yIn, double zIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xIn, yIn, zIn, xSpeedIn, ySpeedIn, zSpeedIn);

        this.particleGravity = 0.5F;
        this.particleScale *= 2.0F;
        this.setColor(0xCCFFFF);
    }

    protected void setColor(int color) {
        this.particleRed = (float) ((color >> 16) & 0xFF) / 255.0F;
        this.particleGreen = (float) ((color >> 8) & 0xFF) / 255.0F;
        this.particleBlue = (float) (color & 0xFF) / 255.0F;
    }

    @Override
    public int getBrightnessForRender(float par1) {
        float age = ((float) this.particleAge + par1) / (float) this.particleMaxAge;
        age = MathHelper.clamp(age, 0.0F, 1.0F);
        int brightness = (int) (240.0F * age);
        int bx = brightness;
        int by = brightness;
        return bx | by << 16;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float minU = (float) this.particleTextureIndexX / 16.0F;
        float maxU = minU + 0.0624375F;
        float minV = (float) this.particleTextureIndexY / 16.0F;
        float maxV = minV + 0.0624375F;
        float scY = 0.1F * this.particleScale;
        float scXZ = 0.0625F;

        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int j = brightness >> 16 & 65535;
        int k = brightness & 65535;
        Vec3d[] vecs = new Vec3d[]{
                new Vec3d(-rotationX * scXZ - rotationXY * scXZ, -rotationZ * scY, -rotationYZ * scXZ - rotationXZ * scXZ),
                new Vec3d(-rotationX * scXZ + rotationXY * scXZ, rotationZ * scY, -rotationYZ * scXZ + rotationXZ * scXZ),
                new Vec3d(rotationX * scXZ + rotationXY * scXZ, rotationZ * scY, rotationYZ * scXZ + rotationXZ * scXZ),
                new Vec3d(rotationX * scXZ - rotationXY * scXZ, -rotationZ * scY, rotationYZ * scXZ - rotationXZ * scXZ)
        };

        if (this.particleAngle != 0.0F) {
            float rad = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = MathHelper.cos(rad * 0.5F);
            float vx = MathHelper.sin(rad * 0.5F) * (float) cameraViewDir.x;
            float vy = MathHelper.sin(rad * 0.5F) * (float) cameraViewDir.y;
            float vz = MathHelper.sin(rad * 0.5F) * (float) cameraViewDir.z;
            Vec3d vec3d = new Vec3d((double) vx, (double) vy, (double) vz);

            for (int l = 0; l < 4; ++l) {
                vecs[l] = vec3d.scale(2.0D * vecs[l].dotProduct(vec3d)).add(vecs[l].scale((double) (f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vecs[l]).scale((double) (2.0F * f9)));
            }
        }

        buffer.pos(px + vecs[0].x, py + vecs[0].y, pz + vecs[0].z).tex(maxU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(px + vecs[1].x, py + vecs[1].y, pz + vecs[1].z).tex(maxU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(px + vecs[2].x, py + vecs[2].y, pz + vecs[2].z).tex(minU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(px + vecs[3].x, py + vecs[3].y, pz + vecs[3].z).tex(minU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }
}