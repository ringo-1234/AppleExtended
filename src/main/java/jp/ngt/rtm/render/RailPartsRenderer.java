package jp.ngt.rtm.render;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.renderer.IRenderer;
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.model.Face;
import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.ngtlib.renderer.model.TextureCoordinate;
import jp.ngt.ngtlib.renderer.model.Vertex;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailPartsRenderer extends RailPartsRendererBase
{
	public RailPartsRenderer(String... par1)
	{
		super(par1);
	}

	//VA版
	/*@Override
	public void renderStaticParts(TileEntityLargeRailCore tileEntity, double par2, double par4, double par6)
	{
		boolean hasGLList = GLHelper.isValid(tileEntity.glList);
		if(!hasGLList)
		{
			tileEntity.glList = GLHelper.generateVA();
		}
		else if(tileEntity.shouldRerenderRail)//再描画
		{
			hasGLList = false;
		}

		if(!hasGLList)//ディスプレイリスト生成
		{
			float[][] fa = this.createRailPos(tileEntity);
			if(fa != null)
			{
				BlockPos pos = tileEntity.getPos();
				int[] brightness = this.getRailBrightness(tileEntity.getWorld(), pos.getX(), pos.getY(), pos.getZ(), fa);
				FloatBuffer fb = this.createMatrix(fa);
				this.genFBuffer((IRenderer)tileEntity.glList, tileEntity, fb, brightness, this.modelSet.modelObj.model.getGroupObjects());

				tileEntity.shouldRerenderRail = false;
				hasGLList = true;
			}
			else
			{
				tileEntity.shouldRerenderRail = true;
				hasGLList = false;
			}
		}

		if(hasGLList)//ディスプレイリスト描画
		{
			RailPosition rp = tileEntity.getRailPositions()[0];
			double x = rp.posX - (double)rp.blockX;
			double y = rp.posY - (double)rp.blockY - 0.0625D;
			double z = rp.posZ - (double)rp.blockZ;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(par2 + x), (float)(par4 + y), (float)(par6 + z));
			this.bindTexture(this.getModelObject().textures[0].material.texture);//ディスプレイリストに入れると生成重い
			((VertexArray2)tileEntity.glList).render();
			if(NGTUtilClient.getMinecraft().gameSettings.showDebugInfo)
			{
				this.renderRailPosArray(tileEntity.getRailPositions(), tileEntity.getAllRailMaps());
			}
			GL11.glPopMatrix();
		}
	}*/

	private void genFBuffer(IRenderer renderer, TileEntityLargeRailCore tileEntity, FloatBuffer matrix, int[] brightness, List<GroupObject> gObjList)
	{
		renderer.startDrawing(GL11.GL_TRIANGLES);
		int capacity = matrix.capacity() >> 4;
		int vtxCount = 0;
		for(int sectionIndex = 0; sectionIndex < capacity; ++sectionIndex)
		{
			renderer.setBrightness(brightness[sectionIndex]);
			for(int j = 0; j < gObjList.size(); ++j)
	        {
				GroupObject group = gObjList.get(j);
				if(group.name.startsWith("side") && !(sectionIndex == 0 || sectionIndex == capacity - 1)){continue;}//レールの端以外は断面を描画しない, +1~2fps

				if(!this.shouldRenderObject(tileEntity, group.name, capacity, sectionIndex)){continue;}//描画するかスクリプト側で判断

				for(int k = 0; k < group.faces.size(); ++k)
	            {
	            	Face face = group.faces.get(k);
	            	renderer.setNormal(face.faceNormal.getX(), face.faceNormal.getY(), face.faceNormal.getZ());

	            	for(int i = 0; i < face.vertices.length; ++i)
	                {
	            		this.addFace(i, face, renderer, matrix, sectionIndex);
	            		++vtxCount;
	                }
	            }
	        }
		}
		renderer.draw();
	}

	private void addFace(int index, Face face, IRenderer renderer, FloatBuffer matrix, int sectionIndex)
	{
		Vertex vtx = face.vertices[index];
		TextureCoordinate tex = face.textureCoordinates[index];
		NGTRenderHelper.addVertexWithMatrix(vtx.getX(), vtx.getY(), vtx.getZ(), tex.getU(), tex.getV(), renderer, matrix, sectionIndex);
	}
}