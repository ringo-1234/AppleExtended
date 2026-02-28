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

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.item.ItemGun;
import jp.ngt.rtm.modelpack.modelset.ModelSetNPC;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.opengl.GL11;

public class NPCPartsRenderer extends EntityPartsRenderer<ModelSetNPC> {
    public float headAngleX;
    public float headAngleY;
    public float headAngleZ;
    public float bodyAngleX;
    public float bodyAngleY;
    public float bodyAngleZ;
    public float leftArmAngleX;
    public float leftArmAngleY;
    public float leftArmAngleZ;
    public float rightArmAngleX;
    public float rightArmAngleY;
    public float rightArmAngleZ;
    public float leftLegAngleX;
    public float leftLegAngleY;
    public float leftLegAngleZ;
    public float rightLegAngleX;
    public float rightLegAngleY;
    public float rightLegAngleZ;

    public NPCPartsRenderer(String... par1) {
        super(par1);
    }

    @Override
    public void init(ModelSetNPC par1, ModelObject par2) {
        super.init(par1, par2);
    }

    public void rotateAndRender(Parts parts, float x, float y, float z, float rotationX, float rotationY, float rotationZ) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(NGTMath.toDegrees(rotationZ), 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(NGTMath.toDegrees(rotationY), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(NGTMath.toDegrees(rotationX), 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(-x, -y, -z);
        parts.render(this);
        GL11.glPopMatrix();
    }

    public void setRotationAngles(EntityLivingBase entity, float partialTicks) {
        if (entity == null) {
            return;
        }//選択GUI内描画

        this.setupRotateCorpse(entity, partialTicks);//Yaw設定

        float arg1 = this.getRendererArg1(entity, partialTicks);
        float arg2 = this.getRendererArg2(entity, partialTicks);
        float arg3 = this.getRendererArg3(entity, partialTicks);
        float arg4 = this.getRendererArg4(entity, partialTicks);
        float arg5 = this.getRendererArg5(entity, partialTicks);
        int heldItemRight = this.heldItemRight(entity);
        int heldItemLeft = this.heldItemLeft(entity);
        float radPIdiv10 = NGTMath.PI / 10F;

        this.headAngleY = NGTMath.toRadians(arg4);
        this.headAngleX = NGTMath.toRadians(arg5);
        this.rightArmAngleX = NGTMath.getCos(arg1 * 0.6662F + NGTMath.PI) * 2.0F * arg2 * 0.5F;
        this.leftArmAngleX = NGTMath.getCos(arg1 * 0.6662F) * 2.0F * arg2 * 0.5F;
        this.rightArmAngleZ = 0.0F;
        this.leftArmAngleZ = 0.0F;
        this.rightLegAngleX = NGTMath.getCos(arg1 * 0.6662F) * 1.4F * arg2;
        this.leftLegAngleX = NGTMath.getCos(arg1 * 0.6662F + NGTMath.PI) * 1.4F * arg2;
        this.rightLegAngleY = 0.0F;
        this.leftLegAngleY = 0.0F;

        if (this.isRiding(entity)) {
            this.rightArmAngleX += -(NGTMath.PI / 5F);
            this.leftArmAngleX += -(NGTMath.PI / 5F);
            this.rightLegAngleX = -(NGTMath.PI * 2F / 5F);
            this.leftLegAngleX = -(NGTMath.PI * 2F / 5F);
            this.rightLegAngleY = radPIdiv10;
            this.leftLegAngleY = -radPIdiv10;
        }

        if (heldItemLeft > 0) {
            this.leftArmAngleX = this.leftArmAngleX * 0.5F - radPIdiv10 * (float) heldItemLeft;
        }

        this.rightArmAngleY = 0.0F;
        this.leftArmAngleY = 0.0F;

        switch (heldItemRight) {
            case 0:
                break;
            case 2:
                break;
            case 1:
                this.rightArmAngleX = this.rightArmAngleX * 0.5F - radPIdiv10 * (float) heldItemRight;
                break;
            case 3:
                this.rightArmAngleX = this.rightArmAngleX * 0.5F - radPIdiv10 * (float) heldItemRight;
                this.rightArmAngleY = -0.5235988F;
            default:
                break;
        }

        float swingProgress = this.getSwingProgress(entity, partialTicks);
        if (swingProgress > 0.0F) {
            this.bodyAngleY = NGTMath.getSin((float) (Math.sqrt(swingProgress) * NGTMath.PI * 2.0F)) * 0.2F;
            //this.rightArmPosZ = MathHelper.sin(this.bodyAngleY) * 5.0F;
            //this.rightArmPosX = -MathHelper.cos(this.bodyAngleY) * 5.0F;
            //this.leftArmPosZ = -MathHelper.sin(this.bodyAngleY) * 5.0F;
            //this.leftArmPosX = MathHelper.cos(this.bodyAngleY) * 5.0F;
            this.rightArmAngleY += this.bodyAngleY;
            this.leftArmAngleY += this.bodyAngleY;
            this.leftArmAngleX += this.bodyAngleY;
            float f = 1.0F - swingProgress;
            f = f * f * f * f;
            f = 1.0F - f;
            float f1 = NGTMath.getSin(f * NGTMath.PI);
            float f2 = NGTMath.getSin(swingProgress * NGTMath.PI) * -(this.headAngleX - 0.7F) * 0.75F;
            this.rightArmAngleX = (float) ((double) this.rightArmAngleX - ((double) f1 * 1.2D + (double) f2));//不自然に手が上がる
            this.rightArmAngleY += this.bodyAngleY * 2.0F;
            this.rightArmAngleZ += NGTMath.getSin(swingProgress * NGTMath.PI) * -0.4F;
        }

        if (this.isSneak(entity)) {
            this.bodyAngleX = 0.5F;
            this.rightArmAngleX += 0.4F;
            this.leftArmAngleX += 0.4F;
            //this.rightLegPosZ = 4.0F;
            //this.leftLegPosZ = 4.0F;
            //this.rightLegPosY = 9.0F;
            //this.leftLegPosY = 9.0F;
            //this.headPosY = 1.0F;
        } else {
            this.bodyAngleX = 0.0F;
            //this.rightLegPosZ = 0.1F;
            //this.leftLegPoseZ = 0.1F;
            //this.rightLegPosY = 12.0F;
            //this.leftLegPosY = 12.0F;
            //this.headPosY = 0.0F;
        }

        this.rightArmAngleZ += NGTMath.getCos(arg3 * 0.09F) * 0.05F + 0.05F;
        this.leftArmAngleZ -= NGTMath.getCos(arg3 * 0.09F) * 0.05F + 0.05F;
        this.rightArmAngleX += NGTMath.getSin(arg3 * 0.067F) * 0.05F;
        this.leftArmAngleX -= NGTMath.getSin(arg3 * 0.067F) * 0.05F;

        if (this.aimedBow(entity)) {
            float f3 = 0.0F;
            float f4 = 0.0F;
            this.rightArmAngleZ = 0.0F;
            this.leftArmAngleZ = 0.0F;
            this.rightArmAngleY = -(0.1F - f3 * 0.6F) + this.headAngleY;
            this.leftArmAngleY = 0.1F - f3 * 0.6F + this.headAngleY + 0.4F;
            this.rightArmAngleX = -NGTMath.toRadians(90.0F) + this.headAngleX;
            this.leftArmAngleX = -NGTMath.toRadians(90.0F) + this.headAngleX;
            this.rightArmAngleX -= f3 * 1.2F - f4 * 0.4F;
            this.leftArmAngleX -= f3 * 1.2F - f4 * 0.4F;
            this.rightArmAngleZ += NGTMath.getCos(arg3 * 0.09F) * 0.05F + 0.05F;
            this.leftArmAngleZ -= NGTMath.getCos(arg3 * 0.09F) * 0.05F + 0.05F;
            this.rightArmAngleX += NGTMath.getSin(arg3 * 0.067F) * 0.05F;
            this.leftArmAngleX -= NGTMath.getSin(arg3 * 0.067F) * 0.05F;
        }

        //GlStateManager.scale(-1.0F, -1.0F, 1.0F);を考慮//

        float f0;
        //f0 = this.rightArmAngleX;
        //this.rightArmAngleX = this.leftArmAngleX;
        //this.leftArmAngleX = f0;

        f0 = this.rightArmAngleY;
        this.rightArmAngleY = this.leftArmAngleY;
        this.leftArmAngleY = f0;

        f0 = this.rightArmAngleZ;
        this.rightArmAngleZ = this.leftArmAngleZ;
        this.leftArmAngleZ = f0;

        f0 = this.rightLegAngleX;
        this.rightLegAngleX = this.leftLegAngleX;
        this.leftLegAngleX = f0;

        f0 = this.rightLegAngleY;
        this.rightLegAngleY = this.leftLegAngleY;
        this.leftLegAngleY = f0;

        f0 = this.rightLegAngleZ;
        this.rightLegAngleZ = this.leftLegAngleZ;
        this.leftLegAngleZ = f0;

        this.headAngleY = -this.headAngleY;
        this.bodyAngleY = -this.bodyAngleY;

        //////////////////////////////////////////////////
    }

    public boolean aimedBow(EntityLivingBase entity) {
        ItemStack heldItem = entity.getHeldItem(EnumHand.MAIN_HAND);
        boolean hasGun = (heldItem != null && heldItem.getItem() instanceof ItemGun);
        boolean usingGun = hasGun && ((EntityNPC) entity).isUsingItem();
        return usingGun;
    }

    public boolean isRiding(EntityLivingBase entity) {
        return entity.isRiding();
    }

    /**
     * @return 常に0
     *
     */
    public int heldItemLeft(EntityLivingBase entity) {
        return 0;
    }

    /**
     * @return 手持ちなし:0, またはuseCount(現状1のみ)
     *
     */
    public int heldItemRight(EntityLivingBase entity) {
        ItemStack heldItem = entity.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return 0;
        } else {
            //player.getItemInUseCount()>0なら3
            return 1;
        }
    }

    public float getSwingProgress(EntityLivingBase entity, float partialTicks) {
        return entity.getSwingProgress(partialTicks);
    }

    public boolean isSneak(EntityLivingBase entity) {
        return entity.isSneaking();
    }

    /*RenderLivingEntity***********************************************************************************************/

    /**
     * 手足の動き(速度)
     */
    public float getRendererArg1(EntityLivingBase entity, float partialTicks) {
        return entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
    }

    /**
     * 手足の動き(合計)
     */
    public float getRendererArg2(EntityLivingBase entity, float partialTicks) {
        return entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
    }

    /**
     * Tick
     */
    public float getRendererArg3(EntityLivingBase entity, float partialTicks) {
        return (float) entity.ticksExisted + partialTicks;
    }

    /**
     * Yaw
     */
    public float getRendererArg4(EntityLivingBase entity, float partialTicks) {
        float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
        float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
        float f2 = f1 - f;

        if (entity.isRiding() && entity.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase riding = (EntityLivingBase) entity.getRidingEntity();
            f = this.interpolateRotation(riding.prevRenderYawOffset, riding.renderYawOffset, partialTicks);
            f2 = f1 - f;
        }
        return f2;
    }

    /**
     * Pitch
     */
    public float getRendererArg5(EntityLivingBase entity, float partialTicks) {
        return entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
    }

    public float interpolateRotation(float par1, float par2, float par3) {
        float f;

        for (f = par2 - par1; f < -180.0F; f += 360.0F) {
            ;
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return par1 + par3 * f;
    }

    public void setupRotateCorpse(EntityLivingBase entity, float partialTicks) {
        float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
        float f8 = this.handleRotationFloat(entity, partialTicks);
        this.rotateCorpse(entity, f8, f, partialTicks);
    }

    public void rotateCorpse(EntityLivingBase entity, float p_77043_2_, float yaw, float partialTicks) {
        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);//バニラでは"180.0F - yaw"

        if (entity.deathTime > 0) {
            float f = (float) NGTMath.firstSqrt(((float) entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F);
            if (f > 1.0F) {
                f = 1.0F;
            }
            GlStateManager.rotate(f * this.getDeathMaxRotation(entity), 0.0F, 0.0F, 1.0F);
        }
    }

    public float handleRotationFloat(EntityLivingBase entity, float partialTicks) {
        return (float) entity.ticksExisted + partialTicks;
    }

    public float getDeathMaxRotation(EntityLivingBase entity) {
        return 90.0F;
    }

    /******************************************************************************************************************/
}