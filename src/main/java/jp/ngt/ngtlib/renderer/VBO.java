package jp.ngt.ngtlib.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;

//今の所未完成
public class VBO extends GLObject implements IRenderer
{
	private static final int NATIVE_BUFFER_SIZE = 0x600000;//0x200000=2097152//速度に影響なし?

    private static final ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(NATIVE_BUFFER_SIZE * 4);
    private static final IntBuffer intBuffer = byteBuffer.asIntBuffer();
    private static final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    private static final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

    private int[] rawBuffer;
    private int rawBufferIndex;
    private int rawBufferSize = 0;
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

    private int vboId, vioId;

	public VBO(int id, int size)
	{
		super(id);
	}

	public void render()
	{
		int stride = 8 * 4;

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.vioId);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		if(this.hasTexture)
        {
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }

        if(this.hasColor)
        {
        	GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        }

        if(this.hasNormals)
        {
        	GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        }

        if(this.hasBrightness)
        {
        	GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);

        GL11.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
		if(this.hasTexture)
        {
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
        }

        if(this.hasColor)
        {
            GL11.glColorPointer(4, GL11.GL_BYTE, stride, 20);
        }

        if(this.hasNormals)
        {
            GL11.glNormalPointer(GL11.GL_BYTE, stride, 24);
        }

        if(this.hasBrightness)
        {
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glTexCoordPointer(2, GL11.GL_SHORT, stride, 28);
            //OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        GL11.glDrawElements(this.drawMode, this.vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        if(this.hasTexture)
        {
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }

        if(this.hasColor)
        {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        }

        if(this.hasNormals)
        {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        }

        if(this.hasBrightness)
        {
            //OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }
	}

	/*@Deprecated
	public void render(boolean compile)
	{
		int offs = 0;
        while(offs < this.vertexCount)
        {
        	int vtc = Math.min(this.vertexCount - offs, NATIVE_BUFFER_SIZE >> 5);
        	if(compile)
        	{
        		this.intBuffer.clear();
                this.intBuffer.put(this.rawBuffer, offs << 3, vtc << 3);
                this.byteBuffer.position(0);
                this.byteBuffer.limit(vtc << 5);
        	}
            offs += vtc;

            if(this.hasTexture)
            {
                this.floatBuffer.position(3);
                GL11.glTexCoordPointer(2, 32, this.floatBuffer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            }

            if(this.hasColor)
            {
                this.byteBuffer.position(20);
                GL11.glColorPointer(4, true, 32, this.byteBuffer);
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            }

            if(this.hasNormals)
            {
                this.byteBuffer.position(24);
                GL11.glNormalPointer(32, this.byteBuffer);
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            }

            if(this.hasBrightness)
            {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                this.shortBuffer.position(14);
                GL11.glTexCoordPointer(2, 32, this.shortBuffer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            this.floatBuffer.position(0);
            GL11.glVertexPointer(3, 32, this.floatBuffer);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDrawArrays(this.drawMode, 0, vtc);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

            if(this.hasTexture)
            {
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            }

            if(this.hasColor)
            {
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            }

            if(this.hasNormals)
            {
                GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            }

            if(this.hasBrightness)
            {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
	}*/

    @Override
    public int draw()
    {
        if(!this.isDrawing)
        {
            throw new IllegalStateException("Not tesselating!");
        }
        else
        {
            this.isDrawing = false;

            this.intBuffer.clear();
            this.intBuffer.put(this.rawBuffer, 0, this.vertexCount * 8);
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.vertexCount * 8 * 4);
            //this.byteBuffer.flip();

            //頂点インデックス
            ByteBuffer vIndexByte = GLAllocation.createDirectByteBuffer(this.vertexCount * 4);
            IntBuffer vIndexInt = vIndexByte.asIntBuffer();
            int[] vIndex = new int[this.vertexCount];
            for(int i = 0; i < this.vertexCount; ++i)
            {
            	vIndex[i] = i;
            }
            vIndexInt.put(vIndex);
            vIndexByte.flip();

            this.vboId = GL15.glGenBuffers();
            this.vioId = GL15.glGenBuffers();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.intBuffer, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.vioId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, vIndexInt, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

            /*if(this.rawBufferSize > 0x20000 && this.rawBufferIndex < (this.rawBufferSize << 3))
            {
            	this.rawBufferSize = 0x10000;
            	this.rawBuffer = new int[this.rawBufferSize];
            }*/

            //this.reset();
            return (this.rawBufferIndex << 2);
        }
    }

    private void reset()
    {
        this.vertexCount = 0;
        this.byteBuffer.clear();
        this.rawBufferIndex = 0;
    }

    @Override
    public void startDrawing(int par1)
    {
        if(this.isDrawing)
        {
            throw new IllegalStateException("Already tesselating!");
        }
        else
        {
            this.isDrawing = true;
            this.reset();
            this.drawMode = par1;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.hasBrightness = false;
        }
    }

    public void setTextureUV(float par1, float par3)
    {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
    }

    @Override
    public void setBrightness(int par1)
    {
        this.hasBrightness = true;
        this.brightness = par1;
    }

    @Override
    public void setColor(int r, int g, int b, int a)
    {
    	r = r > 255 ? 255 : (r < 0 ? 0 : r);
    	g = g > 255 ? 255 : (g < 0 ? 0 : g);
    	b = b > 255 ? 255 : (b < 0 ? 0 : b);
    	a = a > 255 ? 255 : (a < 0 ? 0 : a);

        this.hasColor = true;

        if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
        {
            this.color = a << 24 | b << 16 | g << 8 | r;
        }
        else
        {
            this.color = r << 24 | g << 16 | b << 8 | a;
        }
    }

    @Override
    public void addVertexWithUV(float par1, float par3, float par5, float par7, float par9)
    {
        this.setTextureUV(par7, par9);
        this.addVertex(par1, par3, par5);
    }

    /** Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets full.*/
    public void addVertex(float par1, float par3, float par5)
    {
        if(this.rawBufferIndex >= this.rawBufferSize - 32)
        {
            if (this.rawBufferSize == 0)
            {
            	this.rawBufferSize = 0x10000;
            	this.rawBuffer = new int[this.rawBufferSize];
            }
            else
            {
            	this.rawBufferSize *= 2;
            	this.rawBuffer = Arrays.copyOf(this.rawBuffer, this.rawBufferSize);
            }
        }

        this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits(par1);
        this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits(par3);
        this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits(par5);

        if(this.hasTexture)
        {
            this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits(this.textureU);
            this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits(this.textureV);
        }

        if(this.hasColor)
        {
            this.rawBuffer[this.rawBufferIndex + 5] = this.color;
        }

        if(this.hasNormals)
        {
            this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
        }

        if(this.hasBrightness)
        {
            this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
        }

        this.rawBufferIndex += 8;
        ++this.vertexCount;
    }

    public void setColorOpaque_I(int par1)
    {
        this.setColorRGBA_I(par1, 0xFF);
    }

    public void setColorRGBA_I(int par1, int par2)
    {
        int k = par1 >> 16 & 0xFF;
        int l = par1 >> 8 & 0xFF;
        int i1 = par1 & 0xFF;
        this.setColor(k, l, i1, par2);
    }

    /**
     * Sets the normal for the current draw call.
     */
    @Override
    public void setNormal(float par1, float par2, float par3)
    {
        this.hasNormals = true;
        int b0 = (int)(par1 * 127.0F);
        int b1 = (int)(par2 * 127.0F);
        int b2 = (int)(par3 * 127.0F);
        this.normal = b0 & 0xFF | (b1 & 0xFF) << 8 | (b2 & 0xFF) << 16;
    }
}