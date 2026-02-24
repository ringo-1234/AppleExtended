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

package jp.ngt.rtm.block.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.modelpack.modelset.TextureSetFlag;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFlag extends TileEntitySpecialRenderer<TileEntityFlag>
{
	private static ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/ornament/iron.png");

	private void renderFlag(TileEntityFlag tileEntity, double par2, double par4,double par6, float par8)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.0F, (float)par6 + 0.5F);

		TextureSetFlag set = tileEntity.getResourceState().getResourceSet();

		this.renderPole(set);

		if(BlockUtil.getBlock(this.getWorld(), tileEntity.getPos().up()) != RTMBlock.flag)
		{
			this.renderFlag(tileEntity, set);
		}

		GL11.glPopMatrix();
	}

	private void renderFlag(TileEntityFlag tileEntity, TextureSetFlag set)
	{
		GL11.glTranslatef(0.0F, set.getConfig().poleLength, 0.0F);
		float yaw = tileEntity.getRotation();
		GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);

		float wind = 1.0F;//NGTMath.getSin(NGTMath.toRadians((float)tileEntity.tick * 0.64F)) * 0.5F + 0.5F;
		float windInv = 1.0F - wind;
		float h = set.getConfig().height;
		float w = set.getConfig().width;

		GL11.glShadeModel(GL11.GL_SMOOTH);
		this.bindTexture(set.texture);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		int resV = set.getConfig().resolutionV;
		int resU = set.getConfig().resolutionU;
		for(int i = 0; i < resV; ++i)
		{
			float v0 = (float)i / (float)resV;
			float v1 = (float)(i + 1) / (float)resV;

			for(int j = 0; j < resU; ++j)
			{
				float u0 = (float)j / (float)resU;
				float u1 = (float)(j + 1) / (float)resU;
				float u0w = u0 * w;
				float u1w = u1 * w;

				float r0 = this.getR(tileEntity.wave, u1, v0);
				float d0 = this.getWave(r0, u1);
				float nr0 = this.getNormalR(r0 + yaw);
				tessellator.setNormal(NGTMath.getSin(nr0), 0.0F, NGTMath.getCos(nr0));
				tessellator.addVertexWithUV(d0, -(v0 + windInv * u1w) * h, u1w * wind, u1, v0);

				float r1 = this.getR(tileEntity.wave, u1, v1);
				float d1 = this.getWave(r1, u1);
				float nr1 = this.getNormalR(r1 + yaw);
				tessellator.setNormal(NGTMath.getSin(nr1), 0.0F, NGTMath.getCos(nr1));
				tessellator.addVertexWithUV(d1, -(v1 + windInv * u1w) * h, u1w * wind, u1, v1);

				float r2 = this.getR(tileEntity.wave, u0, v1);
				float d2 = this.getWave(r2, u0);
				float nr2 = this.getNormalR(r2 + yaw);
				tessellator.setNormal(NGTMath.getSin(nr2), 0.0F, NGTMath.getCos(nr2));
				tessellator.addVertexWithUV(d2, -(v1 + windInv * u0w) * h, u0w * wind, u0, v1);

				float r3 = this.getR(tileEntity.wave, u0, v0);
				float d3 = this.getWave(r3, u0);
				float nr3 = this.getNormalR(r3 + yaw);
				tessellator.setNormal(NGTMath.getSin(nr3), 0.0F, NGTMath.getCos(nr3));
				tessellator.addVertexWithUV(d3, -(v0 + windInv * u0w) * h, u0w * wind, u0, v0);
			}
		}
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	private void renderPole(TextureSetFlag set)
	{
		NGTTessellator tessellator = NGTTessellator.instance;
		this.bindTexture(TEXTURE);
		tessellator.startDrawingQuads();
		NGTRenderer.renderPole(tessellator, 0.0625F, set.getConfig().poleLength, true);
		tessellator.draw();
	}

	//正弦波のX
	private float getR(float r, float u, float v)
	{
		//return NGTMath.toRadians(r + (540.0F * u * u) + (90.0F * v));
		//return NGTMath.toRadians(r + 30.0F * (60.0F * u / (u + 1.0F)) + 90.0F * v);
		return -NGTMath.toRadians(r + (360.0F / (3.0F * u + 1.0F)) * (v + 1));
	}

	//正弦波のY
	private float getWave(float r, float u)
	{
		return NGTMath.getSin(r) * u * 0.15F;
	}

	//法線の角度
	private float getNormalR(float r)
	{
		return NGTMath.toRadians(45.0F * NGTMath.getCos(r) + 90.0F);
	}

	@Override
	public void render(TileEntityFlag par1, double par2, double par4, double par6, float par8, int par9, float alpha)
    {
        this.renderFlag(par1, par2, par4, par6, par8);
    }
}