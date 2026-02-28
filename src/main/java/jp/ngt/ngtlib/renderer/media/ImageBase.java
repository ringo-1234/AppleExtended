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

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ImageBase extends MediaBase {
    private ByteBuffer buffer;
    private int[] intBuffer;
    private float[] uvBuf = {0.0F, 0.0F, 1.0F, 1.0F};

    public abstract @Nullable BufferedImage getImage();

    public abstract int getTextureId();

    public void setUV(float minU, float minV, float maxU, float maxV) {
        this.uvBuf[0] = minU;
        this.uvBuf[1] = minV;
        this.uvBuf[2] = maxU;
        this.uvBuf[3] = maxV;
    }

    @Override
    public void render(float width, float height, boolean fitAspectRatio) {
        BufferedImage image = this.getImage();
        if (image != null) {
            this.renderImage(width, height, fitAspectRatio, image);
        }
    }

    @Override
    public void exit() {
    }

    protected void renderImage(float width, float height, boolean fitAspectRatio, BufferedImage image) {
        GlStateManager.bindTexture(this.getTextureId());

        float minU = this.uvBuf[0];
        float maxU = this.uvBuf[2];
        float minV = this.uvBuf[1];
        float maxV = this.uvBuf[3];

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
    }

    protected void uploadTexture(int id, BufferedImage image) {
        //TextureUtil.uploadTextureImage(id, image);
        this.uploadTextureGL(id, image);
    }

    private void uploadTextureGL(int id, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (this.intBuffer == null) {
            this.intBuffer = new int[width * height];
            TextureUtil.allocateTexture(id, width, height);
        }

        switch (image.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
                this.readAsIntARGB(image);
                break;
            case BufferedImage.TYPE_INT_RGB://キャプチャ
                this.readAsIntRGB(image);
                break;
            case BufferedImage.TYPE_BYTE_INDEXED://GIF
                this.readAsByteIndexed(image);
                break;
            case BufferedImage.TYPE_CUSTOM://WebCam
                this.readAsCustom(image);
                break;
            default:
                NGTLog.debug("Unsupported type : " + image.getType());
                return;
        }

        TextureUtil.uploadTexture(id, this.intBuffer, width, height);
    }

    @Deprecated
    private void uploadTextureGL2(int id, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        GlStateManager.bindTexture(id);

        if (this.buffer == null) {
            this.buffer = BufferUtils.createByteBuffer(width * height * 4);
            this.buffer.order(ByteOrder.nativeOrder());
            //level:mipmap
            //border:境界線の太さ
            //テクスチャ生成
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.buffer);
        }

        this.buffer.clear();
        //DataBufferByte  dataBuf = (DataBufferByte)image.getRaster().getDataBuffer();
        switch (image.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
                this.readAsIntARGB(image);
                break;
            case BufferedImage.TYPE_INT_RGB:
                this.readAsIntRGB(image);
                break;
            case BufferedImage.TYPE_BYTE_INDEXED:
                this.readAsByteIndexed(image);
                break;
            default:
                this.readAsIntARGB(image);
        }
        this.buffer.flip();

        //テクスチャ上書き(再生成より軽い)
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.buffer);
    }

    private void readAsIntARGB(BufferedImage image) {
        DataBuffer dataBuf = image.getRaster().getDataBuffer();
        for (int i = 0; i < dataBuf.getSize(); i++) {
            int color = dataBuf.getElem(i);
            byte a = (byte) (color >> 24 & 0xFF);
            byte r = (byte) (color >> 16 & 0xFF);
            byte g = (byte) (color >> 8 & 0xFF);
            byte b = (byte) (color & 0xFF);

			/*this.buffer.put(a);
			this.buffer.put(b);
			this.buffer.put(g);
			this.buffer.put(r);*/

            this.intBuffer[i] = color;
        }
    }

    /**
     * デスクトップキャプチャなど
     */
    private void readAsIntRGB(BufferedImage image) {
        DataBufferInt dataBuf = (DataBufferInt) image.getRaster().getDataBuffer();
		/*for(int i = 0; i < dataBuf.getSize(); i++)
		{
			int color = dataBuf.getElem(i);
			this.intBuffer[i] = color | 0xFF000000;
		}*/
        //System.arraycopy(dataBuf.getData(), 0, this.intBuffer, 0, this.intBuffer.length);
        this.intBuffer = dataBuf.getData();
    }

    /**
     * GIFなど
     */
    private void readAsByteIndexed(BufferedImage image) {
        IndexColorModel model = (IndexColorModel) image.getColorModel();
        DataBufferByte dataBuf = (DataBufferByte) image.getRaster().getDataBuffer();
        for (int i = 0; i < dataBuf.getSize(); i++) {
            int index = dataBuf.getElem(i);
            int color = model.getRGB(index);
            this.intBuffer[i] = color;
        }
    }

    /**
     * Webカメラなど
     */
    private void readAsCustom(BufferedImage image) {
        DataBufferByte dataBuf = (DataBufferByte) image.getRaster().getDataBuffer();
        for (int i = 0; i < dataBuf.getSize(); i += 3) {
            int r = dataBuf.getElem(i);
            int g = dataBuf.getElem(i + 1);
            int b = dataBuf.getElem(i + 2);
            this.intBuffer[i / 3] = 0xFF000000 | (r << 16) | (g << 8) | b;
        }
    }
}