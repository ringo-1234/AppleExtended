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
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.TextureSetBase;
import jp.ngt.rtm.modelpack.modelset.TextureSetSignboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonSelectTexture extends GuiButton {
    public final ResourceType type;
    public final TextureSetBase property;
    private int movement;

    public GuiButtonSelectTexture(int id, int xPos, int yPos, int w, int h, ResourceType par4, TextureSetBase par5) {
        super(id, xPos, yPos, w, h, "");
        this.type = par4;
        this.property = par5;
    }

    @Override
    public void drawButton(Minecraft mc, int par2, int par3, float ptick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(this.property.texture);
        this.draw();
    }

    private void draw() {
        float maxV = 1.0F;
        if (this.type.equals(RTMResource.SIGNBOARD)) {
            int frame = ((TextureSetSignboard) this.property).getConfig().frame;
            if (frame > 1) {
                maxV = 1.0F / (float) ((TextureSetSignboard) this.property).getConfig().frame;
            }
        }
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(this.x + this.width, this.y + this.height, this.zLevel, 1.0F, maxV);
        tessellator.addVertexWithUV(this.x + this.width, this.y, this.zLevel, 1.0F, 0.0F);
        tessellator.addVertexWithUV(this.x, this.y, this.zLevel, 0.0F, 0.0F);
        tessellator.addVertexWithUV(this.x, this.y + this.height, this.zLevel, 0.0F, maxV);
        tessellator.draw();
    }

    public void moveButton(int moveY) {
        this.y -= moveY;
    }
}