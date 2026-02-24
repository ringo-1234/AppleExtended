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

package jp.ngt.rtm.render;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.model.Face;
import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import jp.ngt.ngtlib.renderer.model.Vertex;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ActionParts extends Parts
{
	private static final float OUTLINE_THICKNESS = 0.005F;

	public final ActionType behavior;
	private GroupObject[] outlineModels;
	public int id;

	public ActionParts(ActionType par1, String... par2)
	{
		super(par2);
		this.behavior = par1;
	}

	@Override
	public void init(PartsRenderer renderer)
	{
		super.init(renderer);
		this.setupOutlineModel(renderer);
	}

	private void setupOutlineModel(PartsRenderer renderer)
	{
		GroupObject[] objs = this.getObjects(renderer.modelObj.model);
		this.outlineModels = new GroupObject[objs.length];
		for(int i = 0; i < objs.length; ++i)
		{
			GroupObject go = objs[i].copy("outline_" + i);
			go.smoothingAngle = 180.0F;
			go.calcVertexNormals(VecAccuracy.MEDIUM);
			for(int j = 0; j < go.faces.size(); ++j)
			{
				Face face = go.faces.get(j);
				for(int k = 0; k < face.vertices.length; ++k)
				{
					Vertex vNormal = face.vertexNormals[k].copy(VecAccuracy.MEDIUM);
					face.vertices[k] = face.vertices[k].add(vNormal.expand(OUTLINE_THICKNESS));
				}
				face.calculateFaceNormal(VecAccuracy.MEDIUM);
			}
			this.outlineModels[i] = go;
		}
	}

	@Override
	public void render(PartsRenderer renderer)
	{
		if(renderer.currentPass == RenderPass.OUTLINE.id)
		{
			boolean hit = this.equals(renderer.hittedParts.get(renderer.hittedEntity));
			if(hit)
			{
				int color = Mouse.isButtonDown(1) ? 0xFF8000 : 0xFFFFFF;
				this.renderOutline(renderer, color);
			}
		}
		else if(renderer.currentPass == RenderPass.PICK.id)
		{
			GL11.glLoadName(this.id);
			boolean smoothing = renderer.modelSet.getConfig().smoothing;
			IModelNGT model = renderer.modelObj.model;
			model.renderOnly(smoothing, this.objNames);//calllistするとEXCEPTION_ACCESS_VIOLATION?
		}
		else
		{
			super.render(renderer);
		}
	}

	private void renderOutline(PartsRenderer par1, int color)
    {
		GL11.glPushMatrix();
    	GL11.glCullFace(GL11.GL_FRONT);
    	GLHelper.disableLighting();
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GLHelper.setColor(color, 0xFF);
    	for(GroupObject obj : this.outlineModels)
    	{
    		obj.render(false);
    	}
    	GLHelper.setColor(0xFFFFFF, 0xFF);
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GLHelper.enableLighting();
    	GL11.glCullFace(GL11.GL_BACK);
    	GL11.glPopMatrix();

    	//輪郭描画(MobEffects.GLOWING)
    	//NGTUtilClient.getMinecraft().renderGlobal.entityOutlineShader.render(partialTicks);
    }

	@Override
	public boolean ignoreMatId(PartsRenderer renderer)
	{
		return renderer.currentPass == RenderPass.PICK.id;//ピッキング時はマテリアルにかかわらずポリゴンを描画
	}
}