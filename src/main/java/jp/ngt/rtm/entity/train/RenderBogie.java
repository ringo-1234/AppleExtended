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

package jp.ngt.rtm.entity.train;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderBogie extends Render<EntityBogie>
{
	public RenderBogie(RenderManager renderManager)
	{
		super(renderManager);
	}

	private final void renderBogie(EntityBogie bogie, double par2, double par4, double par6, float entityYaw, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		//台車は線形補間したほうが滑らか(1.12
		//partialTickはMinecraft.timerで算出

		double x = par2;
		double y = par4;
		double z = par6;
		if(bogie.getTrain() != null)
		{
			EntityTrainBase train = bogie.getTrain();
			//RenderMngで足されてる補完値を引く
			double bogieFX = bogie.lastTickPosX + (bogie.posX - bogie.lastTickPosX) * (double)partialTick;
			double bogieFY = bogie.lastTickPosY + (bogie.posY - bogie.lastTickPosY) * (double)partialTick;
			double bogieFZ = bogie.lastTickPosZ + (bogie.posZ - bogie.lastTickPosZ) * (double)partialTick;

			float[][] pos = train.getResourceState().getResourceSet().getConfig().getBogiePos();
			int bogieIndex = bogie.getBogieId();
			Vec3 v31 = new Vec3(pos[bogieIndex][0], pos[bogieIndex][1], pos[bogieIndex][2]);
			v31 = v31.rotateAroundX(train.prevRotationPitch + NGTMath.wrapAngle(train.rotationPitch - train.prevRotationPitch) * partialTick);
			v31 = v31.rotateAroundY(train.prevRotationYaw + NGTMath.wrapAngle(train.rotationYaw - train.prevRotationYaw) * partialTick);
			double newX = v31.getX() + (train.lastTickPosX + ((train.posX - train.lastTickPosX) * partialTick));
			double newY = v31.getY() + (train.lastTickPosY + ((train.posY - train.lastTickPosY) * partialTick));
			double newZ = v31.getZ() + (train.lastTickPosZ + ((train.posZ - train.lastTickPosZ) * partialTick));
			x = par2 - bogieFX + newX;
			y = par4 - bogieFY + newY;
			z = par6 - bogieFZ + newZ;
		}

		//NGTLog.debug("(R) %d x%5.1f z%5.1f", bogie.getEntityId(), x, z);

        GL11.glTranslatef((float)x, (float)y + EntityTrainBase.TRAIN_HEIGHT, (float)z);
        float yaw =  bogie.prevRotationYaw + NGTMath.wrapAngle(bogie.rotationYaw - bogie.prevRotationYaw) * partialTick;
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        float pitch =  bogie.prevRotationPitch + (bogie.rotationPitch - bogie.prevRotationPitch) * partialTick;
        GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
        float roll = bogie.prevRotationRoll + (bogie.rotationRoll - bogie.prevRotationRoll) * partialTick;
        GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);

        byte index = bogie.getBogieId();
        boolean flag = true;
        if(bogie.getTrain() != null)
        {
        	ModelSetTrain modelset = bogie.getTrain().getResourceState().getResourceSet();
            if(!modelset.isDummy())
            {
            	VehicleBaseConfig cfg = modelset.getConfig();
            	modelset.bogieModels[index].render(bogie, cfg, 0, partialTick);
            	flag = false;
            }
        }

        if(flag)
        {
        	RTMCore.proxy.renderMissingModel();
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(EntityBogie par1, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBogie(par1, par2, par4, par6, par8, par9);
    }

    @Override
	protected ResourceLocation getEntityTexture(EntityBogie entity)
	{
		return new ResourceLocation("textures/train/bogie.png");
	}
}