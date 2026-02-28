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

import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.rtm.block.tt.SignboardText;
import jp.ngt.rtm.modelpack.cfg.SignboardConfig;
import jp.ngt.rtm.modelpack.modelset.TextureSetSignboard;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderSignBoard extends TileEntitySpecialRenderer<TileEntitySignBoard> {
    public void renderSignBoardAt(TileEntitySignBoard tileEntity, double par2, double par4, double par6, float par8) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) par2 + 0.5F, (float) par4 + 0.5F, (float) par6 + 0.5F);

        TextureSetSignboard set = tileEntity.getResourceState().getResourceSet();
        SignboardConfig cfg = set.getConfig();
        float height = cfg.height / 2.0F;
        float width = cfg.width / 2.0F;
        float depth = cfg.depth / 2.0F;
        int meta = tileEntity.getBlockMetadata();
        byte dir = tileEntity.getDirection();
        float minV = 0.0F;
        float maxV = 1.0F;
        if (cfg.frame > 1) {
            minV = (float) (tileEntity.counter / cfg.animationCycle) / ((float) cfg.frame);
            maxV = (float) ((tileEntity.counter / cfg.animationCycle) + 1) / ((float) cfg.frame);
        }

        GL11.glRotatef((float) dir * -90.0F, 0.0F, 1.0F, 0.0F);

        if (meta == 0) {
            GL11.glTranslatef(0.0F, 0.5F - height, 0.0F);//up
        } else if (meta == 1) {
            GL11.glTranslatef(0.0F, height - 0.5F, 0.0F);//down
        } else {
            if ((dir == 1 && meta == 4) || (dir == 3 && meta == 5)) {
                GL11.glTranslatef(0.0F, 0.0F, depth - 0.5F);
            } else if ((dir == 0 && meta == 3) || (dir == 2 && meta == 2)) {
                GL11.glTranslatef(0.0F, 0.0F, depth - 0.5F);
            } else if ((dir == 1 && meta == 3) || (dir == 3 && meta == 2)) {
                GL11.glTranslatef(width - 0.5F, 0.0F, 0.0F);
            } else if ((dir == 0 && meta == 4) || (dir == 2 && meta == 5)) {
                GL11.glTranslatef(0.5F - width, 0.0F, 0.0F);
            } else if ((dir == 0 && meta == 5) || (dir == 2 && meta == 4)) {
                GL11.glTranslatef(width - 0.5F, 0.0F, 0.0F);
            } else {
                GL11.glTranslatef(0.5F - width, 0.0F, 0.0F);
            }
        }

        GL11.glTranslatef(tileEntity.getOffsetX(), tileEntity.getOffsetY(), tileEntity.getOffsetZ());
        GL11.glRotatef(tileEntity.getRotation(), 0.0F, 1.0F, 0.0F);

        GL11.glDisable(GL11.GL_LIGHTING);
        NGTTessellator tessellator = NGTTessellator.instance;
        this.bindTexture(set.texture);
        float u0 = cfg.backTexture == 1 ? 0.5F : 1.0F;
        float u1 = cfg.backTexture == 1 ? 0.5F : 0.0F;
        tessellator.startDrawingQuads();
        //Front
        tessellator.addVertexWithUV(width, -height, depth, u0, maxV);
        tessellator.addVertexWithUV(width, height, depth, u0, minV);
        tessellator.addVertexWithUV(-width, height, depth, 0.0F, minV);
        tessellator.addVertexWithUV(-width, -height, depth, 0.0F, maxV);

        int color = cfg.color;
        boolean flag1 = false;
        if (cfg.backTexture == 2) {
            tessellator.draw();
            flag1 = true;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(color, 255);
        }

        //Back
        tessellator.addVertexWithUV(-width, -height, -depth, 1.0F, maxV);
        tessellator.addVertexWithUV(-width, height, -depth, 1.0F, minV);
        tessellator.addVertexWithUV(width, height, -depth, u1, minV);
        tessellator.addVertexWithUV(width, -height, -depth, u1, maxV);
        tessellator.draw();

        color -= 0x101010;//影
        if (color < 0) {
            color = 0;
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(color, 255);
        //Top
        tessellator.addVertex(width, height, depth);
        tessellator.addVertex(width, height, -depth);
        tessellator.addVertex(-width, height, -depth);
        tessellator.addVertex(-width, height, depth);
        //Bottom
        tessellator.addVertex(-width, -height, depth);
        tessellator.addVertex(-width, -height, -depth);
        tessellator.addVertex(width, -height, -depth);
        tessellator.addVertex(width, -height, depth);
        //Left
        tessellator.addVertex(width, -height, -depth);
        tessellator.addVertex(width, height, -depth);
        tessellator.addVertex(width, height, depth);
        tessellator.addVertex(width, -height, depth);
        //Right
        tessellator.addVertex(-width, -height, depth);
        tessellator.addVertex(-width, height, depth);
        tessellator.addVertex(-width, height, -depth);
        tessellator.addVertex(-width, -height, -depth);

        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        for (SignboardText text : tileEntity.getResourceState().texts) {
            if (cfg.backTexture != 1 || text.posU < width) {
                //表
                text.render(text.posU - width, text.posV - height, depth + 0.01F, 1.0F);
            }

            if (cfg.backTexture == 0 || (cfg.backTexture == 1 && text.posU >= width)) {
                //裏
                GL11.glPushMatrix();
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                float x = text.posU - width;
                if (cfg.backTexture == 1) {
                    x -= cfg.width;
                }
                text.render(x, text.posV - height, depth + 0.01F, 1.0F);
                GL11.glPopMatrix();
            }
        }
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    @Override
    public void render(TileEntitySignBoard tileEntity, double par2, double par4, double par6, float par8, int par9, float alpha) {
        this.renderSignBoardAt(tileEntity, par2, par4, par6, par8);
    }
}