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

import net.minecraft.client.renderer.texture.TextureUtil;

import java.awt.image.BufferedImage;

public class AnimatedPicture extends Picture {
    private GIF gifImage;

    protected AnimatedPicture(String par1) {
        super(par1);
    }

    @Override
    protected void loadImage() {
        this.gifImage = GIF.load(this.url);
        this.textureId = TextureUtil.glGenTextures();
        //this.uploadTexture(this.textureId, this.image);
    }

    @Override
    public BufferedImage getImage() {
        if (this.textureId < 0) {
            this.loadImage();
        }

        if (this.gifImage == null) {
            return null;
        }

        int frameIndex = this.gifImage.getCurrentFrameIndex();
        this.image = this.gifImage.getImage(frameIndex);
        this.uploadTexture(this.textureId, this.image);
        return this.image;
    }

    //結合時の描画方式
	/*@Override
	protected void renderImage(float width, float height, boolean fitAspectRatio, BufferedImage image)
	{
		GlStateManager.bindTexture(this.getTextureId());

		int frameIndex = (int)(System.currentTimeMillis() / this.frameRate) % this.frameNum;
		float minU = 0.0F;
		float maxU = 1.0F;
		float minV = (float)frameIndex / (float)this.frameNum;
		float maxV = minV + 1.0F;

		float hw = width * 0.5F;
		float hh = height * 0.5F;
		float depth = 0.0F;
		NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0F - hw, 0.0F + hh, depth, minU, minV);
        tessellator.addVertexWithUV(0.0F - hw, 0.0F - hh, depth, minU, maxV);
        tessellator.addVertexWithUV(0.0F + hw, 0.0F - hh, depth, maxU, maxV);
        tessellator.addVertexWithUV(0.0F + hw, 0.0F + hh, depth, maxU, minV);
        tessellator.draw();
	}*/
}