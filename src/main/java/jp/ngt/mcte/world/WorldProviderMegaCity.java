package jp.ngt.mcte.world;

import jp.ngt.mcte.MCTE;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderMegaCity extends WorldProvider
{
	@Override
	public void init()
    {
        this.biomeProvider = new BiomeProviderSingle(MCTEBiome.MEGA_CITY);
        this.nether = false;
        //this.hasSkyLight = false;
    }

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderMegaCity(this.world, this.world.getSeed());
	}

	@Override
	public boolean canRespawnHere()
    {
        return true;
    }

	@Override
	public double getHorizon()
    {
        return 16.0D;
    }

	@Override
	public DimensionType getDimensionType()
	{
		return MCTE.MEGA_CITY;
	}
}