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

package jp.ngt.rtm.rail;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.render.RailPartsRendererBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTurntable extends RenderLargeRail
{
	public RenderTurntable(){}

	@Override
	public void render(TileEntityLargeRailCore tileEntity, double d0, double d1,double d2, float f, int i, float alpha)
	{
		this.renderTurntable((TileEntityTurnTableCore)tileEntity, d0, d1, d2, f);
	}

	private void renderTurntable(TileEntityTurnTableCore rail, double par2, double par4, double par6, float par8)
	{
		if(!rail.isLoaded()){return;}

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPushAttrib(8256);

		ModelSetRail modelSet = rail.getResourceState().getResourceSet();
		if(modelSet != null && !modelSet.isDummy())
		{
			GLHelper.disableLighting();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderLargeRail.BLOCK_RENDERER.renderRailBlocks(rail, par2, par4, par6, par8);
			GLHelper.enableLighting();
			NGTUtilClient.getMinecraft().entityRenderer.enableLightmap();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)(par2 + 0.5D), (float)par4, (float)(par6 + 0.5D));
			float rot = rail.getPrevRotation() + (MathHelper.wrapDegrees(rail.getRotation() - rail.getPrevRotation())) * par8;
			GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-((float)(par2 + 0.5D)), -((float)par4), -((float)(par6 + 0.5D)));

			try
			{
				RailPartsRendererBase renderer = (RailPartsRendererBase)modelSet.modelObj.renderer;
				renderer.renderRail(rail, 0, par2, par4, par6, par8);

				for(int i = 0; i < rail.subRails.size(); ++i)
				{
					ResourceStateRail state = rail.subRails.get(i);
					RailPartsRendererBase subRenderer = (RailPartsRendererBase)state.getResourceSet().modelObj.renderer;
					subRenderer.renderRail(rail, i + 1, par2, par4, par6, par8);
				}
			}
			catch(ClassCastException e)
			{
			}
		}

		GL11.glPopAttrib();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}