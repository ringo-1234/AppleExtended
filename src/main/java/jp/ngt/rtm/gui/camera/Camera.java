package jp.ngt.rtm.gui.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Camera
{
	public static final Camera INSTANCE = new Camera();

	private static final int DELAY = 10;
	private static final int GAUS_MAX = 24;
	private static final int DEPTH_SPLIT = 8;
	/**ぼかし強度*/
	private static final float GAUS_COEF = 20.0F;
	/**目の前のブロックの深度が約0.8*/
	private static final float MIN_DEPTH = 0.75F;
	private final float[] gaussianWeights = new float[GAUS_MAX];
	private final FloatBuffer gaussianWeightsBuf = GLAllocation.createDirectFloatBuffer(GAUS_MAX);

	/**ガウスぼかし用FBO(横, 縦+合計, 深度)*/
	private final int[] gaussianFBO = {-1, -1, -1};
	/**ガウスぼかし用テクスチャ(横, 縦+合計, 深度)*/
	private final int[] gaussianTex = {-1, -1, -1};
	/**MC画面のコピー(モーションブラー用, DOF用)*/
	private final int[] mcScreenTex = {-1, -1};
	@Deprecated
	private int mcDepthTex = -1;
	private int dofShader = -1;
	private FloatBuffer depthBuffer;
	private IntBuffer depthColorBuffer;

	private boolean isActive;
	private boolean mcTexRendered;

	private float scale = 1.0F;
	private float sensitivity = 1.0F;
	private float focus = 0.5F;
	/**0:none, 1:auto, 2:manual*/
	private int focusMode;

	private float fovModification = 1.0F;
	private float frameBufU = 1.0F;
	private float frameBufV = 1.0F;
	private int tickCount;

	private Camera()
	{
		for(int i = 0; i < this.gaussianFBO.length; ++i)
		{
			this.gaussianFBO[i] = GL30.glGenFramebuffers();
			this.gaussianTex[i] = GL11.glGenTextures();
		}

		for(int i = 0; i < 2; ++i)
		{
			this.mcScreenTex[i] = GL11.glGenTextures();
		}
		this.mcDepthTex = GL11.glGenTextures();
	}

	private void init(int w, int h)
	{
		for(int i = 0; i < this.gaussianFBO.length; ++i)
		{
			this.initTexture(this.gaussianTex[i], w, h);

			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.gaussianFBO[i]);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.gaussianTex[i], 0);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}

		for(int i = 0; i < 2; ++i)
		{
			this.initTexture(this.mcScreenTex[i], w, h);
		}

		/*GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mcDepthTex);
		//GL_DEPTH_COMPONENT24
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, w, h, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);*/

		try
		{
			String vsh = NGTText.getText(new ResourceLocation("rtm", "shaders/dof.vsh"), true);
			String fsh = NGTText.getText(new ResourceLocation("rtm", "shaders/dof.fsh"), true);
			this.dofShader = GLHelper.getShaderProgram(vsh, fsh);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if(this.depthBuffer == null || this.depthBuffer.capacity() < w * h)
		{
			this.depthBuffer = GLAllocation.createDirectFloatBuffer(w * h);
			this.depthColorBuffer = GLAllocation.createDirectIntBuffer(w * h);
		}
	}

	private void initTexture(int texture, int w, int h)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (IntBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	//runGameLoop
	// entityRenderer.updateCameraAndRender
	//  renderWorld
	//  mc.ingameGUI.renderGameOverlay
	//   ココ
	//   renderHelmet
	public void onRenderGameOverlayPre()
	{
		if(!this.isActive){return;}

		if(this.sensitivity < 1.0F)
		{
			if(this.sensitivity < 1.0F)
			{
				if(this.tickCount >= DELAY)
				{
					this.tickCount = 0;
					this.copyScreenBuf(0);
				}
				++this.tickCount;
			}
		}
	}

	public void onRenderWorldPost()
	{
		if(!this.isActive){return;}

		if(this.focusMode > 0)
		{
			this.copyDepthBuffer();
			this.copyScreenBuf(1);
		}
	}

	private void copyScreenBuf(int texId)
	{
		this.mcTexRendered = true;

		Minecraft mc = Minecraft.getMinecraft();
		int w = mc.getFramebuffer().framebufferTextureWidth;
		int h = mc.getFramebuffer().framebufferTextureHeight;

		GL43.glCopyImageSubData(mc.getFramebuffer().framebufferTexture, GL11.GL_TEXTURE_2D, 0, 0, 0, 0,
				this.mcScreenTex[texId], GL11.GL_TEXTURE_2D, 0, 0, 0, 0, w, h, 1);

		this.frameBufU = (float)mc.getFramebuffer().framebufferWidth / (float)mc.getFramebuffer().framebufferTextureWidth;
		this.frameBufV = (float)mc.getFramebuffer().framebufferHeight / (float)mc.getFramebuffer().framebufferTextureHeight;
	}

	private void copyDepthBuffer()
	{
		Minecraft mc = Minecraft.getMinecraft();
		int w = mc.getFramebuffer().framebufferTextureWidth;
		int h = mc.getFramebuffer().framebufferTextureHeight;
		this.depthBuffer.clear();
		GL11.glReadPixels(0, 0, w, h, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, this.depthBuffer);
		this.depthColorBuffer.clear();
		for(int i = 0;i < this.depthBuffer.capacity(); ++i)
		{
			float depth = this.depthBuffer.get(i);
			this.depthColorBuffer.put(this.depth2Color(depth));
		}
		this.depthColorBuffer.flip();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.gaussianTex[2]);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.depthColorBuffer);

		if(this.focusMode == 1)
		{
			int index = (h / 2) * w + (w / 2);
			int g = ColorUtil.getG(this.depthColorBuffer.get(index));
			this.focus = (float)g / 255.0F;
			//this.focus = this.fixDepth(this.depthBuffer.get(index));
		}

		/*GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, mc.getFramebuffer().depthBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mcDepthTex);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, w, h);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);*/
	}

	private int depth2Color(float depth)
	{
		int c0 = (int)(this.fixDepth(depth) * 255.0F);
		return ColorUtil.encode(c0, c0, c0);
	}

	/**デフォルトの深度値は1.0寄りなので補正*/
	private float fixDepth(float depth)
	{
		if(depth >= MIN_DEPTH)
		{
			double d0 = (depth - MIN_DEPTH) / (1.0F - MIN_DEPTH);
			//return (float)NGTMath.pow(d0, 6);
			if(d0 >= 1.0D){return 1.0F;}
			d0 *= d0;//^2
			d0 *= d0;//^4
			d0 *= d0;//^8
			return (float)(-Math.sqrt(1 - d0) + 1);//円弧のx^2をx^8にしたもの
		}
		else
		{
			return 0.0F;
		}
	}

	public void off()
	{
		if(this.isActive)
		{
			this.isActive = false;
			this.mcTexRendered = false;
			this.tickCount = 0;
		}
	}

	public void render(Minecraft mc, RenderGameOverlayEvent event, int guiW, int guiH)
	{
		int w = mc.displayWidth;
		int h = mc.displayHeight;

		if(!this.isActive)
		{
			this.isActive = true;
			this.init(w, h);
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		if(this.sensitivity < 1.0F)
		{
			this.renderDelayFrame(mc, guiW, guiH);
		}

		if(this.focusMode > 0 && this.dofShader > 0)
		{
			this.checkScreenshot(w, h, "ss_depth");
			this.renderDOF(mc, guiW, guiH);
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this.updateKeyState();

		this.renderText(mc, guiW, guiH);
	}

	private void renderDelayFrame(Minecraft mc, int guiW, int guiH)
	{
		if(!this.mcTexRendered){return;}

		//前回画面を透過して描画
		int alpha = (int)(255.0F * (1.0F - this.sensitivity));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mcScreenTex[0]);
		this.renderBuffer(guiW, guiH, alpha, true);
	}

	private void renderDOF(Minecraft mc, int guiW, int guiH)
	{
		if(!this.mcTexRendered){return;}

		ARBShaderObjects.glUseProgramObjectARB(this.dofShader);
		ARBShaderObjects.glUniform1iARB(ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "texture0"), 0);
		ARBShaderObjects.glUniform1iARB(ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "texture1"), 1);
		ARBShaderObjects.glUniform1fARB(ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "width"), guiW);
		ARBShaderObjects.glUniform1fARB(ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "height"), guiH);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.gaussianTex[2]);//深度用テクスチャ(不変なのでここでバインド)

		for(int frame = DEPTH_SPLIT; frame > 0; --frame)//奥から描画する
		{
			float blurThreshold = (float)frame / (float)DEPTH_SPLIT;//背景描画のためmax1.0
			double blurStrMax = (double)GAUS_MAX * ((double)mc.displayWidth / 1500.0D);
			if(blurStrMax > GAUS_MAX)
			{
				blurStrMax = GAUS_MAX;
			}
			float focusDif = Math.abs(blurThreshold - this.focus);
			int range = (int)Math.floor(NGTMath.sigmoid(focusDif * 2.0D, 7.0D) * blurStrMax);//深度によってぼかし範囲変える
			int maxIndex = this.calcGaussian(range);//0以上の値が入ってる配列長(fshのfor回数削減に使いたい)

			ARBShaderObjects.glUniform1fARB(
					ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "threshold"), blurThreshold);
			ARBShaderObjects.glUniform1ARB(
					ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "weight"), this.gaussianWeightsBuf);

			for(int pass = 0; pass < 2; ++pass)
			{
				ARBShaderObjects.glUniform1iARB(ARBShaderObjects.glGetUniformLocationARB(this.dofShader, "pass"), pass);
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.gaussianFBO[pass]);
				switch(pass)
				{
				case 0:
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mcScreenTex[1]);//pass1:MC→横ぼかし
					break;
				case 1:
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.gaussianTex[0]);//pass2:横→縦ぼかし+合成
					break;
				}
				this.renderBuffer(guiW, guiH, -1, false);
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			}
		}

		mc.getFramebuffer().bindFramebuffer(false);//MCのFBOに戻す(ビューポート初期化なし)

		ARBShaderObjects.glUseProgramObjectARB(0);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.gaussianTex[1]);
		this.renderBuffer(guiW, guiH, 0xFF, true);
	}

	private int calcGaussian(int range)
	{
		range = NGTMath.clamp(range, 0, GAUS_MAX);
		float[] weight = this.gaussianWeights;
		if(range <= 0)//ぼかしなし
		{
			for(int i = 0; i < weight.length; i++)
			{
				weight[i] = (i == 0) ? 1.0F : 0.0F;
			}
			return 0;
		}

		int maxIndex = 0;
		double total = 0.0D;
		double variance = GAUS_COEF * ((double)(range * range) / (double)(GAUS_MAX * GAUS_MAX));//大きいほど扁平
		for(int i = 0; i < weight.length; i++)
		{
			double r = 1.0D + 2.0D * i;
			double w = Math.exp(-0.5D * (r * r) / variance);
			if(w < (1.0D / 255.0D))//8bitの分解能未満
		    {
		    	maxIndex = i - 1;
		    	break;
		    }

		    weight[i] = (float)w;
		    if(i > 0)
		    {
		    	w *= 2.0D;//正負両側の分を積算
		    }
		    total += w;
		}

		this.gaussianWeightsBuf.clear();
		for(int i = 0; i < weight.length; i++)
		{
			if(i > maxIndex)
		    {
		    	weight[i] = 0.0F;
		    }
		    weight[i] /= total;//積分値を1.0にする
		    this.gaussianWeightsBuf.put(weight[i]);
		}
		this.gaussianWeightsBuf.flip();
		return maxIndex;
	}

	private void renderBuffer(int w, int h, int alpha, boolean flip)
	{
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);//FBOにはα値が含まれてるので、それは無視する
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        if(alpha > 0)
        {
        	tessellator.setColorRGBA_I(0xFFFFFF, alpha);
        }
        float fx = (float)w;
        float fy = (float)h;
        float fz = -90.0F;
        //MCのフレームバッファはUV反転しているのでここで補正
        float minV = flip ? this.frameBufV : 0.0F;
        float maxV = flip ? 0.0F : this.frameBufV;
        tessellator.addVertexWithUV(0.0F, fy, fz, 0.0F, maxV);
        tessellator.addVertexWithUV(fx, fy, fz, this.frameBufU, maxV);
        tessellator.addVertexWithUV(fx, 0.0F, fz, this.frameBufU, minV);
        tessellator.addVertexWithUV(0.0F, 0.0F, fz, 0.0F, minV);
        tessellator.draw();

        GL11.glDepthMask(true);
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderText(Minecraft mc, int guiW, int guiH)
	{
		int x = 5;
		int y = 5;
		int fontH = 10;
		int color = 0x00FF10;
		String focusStatus;
		switch(this.focusMode)
		{
		case 0:
			focusStatus = "NONE";
			break;
		case 1:
			focusStatus = String.format("AUTO(%.3f)", this.focus);
			break;
		case 2:
			focusStatus = String.format("%.3f", this.focus);
			break;
		default:
			focusStatus = "err";
		}
		String s = String.format("x%.1f S:%d%% F:%s", this.scale, (int)(this.sensitivity * 100.0F), focusStatus);
		mc.fontRenderer.drawStringWithShadow(s, x, y, color);
		y += fontH;
		mc.fontRenderer.drawStringWithShadow(
				String.format("Zoom:[%c][%c]", CameraKey.ZOOM_OUT.chara, CameraKey.ZOOM_IN.chara), x, y, color);
		y += fontH;
		mc.fontRenderer.drawStringWithShadow(
				String.format("Sensitivity:[%c][%c]", CameraKey.SENSIT_DOWN.chara, CameraKey.SENSIT_UP.chara), x, y, color);
		y += fontH;
		mc.fontRenderer.drawStringWithShadow(
				String.format("Focus:[%c][%c] [%c]", CameraKey.FOCUS_OUT.chara, CameraKey.FOCUS_IN.chara, CameraKey.FOCUS_MODE.chara), x, y, color);
	}

	private void updateKeyState()
	{
		this.scale = CameraKeySet.ZOOM.updateValue(this.scale);
		float defaultFov = Minecraft.getMinecraft().gameSettings.fovSetting;
		double d0 = Math.tan(NGTMath.toRadians(defaultFov * 0.5F)) / this.scale;
		float fov = (float)(NGTMath.toDegrees(Math.atan(d0) * 2.0D));//scale=tan(defFov/2)/tan(newFov/2)
		this.fovModification = fov / defaultFov;

		this.sensitivity = CameraKeySet.SENSITIVITY.updateValue(this.sensitivity);

		if(this.focusMode == 2)
		{
			this.focus = CameraKeySet.FOCUS.updateValue(this.focus);//フォーカス手動変更
		}

		if(CameraKey.FOCUS_MODE.isPressed())
		{
			this.focusMode = (this.focusMode + 1) % 3;
		}
	}

	public float getFov()
	{
		return this.fovModification;
	}

	//DEBUG////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void checkScreenshot(int w, int h, String fileName)
	{
		if(CameraKey.DEBUG.isPressed())
		{
	    	try
	    	{
	    		BufferedImage image = this.getDepthBufT(w, h);
	    		File file = new File(NGTFileLoader.getModsDir().get(0), fileName + ".png");
				ImageIO.write(image, "png", file);
				NGTLog.showChatMessage("save:%s", file.getAbsolutePath());
			}
	    	catch (IOException e)
	    	{
				e.printStackTrace();
			}
		}
	}

	private BufferedImage getMCScreen(int w, int h)
	{
		IntBuffer buf = BufferUtils.createIntBuffer(w * h);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < buf.capacity(); ++i)
		{
			int color = buf.get(i);
			image.getRaster().getDataBuffer().setElem(i, color);
		}
		return image;
	}

	/*private BufferedImage getDepthTex(int w, int h)
	{
		IntBuffer buf = BufferUtils.createIntBuffer(w * h);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mcDepthTex);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < buf.capacity(); ++i)
		{
			int color = buf.get(i);
			image.getRaster().getDataBuffer().setElem(i, color);
		}
		return image;
	}*/

	private BufferedImage getDepthBufF(int w, int h)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < this.depthBuffer.capacity(); ++i)
		{
			float depth = this.depthBuffer.get(i);
			image.getRaster().getDataBuffer().setElem(i, this.depth2Color(depth));
		}
		return image;
	}

	private BufferedImage getDepthBufI(int w, int h)
	{
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < this.depthColorBuffer.capacity(); ++i)
		{
			int color = this.depthColorBuffer.get(i);
			image.getRaster().getDataBuffer().setElem(i, color);
		}
		return image;
	}

	private BufferedImage getDepthBufT(int w, int h)
	{
		IntBuffer buf = BufferUtils.createIntBuffer(w * h);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.gaussianTex[2]);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < buf.capacity(); ++i)
		{
			int color = buf.get(i);
			image.getRaster().getDataBuffer().setElem(i, color);
		}
		return image;
	}
}
