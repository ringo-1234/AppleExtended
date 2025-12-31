package jp.ngt.mcte.world;

import java.util.Random;

import jp.ngt.mcte.MCTE;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class MCTEGenerator implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		WorldType type = world.getWorldInfo().getTerrainType();
		int x = chunkX << 4;
		int z = chunkZ << 4;

		if(world.provider.getDimensionType() == MCTE.MEGA_CITY)
		{
			;
		}
	}
}