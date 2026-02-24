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

import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.RTMBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderStation extends TileEntitySpecialRenderer<TileEntityStation>
{
	private static final ResourceLocation texture = new ResourceLocation("rtm", "textures/mark.png");
	private Item stationBlock;

	private int count;

	public void renderStation(TileEntityStation tileEntity, double par2, double par4,double par6, float par8)
	{
		if(this.stationBlock == null)
		{
			this.stationBlock = Item.getItemFromBlock(RTMBlock.stationCore);
		}

		//ブロックを手に持ってる時以外は不可視
		ItemStack stack = NGTUtilClient.getMinecraft().player.getHeldItemMainhand();
		if(stack == null || stack.getItem() != this.stationBlock){return;}

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		GLHelper.disableLighting();
		GLHelper.setLightmapMaxBrightness();
		GL11.glEnable(GL11.GL_CULL_FACE);

		if(this.count >= 300)
		{
			tileEntity.checkHeight();
			this.count = 0;
		}
		++this.count;

		final float moveY = 16.0F;
		GL11.glTranslatef(0.5F, moveY + tileEntity.maxHeight, 0.5F);
		GL11.glRotatef(-NGTUtilClient.getMinecraft().getRenderManager().playerViewY + 180.0F, 0.0F, 1.0F, 0.0F);

		this.bindTexture(texture);
		final float size = 8.0F;
		final float depth = 0.0F;
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(size, 0.0F, depth, 1.0F, 1.0F);
		tessellator.addVertexWithUV(size, 16.0F, depth, 1.0F, 0.0F);
		tessellator.addVertexWithUV(-size, 16.0F, depth, 0.0F, 0.0F);
		tessellator.addVertexWithUV(-size, 0.0F, depth, 0.0F, 1.0F);
		tessellator.draw();

		GL11.glTranslatef(0.0F, 11.0F, 0.0625F);
		FontRenderer fontRenderer = this.getFontRenderer();
		String s = tileEntity.getName() + "駅";
		int w = fontRenderer.getStringWidth(s);
		float f = 4.0F / (float)w;
		GL11.glScalef(f, -f, -f);
		fontRenderer.drawString(s, -w >> 1, -4, 0x00FF00);

		GLHelper.enableLighting();
		GL11.glPopMatrix();
	}

	@Override
	public void render(TileEntityStation tileEntity, double par2, double par4, double par6, float par8, int par9, float alpha)
    {
        this.renderStation(tileEntity, par2, par4, par6, par8);
    }
}