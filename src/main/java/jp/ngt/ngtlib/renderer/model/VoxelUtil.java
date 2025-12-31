package jp.ngt.ngtlib.renderer.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class VoxelUtil
{
	private static final String DEFAULT_MATERIAL = "mat1";

	public static void exportToPolygon(NGTObject ngto, File file)
	{
		String mtlName = file.getName().replace("obj", "mtl");
		String texName = file.getName().replace("obj", "png");

		String obj = getObjAsText(getVertexes(ngto), mtlName);
		NGTText.writeToText(file, obj);

		String mtl = getMtlAsText(texName);
		NGTText.writeToText(new File(file.getParentFile(), mtlName), mtl);

        try
        {
        	saveBlockTexture(new File(file.getParentFile(), texName));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
	}

	public static NGTObject importFromPolygon(File file, float scale)
	{
		PolygonModel model = ModelLoader.loadModel(file, VecAccuracy.MEDIUM);
		return toVoxel(model, scale);
	}

	private static String getObjAsText(DummyVertexBuffer buffer, String mtlFile)
	{
		//List<FaceWithIndex> faces = quadToTriangle(buffer.getFaces());
		List<FaceWithIndex> faces = buffer.getFaces();
		List<Vertex> vertexes = new ArrayList<>();
		List<TextureCoordinate> texCoordinates = new ArrayList<>();

		for(FaceWithIndex face : faces)
		{
			for(int i = 0; i < face.vertices.length; ++i)
			{
				Vertex vtx = face.vertices[i];
				int index = vertexes.indexOf(vtx);
				if(index < 0)
				{
					index = vertexes.size();
					vertexes.add(vtx);
				}
				face.vtxIndexes[i] = index + 1;//objのインデックスは1から始まる

				TextureCoordinate texCo = face.textureCoordinates[i];
				index = texCoordinates.indexOf(texCo);
				if(index < 0)
				{
					index = texCoordinates.size();
					texCoordinates.add(texCo);
				}
				face.uvIndexes[i] = index + 1;//objのインデックスは1から始まる
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("#NGTLib v" + NGTCore.VERSION + " Obj File\n");

		sb.append("mtllib " + mtlFile + "\n");
		sb.append("o default\n");
		sb.append("usemtl " + DEFAULT_MATERIAL + "\n");

		for(Vertex vtx : vertexes)
		{
			sb.append("v ");
			sb.append(vtx.getX());
			sb.append(" ");
			sb.append(vtx.getY());
			sb.append(" ");
			sb.append(vtx.getZ());
			sb.append("\n");
		}

		for(TextureCoordinate tex : texCoordinates)
		{
			sb.append("vt ");
			sb.append(tex.getU());
			sb.append(" ");
			sb.append(tex.getV());
			sb.append("\n");
		}

		for(FaceWithIndex face : faces)
		{
			sb.append("f");
			for(int i = 0; i < face.vertices.length; ++i)
			{
				sb.append(" ");
				sb.append(face.vtxIndexes[i]);
				sb.append("/");
				sb.append(face.uvIndexes[i]);
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	//三角面化不要
	@Deprecated
	private static List<FaceWithIndex> quadToTriangle(List<FaceWithIndex> quadFaces)
	{
		List<FaceWithIndex> faces = new ArrayList<>();
		for(Face oldFace : quadFaces)
		{
			FaceWithIndex face1 = new FaceWithIndex(3, 0);
			face1.vertices[0] = oldFace.vertices[0];
			face1.vertices[1] = oldFace.vertices[1];
			face1.vertices[2] = oldFace.vertices[2];
			face1.textureCoordinates[0] = oldFace.textureCoordinates[0];
			face1.textureCoordinates[1] = oldFace.textureCoordinates[1];
			face1.textureCoordinates[2] = oldFace.textureCoordinates[2];
			faces.add(face1);

			FaceWithIndex face2 = new FaceWithIndex(3, 0);
			face2.vertices[0] = oldFace.vertices[2];
			face2.vertices[1] = oldFace.vertices[3];
			face2.vertices[2] = oldFace.vertices[0];
			face2.textureCoordinates[0] = oldFace.textureCoordinates[2];
			face2.textureCoordinates[1] = oldFace.textureCoordinates[3];
			face2.textureCoordinates[2] = oldFace.textureCoordinates[0];
			faces.add(face2);
		}
		return faces;
	}

	private static DummyVertexBuffer getVertexes(NGTObject ngto)
	{
		NGTWorld world = new NGTWorld(NGTUtil.getClientWorld(), ngto);

		int bufSize = (ngto.blockList.size() * 0x280000) >> 12;
    	if(bufSize <= 0)
    	{
    		bufSize = 1 << 12;
    	}
    	DummyVertexBuffer renderer = new DummyVertexBuffer(bufSize);
    	renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    	for(int i = 0; i < ngto.xSize; ++i)
        {
        	for(int j = 0; j < ngto.ySize; ++j)
	        {
        		for(int k = 0; k < ngto.zSize; ++k)
    	        {
    	        	BlockSet set = ngto.getBlockSet(i, j, k);
    	        	if(set.toBlockState().getMaterial() != Material.AIR)
                    {
    	        		BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    	        		IBlockState state = set.block.getStateFromMeta(set.metadata);
    	        		dispatcher.renderBlock(state, new BlockPos(i, j, k), world, renderer);
                    }
    	        }
	        }
        }
    	renderer.finishDrawing();
    	return renderer;
	}

	private static String getMtlAsText(String texFile)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("#NGTLib v" + NGTCore.VERSION + " Mtl File\n");
		sb.append("newmtl " + DEFAULT_MATERIAL + "\n");

		sb.append("Ns 7.843137\n");
		sb.append("Ka 0.000000 0.000000 0.000000\n");
		sb.append("Kd 0.800000 0.800000 0.800000\n");
		sb.append("Ks 0.000000 0.000000 0.000000\n");
		sb.append("Ni 1.000000\n");
		sb.append("d 0.000000\n");
		sb.append("illum 2\n");

		sb.append("map_Kd " + texFile + "\n");
		sb.append("map_d " + texFile + "\n");

		return sb.toString();
	}

	private static void saveBlockTexture(File file) throws IOException
	{
		int w = GLHelper.getBlockTextureWidth();
		int h = GLHelper.getBlockTextureHeight();
        IntBuffer buffer = GLHelper.getBlockTexture(w, h);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < buffer.capacity(); ++i)
		{
			int color = buffer.get(i);
			image.getRaster().getDataBuffer().setElem(i, color);
		}
    	ImageIO.write(image, "png", file);
    	NGTLog.debug("Save texture : " + file.getAbsolutePath());
	}

	public static NGTObject toVoxel(PolygonModel model, float scale)
	{
		int maxFace = 0;
		int faceCount = 0;

		float minX, minY, minZ;
		minX = minY = minZ = Float.MAX_VALUE;
		float maxX, maxY, maxZ;
		maxX = maxY = maxZ = Float.MIN_VALUE;
		for(GroupObject go : model.groupObjects)
		{
			for(Face face : go.faces)
			{
				++maxFace;
				for(Vertex vtx : face.vertices)
				{
					if(minX > vtx.getX())
					{
						minX = vtx.getX();
					}

					if(maxX < vtx.getX())
					{
						maxX = vtx.getX();
					}

					if(minY > vtx.getY())
					{
						minY = vtx.getY();
					}

					if(maxY < vtx.getY())
					{
						maxY = vtx.getY();
					}

					if(minZ > vtx.getZ())
					{
						minZ = vtx.getZ();
					}

					if(maxZ < vtx.getZ())
					{
						maxZ = vtx.getZ();
					}
				}
			}
		}

		int xSize = NGTMath.floor((maxX - minX) / scale);
		int ySize = NGTMath.floor((maxY - minY) / scale);
		int zSize = NGTMath.floor((maxZ - minZ) / scale);

		//面をボクセル化
		int[][][] posArray = new int[xSize][ySize][zSize];
		for(GroupObject go : model.groupObjects)
		{
			for(Face face : go.faces)
			{
				genVoxel(face, 1.0D / scale, -minX, -minY, -minZ, posArray);
				++faceCount;
				if(faceCount % 1000 == 0)
				{
					NGTLog.debug("Convert Face to Voxel " + faceCount + "/" + maxFace);
				}
			}
		}

		//空気Blockで埋める
		List<BlockSet> list = new ArrayList<>();
		int size = xSize * ySize * zSize;
		for(int i = 0; i < size; ++i)
		{
			list.add(BlockSet.AIR);
		}

		//目的のブロックで置換
		NGTObject ngto = NGTObject.createNGTO(list, xSize, ySize, zSize, 0, 0, 0);
		for(int i = 0; i < xSize; ++i)
		{
			for(int j = 0; j < ySize; ++j)
			{
				for(int k = 0; k < zSize; ++k)
				{
					if(posArray[i][j][k] > 0)
					{
						ngto.setBlockSet(i, j, k, Blocks.STONE, 0);
					}
				}
			}
		}
		return ngto;
	}

	private static void genVoxel(Face face, double scale, float offsetX, float offsetY, float offsetZ, int[][][] posArray)
	{
		int vtxCount = face.vertices.length / 3;
		for(int i = 0; i < vtxCount; ++i)
		{
			genVoxel(face.vertices[i * 3], face.vertices[i * 3 + 1], face.vertices[i * 3 + 2], scale, offsetX, offsetY, offsetZ, posArray);
		}
	}

	private static void genVoxel(Vertex vtx1, Vertex vtx2, Vertex vtx3, double scale, float offsetX, float offsetY, float offsetZ, int[][][] posArray)
	{
		double x = vtx1.getX() * scale;
		double y = vtx1.getY() * scale;
		double z = vtx1.getZ() * scale;
		Vec3 vecA = PooledVec3.create(x, y, z);
		x = (vtx2.getX() - vtx1.getX()) * scale;
		y = (vtx2.getY() - vtx1.getY()) * scale;
		z = (vtx2.getZ() - vtx1.getZ()) * scale;
		Vec3 vecAB = PooledVec3.create(x, y, z);
		x = (vtx3.getX() - vtx2.getX()) * scale;
		y = (vtx3.getY() - vtx2.getY()) * scale;
		z = (vtx3.getZ() - vtx2.getZ()) * scale;
		Vec3 vecBC = PooledVec3.create(x, y, z);
		double lenAB = vecAB.length();
		double lenBC = vecBC.length();
		int lenI = NGTMath.floor((lenAB > lenBC ? lenAB : lenBC) * 2.0D);
		for(int i = 0; i < lenI; ++i)
		{
			double di = (double)i / (double)lenI;
			for(int j = 0; j < i; ++j)
			{
				double dj = (double)j / (double)lenI;
				int ix = NGTMath.floor(vecA.getX() + vecAB.getX() * di + vecBC.getX() * dj + offsetX * scale);
				int iy = NGTMath.floor(vecA.getY() + vecAB.getY() * di + vecBC.getY() * dj + offsetY * scale);
				int iz = NGTMath.floor(vecA.getZ() + vecAB.getZ() * di + vecBC.getZ() * dj + offsetZ * scale);
				if(ix > 0 && ix < posArray.length && iy > 0 && iy < posArray[0].length && iz > 0 && iz < posArray[0][0].length)
				{
					posArray[ix][iy][iz] = 1;
				}
			}
		}
	}
}