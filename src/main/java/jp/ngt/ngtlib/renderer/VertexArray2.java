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

package jp.ngt.ngtlib.renderer;

import jp.ngt.ngtlib.io.NGTLog;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 旧Tessellatorを非static化して再利用可能にしたもの<br>
 * v2:速度向上検討版<br>
 *
 */
public final class VertexArray2 extends GLObject implements IRenderer {
    private static final int BUF_INIT_SIZE = 0x8000;

    private boolean initialized = false;
    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private FloatBuffer floatBuffer;
    private ShortBuffer shortBuffer;

    private final List<Integer> rawBuf = new ArrayList<>();
    private int rawBufferSize = BUF_INIT_SIZE;
    private int[] rawBuffer = new int[this.rawBufferSize];
    private int rawBufferIndex;
    private int vertexCount;

    private int drawMode;
    private boolean isDrawing;

    private float textureU, textureV;
    private int color;
    private int normal;
    private int brightness;

    private boolean hasTexture;
    private boolean hasColor;
    private boolean hasNormals;
    private boolean hasBrightness;

    public VertexArray2(int id) {
        super(id);
    }

    private void setupVertexArray() {
        int sizeInt = this.vertexCount * 8;

        if (!this.initialized) {
            this.initBuffer(sizeInt);
        }

        try {
            this.intBuffer.clear();
            int rem = this.intBuffer.remaining();
            if (sizeInt > rem) {
                NGTLog.debug("[VertexArray2] SizeOver(%d > %d)", sizeInt, rem);
                //sizeInt = rem;
                this.initBuffer(sizeInt);
            }
            this.intBuffer.put(this.rawBuffer, 0, sizeInt);
            this.byteBuffer.position(0);
            this.byteBuffer.limit(sizeInt);
        } catch (BufferOverflowException e) {
            NGTLog.debug("[VertexArray2] Overflow : IntBuf(%d), RawBuf(%d), SizeInt(%d)",
                    this.intBuffer.remaining(), this.rawBuffer.length, sizeInt);
            throw e;
        }

        this.rawBufferSize = BUF_INIT_SIZE;
        this.rawBuffer = new int[this.rawBufferSize];
        this.rawBufferIndex = 0;
    }

    private void initBuffer(int sizeInt) {
        this.initialized = true;
        int sizeByte = sizeInt * 4;
        this.byteBuffer = GLAllocation.createDirectByteBuffer(sizeByte);
        this.intBuffer = this.byteBuffer.asIntBuffer();
        this.floatBuffer = this.byteBuffer.asFloatBuffer();
        this.shortBuffer = this.byteBuffer.asShortBuffer();
        NGTLog.debug("[VertexArray2] CreateBuf(%d KB)", (sizeByte >> 10));
    }

    public void render() {
        if (!this.initialized) {
            return;
        }

        if (this.hasTexture) {
            this.floatBuffer.position(3);
            GL11.glTexCoordPointer(2, 32, this.floatBuffer);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }

        if (this.hasColor) {
            this.byteBuffer.position(20);
            GL11.glColorPointer(4, true, 32, this.byteBuffer);
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        }

        if (this.hasNormals) {
            this.byteBuffer.position(24);
            GL11.glNormalPointer(32, this.byteBuffer);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        }

        if (this.hasBrightness) {
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            this.shortBuffer.position(14);
            GL11.glTexCoordPointer(2, 32, this.shortBuffer);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        this.floatBuffer.position(0);
        GL11.glVertexPointer(3, 32, this.floatBuffer);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

        if (this.hasTexture) {
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }

        if (this.hasColor) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        }

        if (this.hasNormals) {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        }

        if (this.hasBrightness) {
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }
    }

    public void cancel() {
        this.isDrawing = false;
        this.rawBufferSize = BUF_INIT_SIZE;
        this.rawBuffer = new int[this.rawBufferSize];
        this.rawBufferIndex = 0;
    }

    @Override
    public int draw() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        } else {
            this.isDrawing = false;
            this.setupVertexArray();
            return this.rawBufferIndex << 2;
        }
    }

    private void reset() {
        this.vertexCount = 0;
        this.rawBufferIndex = 0;
    }

    @Override
    public void startDrawing(int par1) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already tesselating!");
        } else {
            this.isDrawing = true;
            this.reset();
            this.drawMode = par1;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.hasBrightness = false;
        }
    }

    public void setTextureUV(float par1, float par3) {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
    }

    @Override
    public void setBrightness(int par1) {
        this.hasBrightness = true;
        this.brightness = par1;
    }

    @Override
    public void setColor(int r, int g, int b, int a) {
        r = r > 255 ? 255 : (r < 0 ? 0 : r);
        g = g > 255 ? 255 : (g < 0 ? 0 : g);
        b = b > 255 ? 255 : (b < 0 ? 0 : b);
        a = a > 255 ? 255 : (a < 0 ? 0 : a);

        this.hasColor = true;

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.color = a << 24 | b << 16 | g << 8 | r;
        } else {
            this.color = r << 24 | g << 16 | b << 8 | a;
        }
    }

    @Override
    public void addVertexWithUV(float par1, float par3, float par5, float par7, float par9) {
        this.setTextureUV(par7, par9);
        this.addVertex(par1, par3, par5);
    }

    /**
     * Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets full.
     */
    public void addVertex(float par1, float par3, float par5) {
        if (this.rawBufferIndex >= this.rawBufferSize - 32) {
            if (this.rawBufferSize == 0) {
                this.rawBufferSize = BUF_INIT_SIZE;
                this.rawBuffer = new int[this.rawBufferSize];
            } else {
                this.rawBufferSize *= 2;
                this.rawBuffer = Arrays.copyOf(this.rawBuffer, this.rawBufferSize);
            }
        }

        this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits(par1);
        this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits(par3);
        this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits(par5);

        if (this.hasTexture) {
            this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits(this.textureU);
            this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits(this.textureV);
        }

        if (this.hasColor) {
            this.rawBuffer[this.rawBufferIndex + 5] = this.color;
        }

        if (this.hasNormals) {
            this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
        }

        if (this.hasBrightness) {
            this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
        }

        this.rawBufferIndex += 8;
        ++this.vertexCount;
    }

    public void setColorOpaque_I(int par1) {
        this.setColorRGBA_I(par1, 0xFF);
    }

    public void setColorRGBA_I(int par1, int par2) {
        int k = par1 >> 16 & 0xFF;
        int l = par1 >> 8 & 0xFF;
        int i1 = par1 & 0xFF;
        this.setColor(k, l, i1, par2);
    }

    @Override
    public void setNormal(float par1, float par2, float par3) {
        this.hasNormals = true;
        int b0 = (int) (par1 * 127.0F);
        int b1 = (int) (par2 * 127.0F);
        int b2 = (int) (par3 * 127.0F);
        this.normal = b0 & 0xFF | (b1 & 0xFF) << 8 | (b2 & 0xFF) << 16;
    }
}