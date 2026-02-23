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

package jp.ngt.ngtlib.renderer.media;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FontImage
{
	/**Client Only*/
	private static final List<FontImage> IMAGE_CACHE = new ArrayList<>();

	private final String text;
	private final String font;
	private final int style;//Font.PLAIN
	private final int color;
	private final int size;

	private int width;
	private int textureId = -1;

	private FontImage(String pText, String pFont, int pStyle, int pColor, int pSize)
	{
		this.text = pText;
		this.font = pFont;
		this.style = pStyle;
		this.color = pColor;
		this.size = pSize;
	}

	public static FontImage create(String pText, String pFont, int pStyle, int pColor, int pSize, boolean isClient)
	{
		if(isClient)
		{
			for(FontImage cache : IMAGE_CACHE)
			{
				if(cache.text.equals(pText) && cache.font.equals(pFont) && cache.style == pStyle && cache.color == pColor && cache.size == pSize)
				{
					return cache;
				}
			}
		}

		FontImage image = new FontImage(pText, pFont, pStyle, pColor, pSize);

		if(isClient)
		{
			image.init();
			IMAGE_CACHE.add(image);
		}

		return image;
	}

	public String getText()
	{
		return this.text;
	}

	public String getFont()
	{
		return this.font;
	}

	public int getStyle()
	{
		return this.style;
	}

	public int getColor()
	{
		return this.color;
	}

	public int getHeight()
	{
		return this.size;
	}

	@SideOnly(Side.CLIENT)
	public int getWidth()
	{
		if(this.width <= 0)
		{
			//フォントサイズから幅計算
			int w = this.getHeight() * this.text.length() * 2;
			BufferedImage bi = new BufferedImage(w, this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics2D g2d = bi.createGraphics();
			Font fontObj = new Font(this.font, this.style, this.size);
			g2d.setFont(fontObj);
			FontMetrics metrics = g2d.getFontMetrics();
			this.width = metrics.stringWidth(this.text);
		}
		return this.width;
	}

	//https://www.cresco.co.jp/blog/entry/91/
	@SideOnly(Side.CLIENT)
	private void init()
	{
		BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2d = bi.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

		Font fontObj = new Font(this.font, this.style, this.size);
		g2d.setFont(fontObj);
		g2d.setColor(new Color(this.color));
	    FontMetrics metrics = g2d.getFontMetrics();
		g2d.drawString(this.text, 0, this.getHeight() - metrics.getDescent());

		this.textureId = TextureUtil.glGenTextures();
		TextureUtil.uploadTextureImage(this.textureId, bi);
	}

	@SideOnly(Side.CLIENT)
	public void render(float x, float y, float z, float w, float h, float minU, float minV, float maxU, float maxV)
	{
		this.bindTexture();
		NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + w, y + h, z, maxU, maxV);
        tessellator.addVertexWithUV(x + w, y,     z, maxU, minV);
        tessellator.addVertexWithUV(x,     y,     z, minU, minV);
        tessellator.addVertexWithUV(x,     y + h, z, minU, maxV);
        tessellator.draw();
	}

	@SideOnly(Side.CLIENT)
	protected void bindTexture()
	{
		GlStateManager.bindTexture(this.textureId);//こっち使わないと後から描画するやつのbindTexが効かなくなる
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
	}

	//キャッシュの内容どうするか?
	/*@SideOnly(Side.CLIENT)
	public void deleteTexture()
	{
		GL11.glDeleteTextures(this.textureId);
		this.textureId = -1;
	}*/
}