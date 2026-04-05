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
import jp.ngt.rtm.modelpack.modelset.ModelSetContainer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderContainer extends Render<EntityContainer> {
    public RenderContainer(RenderManager renderManager) {
        super(renderManager);
    }

    private final void renderContainer(EntityContainer entity, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-entity.rotationPitch, 1.0F, 0.0F, 0.0F);
        ModelSetContainer modelSet = entity.getResourceState().getResourceSet();
        if (modelSet == null || modelSet.isDummy()) {
            RTMCore.proxy.renderMissingModel();
        } else {
            int pass = MinecraftForgeClient.getRenderPass();
            modelSet.modelObj.render(entity, modelSet.getConfig(), pass, par8);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(EntityContainer par1, double par2, double par4, double par6, float par8, float par9) {
        this.renderContainer(par1, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityContainer par1) {
        return null;
    }

    @Override
    protected boolean bindEntityTexture(EntityContainer entiy) {
        return false;
    }
}