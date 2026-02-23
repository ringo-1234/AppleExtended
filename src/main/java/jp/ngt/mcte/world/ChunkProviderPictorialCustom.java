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

package jp.ngt.mcte.world;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.*;

import java.util.List;
import java.util.Random;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderPictorialCustom implements IChunkGenerator
{
	/** RNG. */
    private Random rand;
    private NoiseGeneratorOctaves noiseOct0;
    private NoiseGeneratorOctaves noiseOct1;
    private NoiseGeneratorOctaves noiseOct2;
    private NoiseGeneratorPerlin noisePer0;
    public NoiseGeneratorOctaves noiseGen5;
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private World worldObj;
    /** are map structures going to be generated (e.g. strongholds) */
    private final boolean mapFeaturesEnabled;
    private WorldType field_147435_p;
    private final double[] field_147434_q;
    private final float[] parabolicField;
    private double[] stoneNoise = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private MapGenVillage villageGenerator = new MapGenVillage();
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    private MapGenBase ravineGenerator = new MapGenRavine();
    private Biome[] biomesForGeneration;

    private WorldData worldData;

    {
        caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
        strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, STRONGHOLD);
        villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, VILLAGE);
        mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator, SCATTERED_FEATURE);
        ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
    }

    public ChunkProviderPictorialCustom(World par1, long par2, boolean par3, WorldData par4)
    {
        this.worldObj = par1;
        this.mapFeaturesEnabled = par3;
        this.worldData = par4;
        this.field_147435_p = par1.getWorldInfo().getTerrainType();
        this.rand = new Random(par2);
        this.noiseOct0 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseOct1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseOct2 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noisePer0 = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147434_q = new double[825];
        this.parabolicField = new float[25];

        for (int j = -2; j <= 2; ++j)
        {
            for (int k = -2; k <= 2; ++k)
            {
                float f = 10.0F / (float)NGTMath.firstSqrt((float)(j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }

        ContextOverworld ctx = new ContextOverworld(noiseOct0, noiseOct1, noiseOct2, noisePer0, noiseGen5, noiseGen6, mobSpawnerNoise);
        ctx = TerrainGen.getModdedNoiseGenerators(par1, this.rand, ctx);
        this.noiseOct0 = ctx.getLPerlin1();
        this.noiseOct1 = ctx.getLPerlin2();
        this.noiseOct2 = ctx.getPerlin();
        this.noisePer0 = ctx.getHeight();
        this.noiseGen5 = ctx.getScale();
        this.noiseGen6 = ctx.getDepth();
        this.mobSpawnerNoise = ctx.getForest();
    }

    /**
     * 地形のベースと海の生成
     * @param par1 chunkX
     * @param par2 chunkZ
     * @param par3 16*16*256
     * */
    public void setBlocksInChunk(int par1, int par2, ChunkPrimer par3)
    {
    	int x = par1 << 4;
    	int z = par2 << 4;

    	for(int i = 0; i < 16; ++i)
    	{
    		for(int j = 0; j < 16; ++j)
        	{
    			int height = this.worldData.getWorldGenerator().getHeight(x + i, z + j);
    			int y = height;
    			if(this.worldData.seaLevel > y)
    			{
    				y = this.worldData.seaLevel;
    			}
    			for(int k = 0; k < y; ++k)
    			{
    				if(k < height)
    				{
    					//jとk逆では?(動作未確認)
    					par3.setBlockState(i, j, k, this.worldData.baseBlock.getDefaultState());
    				}
    				else
    				{
    					par3.setBlockState(i, j, k, Blocks.WATER.getDefaultState());
    				}
    			}
        	}
    	}
    }

    /**岩盤の生成もここで行われる*/
    public void replaceBlocksForBiome(int par1, int par2, ChunkPrimer par4, Biome[] par5)
    {
        double d0 = 0.03125D;
        this.stoneNoise = this.noisePer0.getRegion(this.stoneNoise, (double)(par1 * 16), (double)(par2 * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

        for(int k = 0; k < 16; ++k)
        {
            for(int l = 0; l < 16; ++l)
            {
                Biome Biome = par5[l + k * 16];
                this.genBiomeTerrain(Biome, this.worldObj, this.rand, par4, par1 * 16 + k, par2 * 16 + l, this.stoneNoise[l + k * 16]);
                //Biome.genTerrainBlocks(this.worldObj, this.rand, par3, par4, par1 * 16 + k, par2 * 16 + l, this.stoneNoise[l + k * 16]);
            }
        }
    }

    private void genBiomeTerrain(Biome biome, World world, Random random, ChunkPrimer primer, int x, int z, double noise)
    {
    	int i = world.getSeaLevel();
        IBlockState iblockstate = biome.topBlock;
        IBlockState iblockstate1 = biome.fillerBlock;
        int j = -1;
        int k = (int)(noise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int l = x & 15;
        int i1 = z & 15;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j1 = 255; j1 >= 0; --j1)
        {
            if (j1 <= rand.nextInt(5))
            {
            	primer.setBlockState(i1, j1, l, Blocks.BEDROCK.getDefaultState());
            }
            else
            {
                IBlockState iblockstate2 = primer.getBlockState(i1, j1, l);

                if(iblockstate2.getBlock().getMaterial(iblockstate2) == Material.AIR)
                {
                    j = -1;
                }
                else if (iblockstate2.getBlock() == Blocks.STONE)
                {
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = null;
                            iblockstate1 = Blocks.STONE.getDefaultState();
                        }
                        else if (j1 >= i - 4 && j1 <= i + 1)
                        {
                            iblockstate = biome.topBlock;
                            iblockstate1 = biome.fillerBlock;
                        }

                        if(j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR))
                        {
                            if(biome.getTemperature(blockpos$mutableblockpos.setPos(x, j1, z)) < 0.15F)
                            {
                                iblockstate = Blocks.ICE.getDefaultState();
                            }
                            else
                            {
                                iblockstate = Blocks.WATER.getDefaultState();
                            }
                        }

                        j = k;

                        if (j1 >= i - 1)
                        {
                            primer.setBlockState(i1, j1, l, iblockstate);
                        }
                        else if (j1 < i - 7 - k)
                        {
                            iblockstate = null;
                            iblockstate1 = Blocks.STONE.getDefaultState();
                            primer.setBlockState(i1, j1, l, Blocks.GRAVEL.getDefaultState());
                        }
                        else
                        {
                        	primer.setBlockState(i1, j1, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        primer.setBlockState(i1, j1, l, iblockstate1);

                        if (j == 0 && iblockstate1.getBlock() == Blocks.SAND)
                        {
                            j = rand.nextInt(4) + Math.max(0, j1 - 63);
                            iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk generateChunk(int x, int z)
    {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.setBlocksInChunk(x, z, chunkprimer);
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.replaceBlocksForBiome(x, z, chunkprimer, this.biomesForGeneration);

        if(this.mapFeaturesEnabled)
        {
        	this.caveGenerator.generate(this.worldObj, x, z, chunkprimer);
            this.ravineGenerator.generate(this.worldObj, x, z, chunkprimer);
            this.mineshaftGenerator.generate(this.worldObj, x, z, chunkprimer);
            this.villageGenerator.generate(this.worldObj, x, z, chunkprimer);
            this.strongholdGenerator.generate(this.worldObj, x, z, chunkprimer);
            this.scatteredFeatureGenerator.generate(this.worldObj, x, z, chunkprimer);
            //this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i)
        {
        	abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /**Populates chunk with ores etc etc*/
    @Override
    public void populate(int x, int z)
    {
        BlockFalling.fallInstantly = true;
        int k = x * 16;
        int l = z * 16;
        BlockPos blockpos = new BlockPos(k, 0, l);
        Biome Biome = this.worldObj.getBiomeForCoordsBody(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long ln1 = this.rand.nextLong() / 2L * 2L + 1L;
        long ln2 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x * ln1 + (long)z * ln2 ^ this.worldObj.getSeed());
        boolean flag = false;
        ChunkPos chunkcoordintpair = new ChunkPos(x, z);

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(this, worldObj, rand, x, z, flag));

        if (this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
            flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
            this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
            this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }

        if (Biome != Biomes.DESERT && Biome != Biomes.DESERT_HILLS && !flag && this.rand.nextInt(4) == 0
            && TerrainGen.populate(this, worldObj, rand, x, z, flag, LAKE))
        {
        	int i1 = this.rand.nextInt(16) + 8;
            int j1 = this.rand.nextInt(256);
            int k1 = this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.WATER)).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
        }

        if (TerrainGen.populate(this, worldObj, rand, x, z, flag, LAVA) && !flag && this.rand.nextInt(8) == 0)
        {
        	int i2 = this.rand.nextInt(16) + 8;
            int l2 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int k3 = this.rand.nextInt(16) + 8;

            if (l2 < 63 || this.rand.nextInt(10) == 0)
            {
                (new WorldGenLakes(Blocks.LAVA)).generate(this.worldObj, this.rand, blockpos.add(i2, l2, k3));
            }
        }

        boolean doGen = TerrainGen.populate(this, worldObj, rand, x, z, flag, DUNGEON);
        for (int j2 = 0; doGen && j2 < 8; ++j2)
        {
            int i3 = this.rand.nextInt(16) + 8;
            int l3 = this.rand.nextInt(256);
            int l1 = this.rand.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
        }

        Biome.decorate(this.worldObj, this.rand, new BlockPos(k, 0, l));
        if (TerrainGen.populate(this, worldObj, rand, x, z, flag, ANIMALS))
        {
        	WorldEntitySpawner.performWorldGenSpawning(this.worldObj, Biome, k + 8, l + 8, 16, 16, this.rand);
        }
        blockpos = blockpos.add(8, 0, 8);

        doGen = TerrainGen.populate(this, worldObj, rand, x, z, flag, ICE);
        for (int k2 = 0; doGen && k2 < 16; ++k2)
        {
            for (int j3 = 0; j3 < 16; ++j3)
            {
                BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
                BlockPos blockpos2 = blockpos1.down();

                if (this.worldObj.canBlockFreezeWater(blockpos2))
                {
                    this.worldObj.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
                }

                if (this.worldObj.canSnowAt(blockpos1, true))
                {
                    this.worldObj.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
                }
            }
        }

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(this, worldObj, rand, x, z, flag));

        BlockFalling.fallInstantly = false;
    }

    /**Returns a list of creatures of the specified type that can spawn at the given location.*/
    @Override
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, BlockPos pos)
    {
        Biome Biome = this.worldObj.getBiomeForCoordsBody(pos);
        return p_73155_1_ == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos) ? this.scatteredFeatureGenerator.getMonsters() : Biome.getSpawnableList(p_73155_1_);
    }

    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored)
	{
        return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.getNearestStructurePos(world, position, false) : null;
    }

    @Override
    public void recreateStructures(Chunk p_180514_1_, int p_82695_1_, int p_82695_2_)
    {
        if (this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.generate(this.worldObj, p_82695_1_, p_82695_2_, null);
            this.villageGenerator.generate(this.worldObj, p_82695_1_, p_82695_2_, null);
            this.strongholdGenerator.generate(this.worldObj, p_82695_1_, p_82695_2_, null);
            this.scatteredFeatureGenerator.generate(this.worldObj, p_82695_1_, p_82695_2_, null);
        }
    }

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z)
	{
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}