package jp.ngt.ngtlib.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.glu.Project;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.Locker;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GLHelper
{
	public static final GLHelper INSTANCE = new GLHelper();
	public static final Locker LOCKER = new Locker();

	private List<GLObject> activeGLLists = new ArrayList<>();
	private List<GLObject> deleteGLLists = new ArrayList<>();

	private GLHelper(){}

	public static void checkGLError(String par1)
    {
        checkGLError(par1, false);
    }

	public static void checkGLError(String par1, boolean par2)
    {
		int i = GL11.glGetError();
        if(i != 0)
        {
        	if(par2){return;}
            NGTLog.debug("GL_ERROR" + "@" + par1);
            //NGTLog.debug(i + ": " + GLU.gluErrorString(i));
            NGTLog.debug(i + ": " + Util.translateGLErrorString(i));
            //GL43.glDebugMessageCallback(new KHRDebugCallback());
            //int program = GL20.glCreateProgram();
            //NGTLog.debug(GL20.glGetProgramInfoLog(program, 256));
            //NGTLog.debug(GL20.glGetShaderInfoLog(shader, maxLength));
        }
    }

	/*public static void clearGLList()
	{
		LOCKER.lock();
		//if(INSTANCE.deleteGLLists.size() > 16)
		{
			for(GLObject dl : INSTANCE.deleteGLLists)
			{
				if(GL11.glIsList(dl.value))//RuntimeException防止
		    	{
		    		GL11.glDeleteLists(dl.value, 1);
		    	}
			}
			NGTLog.debug("Clear " + INSTANCE.deleteGLLists.size() + " GL Lists");
			INSTANCE.deleteGLLists.clear();
		}
		LOCKER.unlock();
	}*/

	/**ディスプレイリストの再生成*/
	public static void initGLList()
	{
		//clearGLList();

		LOCKER.lock();
		if(!INSTANCE.activeGLLists.isEmpty())
		{
			for(GLObject dl : INSTANCE.activeGLLists)
			{
				if(GL11.glIsList(dl.value))
		    	{
					//GL11.glDeleteLists(dl.value, 1);
					//dl.value = 0;
					dl.setDelFlag(true);
		    	}
			}
			INSTANCE.activeGLLists.clear();

		}
		LOCKER.unlock();
	}

	public static void deleteGLList(GLObject par1)
	{
		LOCKER.lock();
		if(par1 != null)
		{
			INSTANCE.activeGLLists.remove(par1);
			INSTANCE.deleteGLLists.add(par1);
			par1.setDelFlag(true);
		}
		LOCKER.unlock();
	}

	public static DisplayList generateGLList(@Nullable GLObject par1)
	{
		LOCKER.lock();
		int value = 0;
		if(par1 != null && par1.value > 0 && GL11.glIsList(par1.value))
		{
			value = par1.value;//使い回しのため
		}
		else
		{
			value = GL11.glGenLists(1);
		}
		DisplayList list = new DisplayList(value);
		INSTANCE.activeGLLists.add(list);
		LOCKER.unlock();
		return list;
	}

	@Deprecated
	public static GLObject generateVAO(int size)
	{
		LOCKER.lock();
		GLObject list = new GLObject(GL30.glGenVertexArrays());
		INSTANCE.activeGLLists.add(list);
		LOCKER.unlock();
		return list;
	}

	static int nextVBOId = 1;

	public static VBO generateVBO(int size)
	{
		LOCKER.lock();
		VBO obj = new VBO(++nextVBOId, size);
		INSTANCE.activeGLLists.add(obj);
		LOCKER.unlock();
		return obj;
	}

	public static VertexArray2 generateVA()
	{
		LOCKER.lock();
		VertexArray2 obj = new VertexArray2(++nextVBOId);
		INSTANCE.activeGLLists.add(obj);
		LOCKER.unlock();
		return obj;
	}

	public static boolean isValid(GLObject par1)
	{
		if(par1 != null)
		{
			if(par1.delFlag)
			{
				/*if(GL11.glIsList(par1.value))
		    	{
					GL11.glDeleteLists(par1.value, 1);//タイミングによって描画がバグる
		    	}*/
				return false;
			}
			else if(par1.value > 0)
			{
				return true;
			}
		}
		return false;
	}

	public static void startCompile(GLObject par1)
	{
		LOCKER.lock();
		GL11.glNewList(par1.value, GL11.GL_COMPILE);
	}

	public static void endCompile()
	{
		GL11.glEndList();
		LOCKER.unlock();
	}

	public static void callList(GLObject par1)
	{
		GL11.glCallList(par1.value);
	}

	public static void setColor(int rgb, int alpha)
	{
		float r = (float)(rgb >> 16) / 255.0F;
		float g = (float)((rgb >> 8) & 0xFF) / 255.0F;
		float b = (float)(rgb & 0xFF) / 255.0F;
		float a = (float)alpha / 255.0F;
		GL11.glColor4f(r, g, b, a);
	}

	public static void setBrightness(int par1)
	{
		int x = par1 & 0xFFFF;
		int y = par1 >> 16;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)x, (float)y);
	}

	public static void setLightmapMaxBrightness()
	{
		//i%0x10000,i/0x10000
		//240より大きいとGeForce系で暗くなる?
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
	}

	public static void enableLighting()
	{
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public static void disableLighting()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	public static int getBlockTextureWidth()
	{
		IBlockState state = Blocks.STONE.getDefaultState();
        TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        int maxSize = NGTMath.floor(1.0F / (icon.getMaxU() - icon.getMinU())) * icon.getIconWidth();
		//int maxSize = Minecraft.getGLMaximumTextureSize();
        return maxSize;
	}

	public static int getBlockTextureHeight()
	{
		IBlockState state = Blocks.STONE.getDefaultState();
        TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        int maxSize = NGTMath.floor(1.0F / (icon.getMaxV() - icon.getMinV())) * icon.getIconHeight();
        return maxSize;
	}

	public static IntBuffer getBlockTexture(int width, int height)
	{
		int pixelCount = width * height;
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(pixelCount << 2).order(ByteOrder.nativeOrder());//DirectBuf:JNI経由に使用
		IntBuffer buffer = byteBuf.asIntBuffer();
		NGTUtilClient.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);//TexUtil.uploadTextureSub()
		return buffer;
	}

	/**
	 * @return 失敗したら-1
	 */
	public static int getShaderProgram(String vsh, String fsh)
	{
		int vertShader = createShader(vsh, ARBVertexShader.GL_VERTEX_SHADER_ARB);
        int fragShader = createShader(fsh, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		if(vertShader == 0 || fragShader == 0){return -1;}

        int program = ARBShaderObjects.glCreateProgramObjectARB();
        if(program == 0){return -1;}

         ARBShaderObjects.glAttachObjectARB(program, vertShader);
         ARBShaderObjects.glAttachObjectARB(program, fragShader);

         ARBShaderObjects.glLinkProgramARB(program);
         if(ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE)
         {
        	 NGTLog.debug(getShaderErrorLog(program));
             return -1;
         }

         ARBShaderObjects.glValidateProgramARB(program);
         if(ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE)
         {
        	 NGTLog.debug(getShaderErrorLog(program));
             return -1;
         }

         return program;
	}

	private static int createShader(String shaderObj, int shaderType)
	{
        int shader = 0;
        try
        {
        	shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if(shader == 0){return 0;}

            byte[] bytes = shaderObj.getBytes();
            ByteBuffer buffer = GLAllocation.createDirectByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            ARBShaderObjects.glShaderSourceARB(shader, buffer);
            ARBShaderObjects.glCompileShaderARB(shader);

            if(ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
            {
            	throw new RuntimeException(getShaderErrorLog(shader));
            }

            return shader;
        }
        catch(Exception e)
        {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

	private static String getShaderErrorLog(int shader)
	{
		return ARBShaderObjects.glGetInfoLogARB(shader,
    			ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

    private static final IntBuffer VIEWPORT_BUF = GLAllocation.createDirectIntBuffer(16);//64byte必須
    /**[nameLen, zMin, zMax, name...] x Hits*/
    private static final IntBuffer SELECT_BUF = GLAllocation.createDirectIntBuffer(1024);
    private static double DEPTH_RANGE;

	/**
	 * マウスピッキングを開始する<br>
	 * (1)各オブジェクト描画前にglLoadNameでID付け<br>
	 * (2)finishMousePickingでヒット数取得<br>
	 * (3)getPickedObjIdでヒットしたオブジェクトのIDを取得<br>
	 * @param range 選択範囲のpixelサイズ
	 * */
	public static void startMousePicking(float range)
	{
		float mouseX = Display.getWidth() / 2.0F;//マウス位置=画面中央
		float mouseY = Display.getHeight() / 2.0F;
		//float fov = NGTUtilClient.getMinecraft().gameSettings.fovSetting;
		//float aspect = (float)NGTUtilClient.getMinecraft().displayWidth / (float)NGTUtilClient.getMinecraft().displayHeight;

		VIEWPORT_BUF.clear();
		SELECT_BUF.clear();

		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT_BUF);//[0, 0, DispW, DispH]
		GL11.glSelectBuffer(SELECT_BUF);//[namesize, minZ, maxZ, no...]*n
		GL11.glRenderMode(GL11.GL_SELECT);
		GL11.glInitNames();
		GL11.glPushName(0);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		//GL11.glLoadIdentity();//不要
		Project.gluPickMatrix(mouseX, VIEWPORT_BUF.get(3) - mouseY, range, range, VIEWPORT_BUF);
		//範囲:0.05F~EntityRenderer.farPlaneDistance * MathHelper.SQRT_2
		//farPlaneDistance = mc.gameSettings.renderDistanceChunks * 16
		//Project.gluPerspective(fov, aspect, 0.05F, 128.0F);//不要
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		DEPTH_RANGE = ((double)NGTUtilClient.getMinecraft().gameSettings.renderDistanceChunks * 16.0D * MathHelper.SQRT_2) - 0.05D;
	}

	public static int finishMousePicking()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		int hits = GL11.glRenderMode(GL11.GL_RENDER);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		return hits;
	}

	public static int getPickedObjId(int count)
	{
		return SELECT_BUF.get(count * 4 + 3);
	}

	/**深度値のMin側(0.0~1.0)*/
	public static double getPickedObjDepth(int count)
	{
		//深度値はuintなので0.0~1.0に変換
		double depthRaw = (double)Integer.toUnsignedLong(SELECT_BUF.get(count * 4 + 1));
		return (depthRaw / (double)0xFFFFFFFFL);
	}

	public static void preMoveTexUV(float u, float v)
	{
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		GL11.glTranslatef(u, v, 0.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	public static void postMoveTexUV()
	{
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
}