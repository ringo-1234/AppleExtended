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

package jp.ngt.rtm.entity.vehicle;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderTrolley extends Render<EntityTrolley>
{
	public RenderTrolley(RenderManager renderManager)
	{
		super(renderManager);
	}

	private void renderVehicle(EntityTrolley vehicle, double par2, double par4, double par6, float entityYaw, float par9)
    {
		GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //partialTick補完するとガクガク(1.9)
        double x = par2;// + (vehicle.posX - vehicle.prevPosX) * par9;
        double y = par4;// + (vehicle.posY - vehicle.prevPosY) * par9;
        double z = par6;// + (vehicle.posZ - vehicle.prevPosZ) * par9;
        GL11.glTranslatef((float)x, (float)y, (float)z);

        //バニラのトロッコは運動ベクトルからyaw算出してるようだ
        double d0 = vehicle.lastTickPosX + (vehicle.posX - vehicle.lastTickPosX) * (double)par9;
        double d1 = vehicle.lastTickPosY + (vehicle.posY - vehicle.lastTickPosY) * (double)par9;
        double d2 = vehicle.lastTickPosZ + (vehicle.posZ - vehicle.lastTickPosZ) * (double)par9;
        double d3 = 0.30000001192092896D;
        Vec3d vec3d = vehicle.getPos(d0, d1, d2);
        float entityPitch = vehicle.prevRotationPitch + (vehicle.rotationPitch - vehicle.prevRotationPitch) * par9;
        if (vec3d != null)
        {
            Vec3d vec3d1 = vehicle.getPosOffset(d0, d1, d2, 0.30000001192092896D);
            Vec3d vec3d2 = vehicle.getPosOffset(d0, d1, d2, -0.30000001192092896D);

            if (vec3d1 == null)
            {
                vec3d1 = vec3d;
            }

            if (vec3d2 == null)
            {
                vec3d2 = vec3d;
            }

            x += vec3d.x - d0;
            y += (vec3d1.y + vec3d2.y) / 2.0D - d1;
            z += vec3d.z - d2;
            Vec3d vec3d3 = vec3d2.addVector(-vec3d1.x, -vec3d1.y, -vec3d1.z);

            if (vec3d3.lengthVector() != 0.0D)
            {
                vec3d3 = vec3d3.normalize();
                entityYaw = (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0D / Math.PI);
                entityPitch = (float)(Math.atan(vec3d3.y) * 73.0D);
            }
        }

        //float yaw =  vehicle.prevRotationYaw + NGTMath.wrapAngle(vehicle.rotationYaw - vehicle.prevRotationYaw) * par9;
        //GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(90.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        //float pitch =  vehicle.prevRotationPitch + (vehicle.rotationPitch - vehicle.prevRotationPitch) * par9;
        //GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-entityPitch, 1.0F, 0.0F, 0.0F);
        //float roll = vehicle.prevRotationRoll + (vehicle.rotationRoll - vehicle.prevRotationRoll) * par9;
        //GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);

        ModelSetVehicleBase modelSet = (ModelSetVehicleBase)vehicle.getResourceState().getResourceSet();
        if(modelSet != null)
        {
            float[] fa = modelSet.getConfig().offset;
        	GL11.glTranslated(fa[0], fa[1], fa[2]);
        	int pass = MinecraftForgeClient.getRenderPass();
            modelSet.modelObj.render(vehicle, modelSet.getConfig(), pass, par9);
        }
        else
        {
        	RTMCore.proxy.renderMissingModel();
        }

        GL11.glPopMatrix();
    }

	@Override
    public void doRender(EntityTrolley par1, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderVehicle(par1, par2, par4, par6, par8, par9);
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityTrolley entity)
	{
		return null;
	}
}
