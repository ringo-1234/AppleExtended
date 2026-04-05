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
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public final class RenderFluid extends Render<EntityFluid> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/experience_orb.png");

    public RenderFluid(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityFluid entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x, (float) y + EntityFluid.R, (float) z);
        float scale = entity.getNormalizedLife();
        GL11.glScalef(scale, scale, scale);

        int pass = MinecraftForgeClient.getRenderPass();
        boolean isCoke = (entity.getFluidType() == FluidType.COKE);
        if ((pass == 0) || (pass == 1 && isCoke)) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GLHelper.disableLighting();
            GLHelper.setLightmapMaxBrightness();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            if (pass == 1) {
                if (isCoke && entity.getTemperature() >= 1000.0F) {
                    this.renderFire(entity);
                }
            } else {
                if (entity.getFluidType().type == FluidType.Type.SOLID) {
                    switch (entity.getEntityId() % 4) {
                        case 0:
                            GL11.glRotatef(5.0F, 1.0F, 0.0F, 0.0F);
                            break;
                        case 1:
                            GL11.glRotatef(5.0F, 0.0F, 0.0F, 1.0F);
                            break;
                        case 2:
                            GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
                            break;
                        case 3:
                            GL11.glRotatef(-5.0F, 0.0F, 0.0F, 1.0F);
                            break;
                    }
                }
                this.renderMetaball(entity);
            }
            GL11.glShadeModel(GL11.GL_FLAT);
            GLHelper.enableLighting();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }

    private void renderMetaball(EntityFluid entity) {
        float normTemp = entity.getNormalizedTemperture();
        boolean isSolid = (entity.getFluidType().type == FluidType.Type.SOLID);
        int splitW = isSolid ? 4 : 8;
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i < FluidVertexHolder.SPLIT_H; ++i)//縦
        {
            for (int j = 0; j <= splitW; ++j)//横
            {
                int j2 = j % splitW;
                this.addVertex(tessellator, entity, (i + 1) * FluidVertexHolder.SPLIT_W + j2, normTemp);
                this.addVertex(tessellator, entity, i * FluidVertexHolder.SPLIT_W + j2, normTemp);
            }
        }
        tessellator.draw();
    }

    private final void addVertex(NGTTessellator tessellator, EntityFluid fluid, int index, float temp) {
        float[] fa = fluid.fluidVtx.buffer[index];
        tessellator.setColorOpaque_I(fluid.getFluidType().getColor(fa[3], temp));
        tessellator.addVertex(fa[0], fa[1], fa[2]);
    }

    private void renderFire(EntityFluid entity) {
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        double d0 = (((System.currentTimeMillis() + (entity.getEntityId() * 50)) % 5000) / 5000.0D) * 360.0D;
        float tempNormal = entity.getNormalizedTemperture();
        float scale = (((NGTMath.sin((float) d0) + 1.0F) * 0.5F) + 2.0F) * tempNormal;
        int color = 0xAA00FF;
        NGTRenderer.renderFire(EntityFluid.SIZE * 0.8F, EntityFluid.SIZE * scale, color, 5);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFluid entity) {
        return null;
    }
}
