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

package jp.ngt.rtm.gui;

import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonColored extends GuiButton {
    private int color;
    private int textColor;

    public GuiButtonColored(int id, int xPos, int yPos, int w, int h, String text, int c1, int c2) {
        super(id, xPos, yPos, w, h, text);
        this.color = c1;
        this.textColor = c2;
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y, float ptick) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.renderBackground();
            this.mouseDragged(mc, x, y);

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, this.textColor);
        }
    }

    private void renderBackground() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(this.color);
        tessellator.addVertex(this.x, this.y + this.height, this.zLevel);
        tessellator.addVertex(this.x + this.width, this.y + this.height, this.zLevel);
        tessellator.addVertex(this.x + this.width, this.y, this.zLevel);
        tessellator.addVertex(this.x, this.y, this.zLevel);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}