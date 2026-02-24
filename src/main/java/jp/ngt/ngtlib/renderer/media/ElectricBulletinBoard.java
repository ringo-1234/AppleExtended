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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.util.ResourceLocation;

public class ElectricBulletinBoard extends MediaBase
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("ngtlib", "textures/ebb/electric_bulletin_board.png");

	private final String name;
	private int offsetU, offsetV, resolution;

	/**[frame][w*h]*/
	private byte[][] colorData;
	/**[colorIndex]*/
	private int[] palette;

	private GIF rawData;

	public ElectricBulletinBoard(String par1)
	{
		this.name = par1;
	}

	public void setParameter(int resolution, int offsetU, int offsetV)
	{
		this.offsetU = offsetU;
		this.offsetV = offsetV;
		this.resolution = resolution;
	}

	private void loadImage()
	{
		GIF gif = GIF.load(this.name);

		if(gif == null)
		{
			return;
		}

		this.rawData = gif;
		List<Integer> list = new ArrayList<>();
		this.colorData = new byte[gif.frameNum][];
		for(int i = 0; i < this.colorData.length; ++i)
		{
			this.colorData[i] = new byte[gif.width * gif.height];
			BufferedImage img = gif.getImage(i);
			for(int w = 0; w < gif.width; ++w)
			{
				for(int h = 0; h < gif.height; ++h)
				{
					int rawColor = img.getRGB(w, h);
					int color = rawColor & 0xFFFFFF;//ARGBからAを除去
					int colorIndex;
					if(list.contains(color))
					{
						colorIndex = list.indexOf(color);
					}
					else
					{
						colorIndex = list.size();
						list.add(color);
						//NGTLog.debug("[EBB] Add color : %x->%d", rawColor, colorIndex);
					}
					int index = getIndex(w, h);
					this.colorData[i][index] = (byte)((colorIndex & 0xFF) + Byte.MIN_VALUE);
				}
			}
		}

		this.palette = new int[list.size()];
		for(int i = 0; i < this.palette.length; ++i)
		{
			this.palette[i] = list.get(i);
		}
	}

	private int getIndex(int w, int h)
	{
		return w + h * this.rawData.width;
	}

	@Override
	public void render(float width, float height, boolean fitAspectRatio)
	{
		if(this.colorData == null)
		{
			this.loadImage();
			if(this.colorData == null)
			{
				return;
			}
		}

		NGTUtilClient.bindTexture(TEXTURE);

		int sizeX = (int)(this.resolution * width);
		int sizeY = (int)(this.resolution * height);
		float resf = 1.0F / (float)this.resolution;
		float hw = width * 0.5F;
		float hh = height * 0.5F;
		float depth = 0.0F;
		int frameIndex = this.rawData.getCurrentFrameIndex();

		NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        for(int u = 0; u < sizeX; ++u)
        {
        	int u2 = (u + this.offsetU) % this.rawData.width;
        	float uf = (float)u * resf;

        	for(int v = 0; v < sizeY; ++v)
        	{
        		int v2 = (v + this.offsetV) % this.rawData.height;
        		int pixelIndex = this.getIndex(u2, v2);
        		int colorIndex = this.colorData[frameIndex][pixelIndex] - Byte.MIN_VALUE;
        		int color = this.palette[colorIndex];
        		tessellator.setColorRGBA_I(color, 0xFF);
        		float vf = (float)v * resf;
        		float uOffset = color > 0 ? 0.5F : 0.0F;
        		tessellator.addVertexWithUV(-hw + uf,        hh - vf,        depth, 0.0F + uOffset, 0.0F);
                tessellator.addVertexWithUV(-hw + uf,        hh - vf - resf, depth, 0.0F + uOffset, 1.0F);
                tessellator.addVertexWithUV(-hw + uf + resf, hh - vf - resf, depth, 0.5F + uOffset, 1.0F);
                tessellator.addVertexWithUV(-hw + uf + resf, hh - vf,        depth, 0.5F + uOffset, 0.0F);
        	}
        }
        tessellator.draw();
	}

	@Override
	public void exit()
	{
		;
	}
}
