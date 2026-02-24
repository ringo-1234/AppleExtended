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

package jp.ngt.rtm.entity.train.parts;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.util.RenderUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderSeat extends Render<EntityFloor>
{
	public RenderSeat(RenderManager renderManager)
	{
		super(renderManager);
	}

	private final ModelCrossSeat model = new ModelCrossSeat();
	private static final ResourceLocation texture = new ResourceLocation("rtm", "textures/train/seat.png");

    private final void renderSeat(EntityFloor entity, double par2, double par4, double par6, float par8, float par9)
    {
    	GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);

        RenderUtil.enableCustomLighting(0, 0.0F, 2.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.renderSeat(entity);
        RenderUtil.disableCustomLighting(0);

    	GL11.glPopMatrix();
    }

    private void renderSeat(EntityFloor entity)
    {
    	byte seatType = entity.getSeatType();
    	if(entity.getVehicle() == null)
		{
			RTMCore.proxy.renderMissingModel();
		}
    	else if(seatType == 1 || seatType == 3)
    	{
    		//Light[] lights = entity.getTrain().getTrainModelSet().getConfig().interiorLights;

            GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);

            this.bindTexture(texture);
            //-17~+17
        	this.model.Shape3.rotateAngleX = (float)Math.toRadians((float)entity.getVehicle().seatRotation * 17.0F / (float)EntityVehicleBase.MAX_SEAT_ROTATION);
            this.model.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
    	}
    	else if(seatType == 3)//寝台
    	{
    		;
    	}
    }

    @Override
    public void doRender(EntityFloor par1, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderSeat(par1, par2, par4, par6, par8, par9);
    }

    @Override
	protected ResourceLocation getEntityTexture(EntityFloor entity)
	{
		return this.texture;
	}
}