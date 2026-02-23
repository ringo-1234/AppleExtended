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

import jp.ngt.ngtlib.renderer.model.ModelLoader;
import jp.ngt.ngtlib.renderer.model.PolygonModel;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import jp.ngt.rtm.RTMCore;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderConverter extends TileEntitySpecialRenderer<TileEntityConverterCore>
{
	private final PolygonModel model;
	private static final ResourceLocation[] textures = {new ResourceLocation("rtm", "textures/tileentity/converter_empty.png"),
		new ResourceLocation("rtm", "textures/tileentity/converter.png"),
		new ResourceLocation("rtm", "textures/tileentity/converter_burning.png"),
		new ResourceLocation("rtm", "textures/tileentity/converter_finish.png")};

	public RenderConverter()
	{
		this.model = ModelLoader.loadModel(new ResourceLocation("rtm", "models/converter.obj"), VecAccuracy.MEDIUM);
	}

	private void renderConverterAt(TileEntityConverterCore tileEntity, double par2, double par4,double par6, float par8)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4, (float)par6 + 0.5F);
		this.bindTexture(textures[tileEntity.getMode()]);
		GL11.glRotatef((float)-tileEntity.getDirection() * 90.0F, 0.0F, 1.0F, 0.0F);
		this.model.renderPart(RTMCore.smoothing, "dai");
		GL11.glRotatef(tileEntity.getPitch(), 1.0F, 0.0F, 0.0F);
		this.model.renderPart(RTMCore.smoothing, "jiku");
		this.model.renderPart(RTMCore.smoothing, "body1");
		this.model.renderPart(RTMCore.smoothing, "body2");
		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntityConverterCore par1, double par2, double par4, double par6, float par8, int par9)
    {
        this.renderConverterAt(par1, par2, par4, par6, par8);
    }
}