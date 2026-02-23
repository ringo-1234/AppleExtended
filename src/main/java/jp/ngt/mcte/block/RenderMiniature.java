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

package jp.ngt.mcte.block;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.mcte.world.MCTEWorld;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.NGTObjectRenderer;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMiniature extends TileEntitySpecialRenderer<TileEntityMiniature>
{
	private static final int TOTAL_PASS = 5;//5
	private static final int[] BLOCK_TEX_ALPHA = new int[TOTAL_PASS];

	public static RenderMiniature INSTANCE = new RenderMiniature();

	private RenderMiniature(){}

	private void renderMiniatureAt(TileEntityMiniature tile, double par2, double par4,double par6, float par8)
	{
		if(tile.blocksObject == null){return;}

		GL11.glPushMatrix();
		GL11.glPushAttrib(8256);//GlStateManager参照
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);

		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.5F, (float)par6 + 0.5F);
		switch(tile.attachSide)
		{
		case 0: GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);break;
		case 1: break;
		case 2: GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);break;
		case 3: GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);break;
		case 4: GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);break;
		case 5: GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);break;
		}
		GL11.glTranslatef(0.0F, -0.5F, 0.0F);

		for(int i = 0; i < TOTAL_PASS; ++i)
		{
			this.renderWithPass(tile, par8, i);//透過したミニチュアを重ねてブラーを再現したかった･･･
		}
		tile.getMBState().setPrevMotion(tile.getMBState().getMotion());

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderWithPass(TileEntityMiniature tile, float par8, int mPass)
	{
		if(tile.getMBState().getMotionType() == 0 && mPass > 0){return;}

		GL11.glPushMatrix();

		setupMotionAndScale(tile.getMBState(), tile.getRotation(), tile.scale, tile.offsetX, tile.offsetY, tile.offsetZ, mPass);

		if(mPass > 0)
		{
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0625F);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		float x = (float)tile.blocksObject.xSize * 0.5F;
		float z = (float)tile.blocksObject.zSize * 0.5F;
		GL11.glTranslatef(-x, 0.0F, -z);

		MCTEWorld world = tile.getDummyWorld();
		if(world != null)//TE未同期時はGLList生成させないため
		{
			int pass = MinecraftForgeClient.getRenderPass();
			if(pass >= 0)
			{
				this.renderEntityAndBlock(tile, world, par8, pass, mPass);
			}
			else
			{
				//Item描画時
				this.renderEntityAndBlock(tile, world, par8, 0, 0);
				this.renderEntityAndBlock(tile, world, par8, 1, 0);
			}
		}

		if(mPass > 0)
		{
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GL11.glPopMatrix();
	}

	private void renderEntityAndBlock(TileEntityMiniature tile, MCTEWorld world, float partialTick, int pass, int mPass)
	{
		//ブロック描画後だと明るさが変になる
		NGTObjectRenderer.INSTANCE.renderTileEntities(world, partialTick, pass);
		NGTObjectRenderer.INSTANCE.renderEntities(world, partialTick, pass);
		this.renderBlocks(world, tile, partialTick, pass, mPass);
	}

	public static void setupMotionAndScale(MiniatureBlockState state, float rotation, float scale, float offsetX, float offsetY, float offsetZ, int pass)
	{
		byte type = state.getMotionType();
		float ro = 0.0F;
		float tr = 0.0F;
		//float prevMotion = state.getPrevMotion();
		//float dif = (state.getMotion() - prevMotion) * ((float)(TOTAL_PASS - pass) / (float)TOTAL_PASS);
		//float motion = prevMotion + dif;
		float speed = state.mm.rotationSpeed;
		float dif = ((float)pass / (float)TOTAL_PASS) * speed * speed / 1440.0F;
		float motion = state.getMotion() - dif;
		if(type == 1)
		{
			ro = motion;
		}
		else if(type == 2)
		{
			tr = (NGTMath.sin(motion) + 1.0F) * 0.5F * state.getTranslationScale() + state.getTranslationOffset();
		}
		else if(type == 3)
		{
			ro = NGTMath.sin(motion) * 0.5F * state.getTranslationScale() + state.getTranslationOffset();
		}
		GL11.glRotatef(rotation + ro, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(offsetX, offsetY + tr, offsetZ);
		GL11.glScalef(scale, scale, scale);
	}

	private void renderBlocks(MCTEWorld world, TileEntityMiniature tile, float par3, int pass, int mPass)
	{
		if(tile.glLists == null)
		{
			tile.glLists = new GLObject[2];
		}

		GLHelper.disableLighting();
		int i = tile.getWorld().getCombinedLight(tile.getPos(), 0);
		GLHelper.setBrightness(i);

		if(mPass == 0)
		{
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		}
		else
		{
			if(BLOCK_TEX_ALPHA[mPass] <= 0)
			{
				BLOCK_TEX_ALPHA[mPass] = this.getAlphaTex(mPass);
			}
			GlStateManager.bindTexture(BLOCK_TEX_ALPHA[mPass]);
		}

		boolean smoothing = NGTUtilClient.getMinecraft().gameSettings.ambientOcclusion != 0;
		if(smoothing){GL11.glShadeModel(GL11.GL_SMOOTH);}

		if(!GLHelper.isValid(tile.glLists[pass]))
		{
			tile.glLists[pass] = GLHelper.generateGLList(tile.glLists[pass]);
			GLHelper.startCompile(tile.glLists[pass]);
			NGTObjectRenderer.INSTANCE.renderNGTObject(world, tile.blocksObject, false, tile.mode.id, pass);
			GLHelper.endCompile();
		}
		else if(world.updated)
		{
			GLHelper.startCompile(tile.glLists[pass]);
			NGTObjectRenderer.INSTANCE.renderNGTObject(world, tile.blocksObject, false, tile.mode.id, pass);
			GLHelper.endCompile();
			world.updated = false;
		}
		else
		{
			GLHelper.callList(tile.glLists[pass]);
		}

		if(smoothing){GL11.glShadeModel(GL11.GL_FLAT);}
		GLHelper.enableLighting();
		NGTUtilClient.getMinecraft().entityRenderer.enableLightmap();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);//明るさ戻す
	}

	private int getAlphaTex(int mPass)
	{
		int w = GLHelper.getBlockTextureWidth();
		int h = GLHelper.getBlockTextureHeight();
        IntBuffer buffer = GLHelper.getBlockTexture(w, h);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < buffer.capacity(); ++i)
		{
			float alpha = (float)(TOTAL_PASS - mPass) / (float)TOTAL_PASS;
			int color = buffer.get(i);
			int a = NGTMath.floor(alpha * 255.0F);
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = (color) & 0xFF;
			color = (a << 24) | (r << 16) | (g << 8) | b;
			image.getRaster().getDataBuffer().setElem(i, color);
		}
		int texId = TextureUtil.glGenTextures();
		TextureUtil.uploadTextureImage(texId, image);
		return texId;
	}

	//debug用
	@Deprecated
	public void clear()
	{
		for(int i = 0; i < BLOCK_TEX_ALPHA.length; ++i)
		{
			BLOCK_TEX_ALPHA[i] = 0;
		}
	}

	@Override
	public void render(TileEntityMiniature tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
		this.renderMiniatureAt(tile, x, y, z, partialTicks);
    }

	@Override
	public boolean isGlobalRenderer(TileEntityMiniature tile)
    {
        return true;
    }
}