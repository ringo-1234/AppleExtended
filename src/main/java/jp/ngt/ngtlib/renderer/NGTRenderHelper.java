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

package jp.ngt.ngtlib.renderer;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.model.Face;
import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.ngtlib.renderer.model.IModelNGT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NGTRenderHelper
{
	/**
	 * 移動
	 * @param buffer : 元の変換行列
	 */
	public static FloatBuffer translate(FloatBuffer buffer, float moveX, float moveY, float moveZ)
	{
		float[][] fa = {{1.0F, 0.0F, 0.0F, 0.0F}, {0.0F, 1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 1.0F, 0.0F}, {moveX, moveY, moveZ, 1.0F}};
		return multiplｙMatrix(buffer, fa);
	}

	/**
	 * 回転
	 * @param buffer : 元の変換行列
	 * @param angle : ラジアン
	 * @param coordinate : 座標軸（'X', 'Y', 'Z'）
	 */
	public static FloatBuffer rotate(FloatBuffer buffer, float angle, char coordinate)
	{
		float sin = NGTMath.getSin(angle);
		float cos = NGTMath.getCos(angle);
		switch(coordinate)
		{
		case 'X': float[][] fa0 = {{1.0F, 0.0F, 0.0F, 0.0F}, {0.0F, cos, sin, 0.0F}, {0.0F, -sin, cos, 0.0F}, {0.0F, 0.0F, 0.0F, 1.0F}};
				return multiplｙMatrix(buffer, fa0);
		case 'Y': float[][] fa1 = {{cos, 0.0F, -sin, 0.0F}, {0.0F, 1.0F, 0.0F, 0.0F}, {sin, 0.0F, cos, 0.0F}, {0.0F, 0.0F, 0.0F, 1.0F}};
				return multiplｙMatrix(buffer, fa1);
		case 'Z': float[][] fa2 = {{cos, sin, 0.0F, 0.0F}, {-sin, cos, 0.0F, 0.0F}, {0.0F, 0.0F, 1.0F, 0.0F}, {0.0F, 0.0F, 0.0F, 1.0F}};
				return multiplｙMatrix(buffer, fa2);
		}
		return buffer;
	}

	private static FloatBuffer multiplｙMatrix(FloatBuffer fb, float[][] fa)
	{
		FloatBuffer buffer = FloatBuffer.allocate(16);
		for(int i = 0; i < 4; ++i)
		{
			for(int j = 0; j < 4; ++j)
			{
				float f = fb.get(j) * fa[i][0] + fb.get(4 + j) * fa[i][1] + fb.get(8 + j) * fa[i][2] + fb.get(12 + j) * fa[i][3];
				buffer.put(i * 4 + j, f);
			}
		}
		return buffer;
	}

	/**NGTTessellatorを使用*/
	public static void renderCustomModelAll(IModelNGT model, byte matId, boolean smoothing)
	{
		renderCustomModelEveryParts(model, matId, false, smoothing);
	}

	public static void renderCustomModel(IModelNGT model, byte matId, boolean smoothing, String... parts)
	{
		renderCustomModelEveryParts(model, matId, false, smoothing, parts);
	}

	public static void renderCustomModelExcept(IModelNGT model, byte matId, boolean smoothing, String... parts)
	{
		renderCustomModelEveryParts(model, matId, true, smoothing, parts);
	}

	public static void renderCustomModelEveryParts(IModelNGT model, byte matId, boolean except, boolean smoothing, String... parts)
	{
		renderCustomModelEveryParts(model, matId, except, smoothing, GL11.GL_TRIANGLES, parts);
	}

	public static void renderCustomModelEveryParts(IModelNGT model, byte matId, boolean except, boolean smoothing, int mode, String... parts)
	{
		NGTTessellator tessellator = NGTTessellator.instance;
		//IRenderer tessellator = PolygonRenderer.INSTANCE;
		//VertexArray2 tessellator = new VertexArray2(0xFFFFFF);
		tessellator.startDrawing(mode);
		List<GroupObject> list = model.getGroupObjects();
		for(int j = 0; j < list.size(); ++j)
        {
			GroupObject group = list.get(j);
			boolean b = false;
			if(parts == null || parts.length == 0)
			{
				b = true;
			}
			else
			{
				b = except;
				for(int k = 0; k < parts.length; ++k)
				{
					if(group.name.equals(parts[k]))
					{
						b = !except;
						break;
					}
				}
			}

			if(b)
			{
				for(int k = 0; k < group.faces.size(); ++k)
	            {
	            	Face face = group.faces.get(k);
	            	if(face.materialId == matId)
	            	{
	            		addFace(face, tessellator, smoothing);
	            	}
	            }
			}
        }
		tessellator.draw();
		//tessellator.render();
	}

	/**NGTTessellatorを使用*/
	public static void addFace(Face face, IRenderer tessellator, boolean smoothing)
	{
		addFaceWithMatrix(face, tessellator, null, -1, smoothing);
	}

	/**NGTTessellatorを使用*/
	public static void addFaceWithMatrix(Face face, IRenderer tessellator, FloatBuffer matrix, int index, boolean smoothing)
	{
		if(!smoothing)
		{
			tessellator.setNormal(face.faceNormal.getX(), face.faceNormal.getY(), face.faceNormal.getZ());
		}

        for(int i = 0; i < face.vertices.length; ++i)
        {
        	if(smoothing)
        	{
        		tessellator.setNormal(face.vertexNormals[i].getX(), face.vertexNormals[i].getY(), face.vertexNormals[i].getZ());
        	}

        	boolean hasTexture = (face.textureCoordinates != null) && (face.textureCoordinates.length > 0) && (face.textureCoordinates[0] != null);
            if(hasTexture)
            {
                if(matrix == null)
            	{
            		tessellator.addVertexWithUV(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), face.textureCoordinates[i].getU(), face.textureCoordinates[i].getV());
            	}
            	else
            	{
            		addVertexWithMatrix(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), face.textureCoordinates[i].getU(), face.textureCoordinates[i].getV(), tessellator, matrix, index);
            	}
            }
            else
            {
            	if(matrix == null)
            	{
            		tessellator.addVertexWithUV(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), 0.0F, 0.0F);
            	}
            	else
            	{
            		addVertexWithMatrix(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), 0.0F, 0.0F, tessellator, matrix, index);
            	}
            }
        }
	}

	public static void addVertexWithMatrix(float x, float y, float z, float u, float v, IRenderer tessellator, FloatBuffer matrix, int index)
	{
		int i = index << 4;
		float x0 = x * matrix.get(i + 0) + y * matrix.get(i + 4) + z * matrix.get(i + 8) + matrix.get(i + 12);
		float y0 = x * matrix.get(i + 1) + y * matrix.get(i + 5) + z * matrix.get(i + 9) + matrix.get(i + 13);
		float z0 = x * matrix.get(i + 2) + y * matrix.get(i + 6) + z * matrix.get(i + 10) + matrix.get(i + 14);
		tessellator.addVertexWithUV(x0, y0, z0, u, v);
	}

	public static void addQuadGuiFaceWithUV(float minX, float minY, float maxX, float maxY, float z, float uMin, float vMin, float uMax, float vMax)
    {
		NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.addVertexWithUV(minX, maxY, z, uMin, vMax);
        tessellator.addVertexWithUV(maxX, maxY, z, uMax, vMax);
        tessellator.addVertexWithUV(maxX, minY, z, uMax, vMin);
        tessellator.addVertexWithUV(minX, minY, z, uMin, vMin);
    }

	public static void addQuadGuiFace(float minX, float minY, float maxX, float maxY, float z)
    {
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.addVertex(minX, maxY, z);
        tessellator.addVertex(maxX, maxY, z);
        tessellator.addVertex(maxX, minY, z);
        tessellator.addVertex(minX, minY, z);
    }

	public static void addQuadGuiFaceWithSize(float minX, float minY, float width, float height, float z)
    {
		addQuadGuiFace(minX, minY, minX + width, minY + height, z);
    }

	public static void addQuadGuiFrame(float minX, float minY, float maxX, float maxY, float z)
    {
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.addVertex(minX, maxY, z);
        tessellator.addVertex(maxX, maxY, z);

        tessellator.addVertex(maxX, maxY, z);
        tessellator.addVertex(maxX, minY, z);

        tessellator.addVertex(maxX, minY, z);
        tessellator.addVertex(minX, minY, z);

        tessellator.addVertex(minX, minY, z);
        tessellator.addVertex(minX, maxY, z);
    }

	public static void addQuadGuiFrameWithSize(float minX, float minY, float width, float height, float z)
    {
		addQuadGuiFrame(minX, minY, minX + width, minY + height, z);
    }

	@Deprecated
	public static void setColor(int color)
	{
		GLHelper.setColor(color, 0xFF);
	}
}