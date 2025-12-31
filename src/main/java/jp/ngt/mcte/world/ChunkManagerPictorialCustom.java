package jp.ngt.mcte.world;

import java.util.Arrays;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

public class ChunkManagerPictorialCustom extends BiomeProvider
{
	protected WorldData worldData;

	public ChunkManagerPictorialCustom(World par1, WorldData par2)
    {
		super(par1.getWorldInfo());
		this.worldData = par2;
    }

	@Override
	public Biome getBiome(BlockPos pos, Biome BiomeIn)
    {
		return this.worldData.getWorldGenerator().getBiome(pos.getX(), pos.getZ());
    }

	@Override
	public Biome[] getBiomesForGeneration(Biome[] par1, int par2, int par3, int par4, int par5)
    {
		int i0 = par4 * par5;
        if(par1 == null || par1.length < i0)
        {
            par1 = new Biome[i0];
        }

		if(par4 == 16 && par5 == 16)
		{
			for(int i = 0; i < 16; ++i)
			{
				for(int j = 0; j < 16; ++j)
				{
					par1[i * 16 + j] = this.worldData.getWorldGenerator().getBiome(par2 + j, par3 + i);
				}
			}
		}
		else
		{
	        Arrays.fill(par1, 0, i0, Biomes.PLAINS);
		}

		return par1;
    }

	//1.8以前
	/*@Override
	public float[] getRainfall(float[] par1, int par2, int par3, int par4, int par5)
    {
		int i0 = par4 * par5;
        if(par1 == null || par1.length < i0)
        {
            par1 = new float[i0];
        }

		if(par4 == 16 && par5 == 16)
		{
			for(int i = 0; i < 16; ++i)
			{
				for(int j = 0; j < 16; ++j)
				{
					par1[i * 16 + j] = this.worldData.getWorldGenerator().getBiome(par2 + j, par3 + i).getRainfall();
				}
			}
		}
		else
		{
	        Arrays.fill(par1, 0, i0, Biomes.PLAINS.getRainfall());
		}

		return par1;
    }*/

	@Override
	public Biome[] getBiomes(Biome[] par1, int par2, int par3, int par4, int par5, boolean par6)
    {
		return this.getBiomesForGeneration(par1, par2, par3, par4, par5);
    }
}