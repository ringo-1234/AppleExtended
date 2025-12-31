package jp.ngt.mcte.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MCTEBiome extends Biome
{
	private static final BiomeProperties MEGA_CITY_Prop = new BiomeProperties("Mega City").setBaseHeight(-1.8F).setTemperature(0.2F).setRainfall(0.1F);
	public static final Biome MEGA_CITY = new MCTEBiome(MEGA_CITY_Prop);

	public MCTEBiome(BiomeProperties prop)
	{
		super(prop);
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
	}

	@SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float par1)
    {
		return 0x500000;
    }

	@Override
	public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double p_180622_6_)
    {
        this.generateTerrain(worldIn, rand, chunkPrimerIn, x, z, p_180622_6_);
    }

    protected void generateTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double p_180628_6_)
    {
        int i = worldIn.getSeaLevel();
        IBlockState iblockstate = this.topBlock;
        IBlockState iblockstate1 = this.fillerBlock;
        int j = -1;
        int k = (int)(p_180628_6_ / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int l = x & 15;
        int i1 = z & 15;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int y = 255; y >= 0; --y)
        {
            if(y <= rand.nextInt(3))
            {
                chunkPrimerIn.setBlockState(i1, y, l, Blocks.BEDROCK.getDefaultState());
            }
            else if(y <= 15)
            {
            	chunkPrimerIn.setBlockState(i1, y, l, Blocks.WATER.getDefaultState());
            }
            /*else
            {
                IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, y, l);

                if (iblockstate2.getBlock().getMaterial() == Material.air)
                {
                    j = -1;
                }
                else if (iblockstate2.getBlock() == Blocks.stone)
                {
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = null;
                            iblockstate1 = Blocks.stone.getDefaultState();
                        }
                        else if (y >= i - 4 && y <= i + 1)
                        {
                            iblockstate = this.topBlock;
                            iblockstate1 = this.fillerBlock;
                        }

                        if (y < i && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air))
                        {
                            if (this.getFloatTemperature(blockpos$mutableblockpos.set(x, y, z)) < 0.15F)
                            {
                                iblockstate = Blocks.ice.getDefaultState();
                            }
                            else
                            {
                                iblockstate = Blocks.water.getDefaultState();
                            }
                        }

                        j = k;

                        if (y >= i - 1)
                        {
                            chunkPrimerIn.setBlockState(i1, y, l, iblockstate);
                        }
                        else if (y < i - 7 - k)
                        {
                            iblockstate = null;
                            iblockstate1 = Blocks.stone.getDefaultState();
                            chunkPrimerIn.setBlockState(i1, y, l, Blocks.gravel.getDefaultState());
                        }
                        else
                        {
                            chunkPrimerIn.setBlockState(i1, y, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        chunkPrimerIn.setBlockState(i1, y, l, iblockstate1);

                        if (j == 0 && iblockstate1.getBlock() == Blocks.sand)
                        {
                            j = rand.nextInt(4) + Math.max(0, y - 63);
                            iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? Blocks.red_sandstone.getDefaultState() : Blocks.sandstone.getDefaultState();
                        }
                    }
                }
            }*/
        }
    }
}