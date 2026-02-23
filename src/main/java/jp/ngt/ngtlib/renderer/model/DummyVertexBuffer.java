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

package jp.ngt.ngtlib.renderer.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;

/**Blockのポリゴン化用*/
public class DummyVertexBuffer extends BufferBuilder
{
	private final List<FaceWithIndex> faces = new ArrayList<>();

	public DummyVertexBuffer(int bufferSizeIn)
	{
		super(bufferSizeIn);
	}

	@Override
	public void addVertexData(int[] vertexData)
    {
		super.addVertexData(vertexData);

		FaceWithIndex face = new FaceWithIndex(4, 0);

		float[] pos = new float[3];
		float[] color = new float[4];
		float[] uvf = new float[2];
		float[] uvs = new float[2];

		for(int i = 0; i < 4; ++i)
		{
			LightUtil.unpack(vertexData, pos, this.getVertexFormat(), i, 0);
			LightUtil.unpack(vertexData, color, this.getVertexFormat(), i, 1);
			LightUtil.unpack(vertexData, uvf, this.getVertexFormat(), i, 2);
			LightUtil.unpack(vertexData, uvs, this.getVertexFormat(), i, 3);

			face.vertices[i] = Vertex.create(round(pos[0]), round(pos[1]), round(pos[2]), VecAccuracy.MEDIUM);
			face.textureCoordinates[i] = TextureCoordinate.create(round(uvf[0]), round(uvf[1]), VecAccuracy.MEDIUM);
		}

		this.faces.add(face);

		//DefaultVertexFormats.BLOCK
		//format: 4 elements:
		// 3,Position,Float
		// 4,Vertex Color,Unsigned Byte
		// 2,UV,Float
		// 2,UV,Short

		//[fx3, bx4, fx2, sx2]x4 -> [ix7]x4
    }

	private static float round(float par1)
	{
		if(par1 < 0.0001F && par1 > -0.0001F)
		{
			return 0.0F;
		}
		return par1;
	}

	@Override
	public void putBrightness4(int p1, int p2, int p3, int p4)
    {
		super.putBrightness4(p1, p2, p3, p4);
    }

	//4回呼ばれる
	@Override
	public void putColorMultiplier(float red, float green, float blue, int vertexIndex)
    {
		super.putColorMultiplier(red, green, blue, vertexIndex);
    }

	@Override
	public void putPosition(double x, double y, double z)
    {
		super.putPosition(x, y, z);

		Face face = this.faces.get(this.faces.size() - 1);
		for(int i = 0; i < face.vertices.length; ++i)
		{
			Vertex vtx = face.vertices[i];
			face.vertices[i] = Vertex.create(vtx.getX() + (float)x, vtx.getY() + (float)y, vtx.getZ() + (float)z, VecAccuracy.MEDIUM);
		}
    }

	public List<FaceWithIndex> getFaces()
	{
		return this.faces;
	}
}