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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.IRenderer;
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.PolygonRenderer;
import jp.ngt.ngtlib.renderer.model.Face;
import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.rail.RenderMarkerBlock;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.TileEntityLargeRailSwitchCore;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailPartsRendererBase extends TileEntityPartsRenderer<ModelSetRail>
{
	protected int currentRailIndex;

	//メモ:これ使うとかえって速度落ちる
	//((Invocable)this.getScript()).getInterface(RailScript.class);

	public RailPartsRendererBase(String... par1)
	{
		super(par1);
	}

	@Override
	public void init(ModelSetRail par1, ModelObject par2)
	{
		super.init(par1, par2);
	}

	/**RenderLargeRailから呼ばれる*/
	public void renderRail(TileEntityLargeRailCore tileEntity, int index, double par2, double par4, double par6, float par8)
	{
		this.currentRailIndex = index;
		this.renderRailStatic(tileEntity, par2, par4, par6, par8);
		this.renderRailDynamic(tileEntity, par2, par4, par6, par8);
	}

	/*スクリプト呼び出しメソッド**********************************************************************************/

	/**形状固定の部分を描画*/
	protected void renderRailStatic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8)
	{
		//Script介さず直接renderStaticParts()呼んでも速度変わらん
		ScriptUtil.doScriptIgnoreError(this.getScript(), "renderRailStatic", tileEntity, x, y, z, par8, 0);
	}

	/**形状が変化する部分を描画*/
	protected void renderRailDynamic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8)
	{
		ScriptUtil.doScriptIgnoreError(this.getScript(), "renderRailDynamic", tileEntity, x, y, z, par8, 0);
	}

	/**
	 * オブジェクトを描画するかどうか
	 * @param objName
	 * @param len セグメント総数
	 * @param pos 何番目のセグメントか
	 * */
	protected boolean shouldRenderObject(TileEntityLargeRailCore tileEntity, String objName, int len, int pos)
	{
		return (Boolean)ScriptUtil.doScriptIgnoreError(this.getScript(), "shouldRenderObject", tileEntity, objName, len, pos);
	}

	/*+***************************************************************************************************/

	/**
	 * 固定パーツをGLListで描画<br>
	 * スクリプトから呼び出し
	 */
	public void renderStaticParts(TileEntityLargeRailCore rail, double par2, double par4, double par6)
	{
		boolean hasGLList = true;

		if(rail.glLists == null)
		{
			rail.glLists = new GLObject[rail.subRails.size() + 1];
			hasGLList = false;
		}
		else if(rail.glLists.length != rail.subRails.size() + 1)
		{
			/*GLObject[] oldList = rail.glLists;
			rail.glLists = new GLObject[rail.subRails.size() + 1];
			for(int i = 0; i < oldList.length; ++i)
			{
				rail.glLists[i] = oldList[i];
			}
			hasGLList = oldList.length > this.currentRailIndex;*/
			for(int i = 0; i < rail.glLists.length; ++i)
			{
				GLHelper.deleteGLList(rail.glLists[i]);
			}
			rail.glLists = new GLObject[rail.subRails.size() + 1];
			hasGLList = false;
		}

		if(hasGLList)
		{
			hasGLList = GLHelper.isValid(rail.glLists[this.currentRailIndex]);
		}

		if(!hasGLList)
		{
			rail.glLists[this.currentRailIndex] = GLHelper.generateGLList(rail.glLists[this.currentRailIndex]);
		}
		else if(rail.shouldRerenderRail)//再描画
		{
			hasGLList = false;
		}

		if(!hasGLList)//ディスプレイリスト生成
		{
			float[][] fa = this.createRailPos(rail);
			if(fa != null)
			{
				BlockPos pos = rail.getPos();
				int[] brightness = this.getRailBrightness(rail.getWorld(), pos.getX(), pos.getY(), pos.getZ(), fa);
				FloatBuffer fb = this.createMatrix(fa);

				GLHelper.startCompile(rail.glLists[this.currentRailIndex]);//GL_COMPILE_AND_EXECUTEは画面がチラつく
				this.tessellateParts(rail, fb, brightness, this.modelSet.modelObj.model.getGroupObjects());
				GLHelper.endCompile();

				rail.shouldRerenderRail = false;
				hasGLList = true;
			}
			else
			{
				rail.shouldRerenderRail = true;
				hasGLList = false;
				//GLHelper.deleteGLList(rail.glList);
				//rail.glList = null;
			}
		}

		if(hasGLList)//ディスプレイリスト描画
		{
			RailPosition rp = rail.getRailPositions()[0];
			double x = rp.posX - (double)rp.blockX;
			double y = rp.posY - (double)rp.blockY - 0.0625D;
			double z = rp.posZ - (double)rp.blockZ;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(par2 + x), (float)(par4 + y), (float)(par6 + z));
			this.bindTexture(this.getModelObject().textures[0].material.texture);//ディスプレイリストに入れると生成重い
			GLHelper.callList(rail.glLists[this.currentRailIndex]);
			GL11.glPopMatrix();
		}
	}

	/**
	 * 各レールパーツの位置と向き
	 * @return {x, y, z, yaw, pitch, roll}
	 */
	protected float[][] createRailPos(TileEntityLargeRailCore par1)
	{
		float[] rev = RailPosition.REVISION[par1.getRailPositions()[0].direction];
		RailMap[] rms = par1.getAllRailMaps();
		if(rms != null)
		{
			List<float[]> list = new ArrayList<float[]>();
			for(RailMap rm : rms)
			{
				int max = (int)(rm.getLength() * 2.0D);
				double[] stPoint = rm.getRailPos(max, 0);
				//double startH = rm.getRailHeight(max, 0);//カント付けた時端が沈む
				double startH = rm.getStartRP().posY;
				float moveX = (float)(stPoint[1] - ((double)par1.getStartPoint()[0] + 0.5D + (double)rev[0]));
				float moveZ = (float)(stPoint[0] - ((double)par1.getStartPoint()[2] + 0.5D + (double)rev[1]));

				for(int i = 0; i <= max; ++i)
				{
					double[] curPoint = rm.getRailPos(max, i);
					float[] array = {
							moveX + (float)(curPoint[1] - stPoint[1]),
							(float)(rm.getRailHeight(max, i) - startH),//0.0F,
							moveZ + (float)(curPoint[0] - stPoint[0]),
							rm.getRailRotation(max, i),
							-rm.getRailPitch(max, i),
							rm.getCant(max, i)
					};

					list.add(array);
				}
			}
			return list.toArray(new float[list.size()][5]);
		}
		return null;
	}

	private final FloatBuffer convBuf = FloatBuffer.wrap(new float[]{
			1.0F, 0.0F, 0.0F, 0.0F,
			0.0F, 1.0F, 0.0F, 0.0F,
			0.0F, 0.0F, 1.0F, 0.0F,
			0.0F, 0.0F, 0.0F, 1.0F});

	/**レンダリング用同時変換行列を作製*/
	protected final FloatBuffer createMatrix(float[][] rp)
	{
		//rpにNaNが含まれるとダメ
		FloatBuffer buffer = FloatBuffer.allocate(rp.length << 4);
		for(int i = 0; i < rp.length; ++i)
		{
			FloatBuffer fb = this.convBuf;
			fb = NGTRenderHelper.translate(fb, rp[i][0], rp[i][1], rp[i][2]);
			fb = NGTRenderHelper.rotate(fb, NGTMath.toRadians(rp[i][3]), 'Y');
			fb = NGTRenderHelper.rotate(fb, NGTMath.toRadians(rp[i][4]), 'X');
			fb = NGTRenderHelper.rotate(fb, NGTMath.toRadians(rp[i][5]), 'Z');
			buffer.put(fb);
		}
		return buffer;
	}

	//Entity.getBrightnessForRender()はRenderManager.renderEntityStatic()で呼ばれる
	/**レール上の明るさを一括取得*/
	protected final int[] getRailBrightness(World world, int x, int y, int z, float[][] rp)
	{
		int[] fa = new int[rp.length];
		for(int i = 0; i < rp.length; ++i)
		{
			int x0 = x + NGTMath.floor(rp[i][0]);
	        int y0 = y + NGTMath.floor(rp[i][1]);
	        int z0 = z + NGTMath.floor(rp[i][2]);
	        fa[i] = this.getBrightness(world, x0, y0, z0);
		}
		//NGTLog.debug("br %d", fa.length);
		return fa;
	}

	/**指定座標の明るさ取得(バグ回避処理付き)*/
	public int getBrightness(World world, int x, int y, int z)
	{
		int brightness = this.getWorldBrightness(world, x, y, z);
        if(brightness <= 0)
        {
        	brightness = this.getWorldBrightness(world, x, y + 1, z);//一部黒くなる問題回避
        }
        return brightness;
	}

	/**指定座標の明るさ取得*/
	private final int getWorldBrightness(World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		return world.isBlockLoaded(pos) ? world.getCombinedLight(pos, 0) : 0;
	}

	/**
	 * 引数のパーツを一括描画<br>
	 * GLListコンパイル用
	 * @param matrix     セグメントごとの座標&角度
	 * @param brightness セグメントごとの明るさ
	 * @param lists      セグメントの構成ObjectのList x セグメント数
	 */
	private final void tessellateParts(TileEntityLargeRailCore tileEntity, FloatBuffer matrix, int[] brightness, List<GroupObject> gObjList)
	{
		//IRenderer tessellator = NGTTessellator.instance;
		IRenderer tessellator = PolygonRenderer.INSTANCE;
		tessellator.startDrawing(GL11.GL_TRIANGLES);
		int capacity = matrix.capacity() >> 4;
		for(int i = 0; i < capacity; ++i)
		{
			tessellator.setBrightness(brightness[i]);
			for(int j = 0; j < gObjList.size(); ++j)
	        {
				GroupObject group = gObjList.get(j);
				if(group.name.startsWith("side") && !(i == 0 || i == capacity - 1)){continue;}//レールの端以外は断面を描画しない, +1~2fps

				if(!this.shouldRenderObject(tileEntity, group.name, capacity, i)){continue;}//描画するかスクリプト側で判断

				for(int k = 0; k < group.faces.size(); ++k)
	            {
	            	Face face = group.faces.get(k);
	            	NGTRenderHelper.addFaceWithMatrix(face, tessellator, matrix, i, false);
	            }
	        }
		}
		tessellator.draw();
	}

	public String[] getAllObjNames()
	{
		List<GroupObject> gObj = this.modelObj.model.getGroupObjects();
		String[] aStr = new String[gObj.size()];
		for(int i = 0; i < aStr.length; ++i)
		{
			aStr[i] = gObj.get(i).name;
		}
		return aStr;
	}

	public boolean isSwitchRail(TileEntityLargeRailCore tileEntity)
	{
		return tileEntity.getAllRailMaps().length > 1;
	}

	public void renderRailMapStatic(TileEntityLargeRailSwitchCore tileEntity, RailMap rm, int max, int startIndex, int endIndex, Parts... pArray)
	{
		double[] origPos = rm.getRailPos(max, 0);
		double origHeight = rm.getRailHeight(max, 0);
		int[] startPos = tileEntity.getStartPoint();
		float[] revXZ = RailPosition.REVISION[tileEntity.getRailPositions()[0].direction];
		//レール全体の始点からの移動差分
		float moveX = (float)(origPos[1] - ((double)startPos[0] + 0.5D + (double)revXZ[0]));
		float moveZ = (float)(origPos[0] - ((double)startPos[2] + 0.5D + (double)revXZ[1]));

		//頂点-中間点
		for(int i = startIndex; i <= endIndex; ++i)
		{
			double[] p1 = rm.getRailPos(max, i);
			double h = rm.getRailHeight(max, i);
			float x0 = moveX + (float)(p1[1] - origPos[1]);
			float y0 = (float)(h - origHeight);
			float z0 = moveZ + (float)(p1[0] - origPos[0]);
			float yaw = rm.getRailRotation(max, i);
			float pitch = rm.getRailPitch(max, i);

			this.setBrightness(this.getBrightness(tileEntity.getWorld(),
					NGTMath.floor(origPos[1] + x0), tileEntity.getPos().getY(), NGTMath.floor(origPos[0] + z0)));
			GL11.glPushMatrix();
			GL11.glTranslatef(x0, y0, z0);
			GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
			for(int j = 0; j < pArray.length; ++j)
			{
				pArray[j].render(this);
			}
			GL11.glPopMatrix();
		}
	}

	public ModelObject getModelObject()
	{
		return this.modelSet.modelObj;
	}

	public void setBrightness(int par1)
	{
		GLHelper.setBrightness(par1);
	}

	//debug//////////////////////////////////////////////////////

	protected void renderRailPosArray(RailPosition[] rps, RailMap[] rms)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GLHelper.disableLighting();
		float lineWidth = (float)NGTUtilClient.getMinecraft().displayHeight * 0.005F;
		float prevLineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(lineWidth);

		double sx = rps[0].posX;
		double sy = rps[0].posY;
		double sz = rps[0].posZ;

		for(RailPosition rp : rps)
		{
			this.renderRailPos(rp, sx, sy, sz);
		}

		for(RailMap rm : rms)
		{
			this.renderRailMap(rm, sx, sy, sz);
		}

		GL11.glLineWidth(prevLineWidth);
		GLHelper.enableLighting();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void renderRailPos(RailPosition rp, double sx, double sy, double sz)
	{
		float lineLen = 2.0F;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)(rp.posX - sx), (float)(rp.posY - sy), (float)(rp.posZ - sz));

		RenderMarkerBlock.renderLine(0.0F, 0.0F, 0.0F, 0.0F, lineLen, 0.0F, 0xFF0000);

		GL11.glPushMatrix();
		GL11.glRotatef(rp.anchorYaw, 0.0F, 1.0F, 0.0F);
		RenderMarkerBlock.renderLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, rp.anchorLengthHorizontal, 0xFF0000);

		GL11.glPushMatrix();
		GL11.glRotatef(-rp.anchorPitch, 1.0F, 0.0F, 0.0F);
		RenderMarkerBlock.renderLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, rp.anchorLengthVertical, 0xFF0000);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glRotatef(rp.cantEdge, 0.0F, 0.0F, 1.0F);
		RenderMarkerBlock.renderLine(0.0F, 0.0F, 0.0F, lineLen, 0.0F, 0.0F, 0xFF0000);
		RenderMarkerBlock.renderLine(0.0F, 0.0F, 0.0F, -lineLen, 0.0F, 0.0F, 0xFF0000);
		GL11.glPopMatrix();

		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	private void renderRailMap(RailMap rm, double sx, double sy, double sz)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(rm.getStartRP().posX - sx), (float)(rm.getStartRP().posY - sy), (float)(rm.getStartRP().posZ - sz));
		int split = (int)(rm.getLength() * 2.0D);
		double[] stPoint = rm.getRailPos(split, 0);
		double startH = rm.getStartRP().posY;
		float moveX = (float)(stPoint[1] - sx);
		float moveZ = (float)(stPoint[0] - sz);
		for(int i = 0; i <= split; ++i)
		{
			double[] curPoint = rm.getRailPos(split, i);
			float x = moveX + (float)(curPoint[1] - stPoint[1]);
			float y = (float)(rm.getRailHeight(split, i) - startH);
			float z = moveZ + (float)(curPoint[0] - stPoint[0]);
			RenderMarkerBlock.renderLine(x, y, z, x, y + 1.5F, z, 0x00FF00);
		}
		GL11.glPopMatrix();
	}
}