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

package jp.ngt.rtm.block.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.rtm.modelpack.modelset.TextureSetRRS;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRailroadSign extends TileEntitySpecialRenderer<TileEntityRailroadSign>
{
    public void renderRailroadSignAt(TileEntityRailroadSign tileEntity, double par2, double par4,double par6, float par8)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)par2 + 0.5F, (float)par4, (float)par6 + 0.5F);
        GL11.glTranslatef(tileEntity.getOffsetX(), tileEntity.getOffsetY(), tileEntity.getOffsetZ());

        GL11.glPushMatrix();
        float f0 = 1.25F;
        float w = 0.25F;
        float d = 0.0675F;
        boolean flipVertical = !tileEntity.getWorld().isAirBlock(tileEntity.getPos().up());
        if(flipVertical)
        {
            f0 = -0.25F;
        }
        GL11.glTranslatef(0.0F, f0, 0.0F);
        GL11.glRotatef(tileEntity.getRotation(), 0.0F, 1.0F, 0.0F);

        NGTTessellator tessellator = NGTTessellator.instance;
        TextureSetRRS set = tileEntity.getResourceState().getResourceSet();
        this.bindTexture(set.texture);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(w, -w,  d, 1.0F, 1.0F);
        tessellator.addVertexWithUV(w, w,   d, 1.0F, 0.0F);
        tessellator.addVertexWithUV(-w, w,  d, 0.0F, 0.0F);
        tessellator.addVertexWithUV(-w, -w, d, 0.0F, 1.0F);
        tessellator.draw();

        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(0);
        tessellator.addVertexWithUV(-w, -w, d, 0.0F, 1.0F);
        tessellator.addVertexWithUV(-w, w,  d, 0.0F, 0.0F);
        tessellator.addVertexWithUV(w, w,   d, 1.0F, 0.0F);
        tessellator.addVertexWithUV(w, -w,  d, 1.0F, 1.0F);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if(flipVertical)
        {
            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
        }
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(0x404040);
        NGTRenderer.renderPole(tessellator, 0.0625F, 1.5F, false);//UV指定しない
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    @Override
    public void render(TileEntityRailroadSign par1, double par2, double par4, double par6, float par8, int par9, float aplha)
    {
        this.renderRailroadSignAt(par1, par2, par4, par6, par8);
    }
}