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

package jp.ngt.rtm.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.rtm.modelpack.cfg.MachineConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityInstalledObject extends Render<EntityInstalledObject>
{
    public RenderEntityInstalledObject(RenderManager renderManager)
    {
		super(renderManager);
	}

    /*@Override
    public boolean isStaticEntity()//DisplayListに入れられる
    {
        return true;//たまにStack overflow
    }*/

    @Override
    public void doRender(EntityInstalledObject entity, double par2, double par4, double par6, float par8, float par9)
    {
    	GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);

        ModelSetMachine modelSet = entity.getResourceState().getResourceSet();
        MachineConfig cfg = modelSet.getConfig();
        if(cfg.followRailAngle)
        {
        	GL11.glRotatef(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
        	GL11.glRotatef(entity.rotationRoll, 0.0F, 0.0F, 1.0F);
        }
        int pass = MinecraftForgeClient.getRenderPass();
        modelSet.modelObj.render(entity, cfg, pass, par9);

        GL11.glPopMatrix();
    }

    @Override
	protected ResourceLocation getEntityTexture(EntityInstalledObject entity)
	{
		return null;
	}
}