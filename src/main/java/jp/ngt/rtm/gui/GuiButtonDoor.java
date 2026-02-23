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

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonDoor extends GuiButton
{
	public boolean opened;

	public GuiButtonDoor(int id, int xPos, int yPos, int w, int h)
    {
		super(id, xPos, yPos, w, h, "");
    }

	@Override
	public void drawButton(Minecraft mc, int x, int y, float ptick)
    {
        if(this.visible)
        {
            mc.getTextureManager().bindTexture(TabTrainControlPanel.TAB_Inventory.getTexture());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            int i0 = this.opened ? -10 : -4;
            this.drawTexturedModalRect(this.x + 25, this.y + i0, 242, 80, 14, 100);//スイッチ
            this.drawTexturedModalRect(this.x, this.y, 192, 0, 64, 80);//本体
            int v = this.opened ? 80 : 88;
            this.drawTexturedModalRect(this.x + 44, this.y + 48, 224, v, 8, 8);//ランプ
            this.mouseDragged(mc, x, y);
        }
    }

	@Override
	public void playPressSound(SoundHandler handler)
    {
		RTMCore.proxy.playSound(NGTUtilClient.getMinecraft().player, RTMSound.LEVER, 1.0F, 1.0F);
    }
}