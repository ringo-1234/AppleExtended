package jp.ngt.mcte.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.ngt.ngtlib.math.Complex;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkProviderScript implements IChunkGenerator
{
	private final Random rand;
	private final World worldObj;
	private final boolean mapFeaturesEnabled;

	public ChunkProviderScript(World par1, long par2, boolean par3)
    {
		this.rand = new Random(par2);
		this.worldObj = par1;
		this.mapFeaturesEnabled = par3;
    }

	@Override
	public Chunk generateChunk(int x, int z)
	{
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.setBlocksInChunk(x, z, chunkprimer);
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();
        for (int i = 0; i < abyte.length; ++i)
        {
        	abyte[i] = (byte)Biome.getIdForBiome(Biomes.OCEAN);
        }
        chunk.generateSkylightMap();
        return chunk;
	}

	protected void setBlocksInChunk(int par1, int par2, ChunkPrimer par3)
    {
		int x = par1 << 4;
    	int z = par2 << 4;

    	for(int i = 0; i < 16; ++i)
    	{
    		for(int j = 0; j < 16; ++j)
        	{
    			/*int height = this.mandelbrot(x + i, z + j, new Complex(0, 0));

    			for(int k = 0; k < height; ++k)
    			{
    				par3.setBlockState(i, k, j, Blocks.STONE.getDefaultState());
    			}*/

    			for(int k = 0; k < 128; ++k)
    			{
    				//初期C値をYで変化させる
    				double d0 = (double)k / 128.0D;
    				//double m = this.mandelbrot(x + i, z + j, new Complex(0, d0));
    				double m = this.mandelbox(x + i, k, z + j);
    				if(m >= 0.5D)
    				{
    					par3.setBlockState(i, k, j, Blocks.STONE.getDefaultState());
    				}
    			}
        	}
    	}
    }

	@Override
	public void populate(int x, int z)
	{
		;
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z)
	{
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		return new ArrayList<>();
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z)
	{
		;
	}

	//バニラの生成最大値は30000000
	//16777216
	protected static final double RANGE = 1.0D / 100000.0D;

	protected double mandelbrot(int x, int z, Complex cz)
	{
		double limitSq = 10.0D * 10.0D;
		int maxCount = 512;
		double r = (double)x * RANGE;
		double i = (double)z * RANGE;
		Complex c = new Complex(r, i);
		for(int count = 0; count < maxCount; ++count)
		{
			cz = cz.multiply(cz).add(c);
			if(cz.absSq() >= limitSq)
			{
				return (double)count / (double)maxCount;
			}
		}
		return 1.0D;
	}

	//クソ重い
	protected double mandelbox(int x, int y, int z)
	{
		double x2 = (double)x * RANGE;
		double y2 = (double)y * RANGE;
		double z2 = (double)z * RANGE;

		double limit = 5.0D;
		double r = 0.5D;
		double s = 1.5D;
		int maxCount = 256;
		Vec3d vc = new Vec3d(x2, y2, z2);
		Vec3d vz = new Vec3d(0.0D, 0.0D, 0.0D);
		for(int count = 0; count < maxCount; ++count)
		{
			double xd = vz.x;
			double yd = vz.y;
			double zd = vz.z;
			if(xd > 1.0D)
			{
				xd = 2.0D - xd;
			}
			else if(xd < -1.0D)
			{
				xd = -2.0D - xd;
			}

			if(yd > 1.0D)
			{
				yd = 2.0D - yd;
			}
			else if(yd < -1.0D)
			{
				yd = -2.0D - yd;
			}

			if(zd > 1.0D)
			{
				zd = 2.0D - zd;
			}
			else if(yd < -1.0D)
			{
				zd = -2.0D - zd;
			}

			vz = new Vec3d(xd, yd, zd);

			double len = vz.lengthVector();

			if(len > 0.0D)
			{
				double l2 = len;
				if(len < r)
				{
					l2 = len / (r * r);
				}
				else if(len < 1.0D)
				{
					l2 = 1.0D / len;
				}

				vz = vz.scale(l2 / len);
			}

			vz = vz.scale(s).add(vc);

			if(vz.lengthVector() > limit)
			{
				return (double)count / (double)maxCount;
			}
		}
		return 1.0D;
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}