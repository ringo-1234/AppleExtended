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

import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.ColorUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InternalButton
{
	private float startX, startY, width, height;
	private int color;
	private String text = "";
	private int textColor = 0xFFFFFF;
	private float textScale = 0.05F;
	private ButtonListner listner;
	public boolean hovered;

	public InternalButton(float startX, float startY, float width, float height)
	{
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

	public InternalButton setText(String text, int color, float scale)
	{
		this.text = text;
		this.textColor = color;
		this.textScale = scale;
		return this;
	}

	public InternalButton setColor(int par1)
	{
		this.color = par1;
		return this;
	}

	public InternalButton setListner(ButtonListner par1)
	{
		this.listner = par1;
		return this;
	}

	public ButtonListner getListner()
	{
		return this.listner;
	}

	public void render(boolean pickMode)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		int color2 = this.hovered ? ColorUtil.multiplicating(this.color, 0xD0D0D0) : this.color;
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawing(GL11.GL_QUADS);
		tessellator.setColorRGBA_I(color2, 0xFF);
		NGTRenderHelper.addQuadGuiFaceWithSize(this.startX, this.startY, this.width, this.height, 0.01F);
		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		if(!pickMode)
		{
			GL11.glPushMatrix();
			GL11.glScalef(this.textScale, -this.textScale, this.textScale);
			GL11.glTranslatef(1.0F, 0.0F, 0.25F);
			FontRenderer fontRenderer = NGTUtilClient.getMinecraft().fontRenderer;
			float x = this.startX / this.textScale;
			float y = (-this.startY / this.textScale) - 10.0F;
			fontRenderer.drawString(this.text, x, y, this.textColor, false);
			GL11.glPopMatrix();
		}
	}

	public static interface ButtonListner
	{
		void onClick(InternalButton button);
	}
}
