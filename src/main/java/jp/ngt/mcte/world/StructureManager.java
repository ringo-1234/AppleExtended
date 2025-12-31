package jp.ngt.mcte.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.math.AABBInt;
import jp.ngt.rtm.entity.train.util.Formation;
import jp.ngt.rtm.entity.train.util.FormationManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.WorldSavedData;

public class StructureManager
{
	private static final String SAVE_NAME = "mcte_structures";
	private static final String STRUCTURE_PATH = "mcte/structure/";

	private static final ResourceLocation[] SAMPLES = new ResourceLocation[]{
			new ResourceLocation("mcte", "structures/chino.ngto"),
			new ResourceLocation("mcte", "structures/hoppo.ngto")
	};

	private final World world;
	private final List<Template> templates = new ArrayList<>();

	private StructureData data;

	public StructureManager(World par1)
	{
		this.world = par1;
		this.loadStructures();
		//this.init(par1);
	}

	private void loadStructures()
	{
		File folder = new File(NGTFileLoader.getModsDir().get(0), STRUCTURE_PATH);
		folder.mkdirs();
		try
		{
			List<File> list = NGTFileLoader.findFileInDirectory(folder, (file)->{return file.getName().endsWith(".ngto");});
			for(File file : list)
			{
				NGTObject ngto = NGTObject.importFromFile(file, 1.0F);
				this.templates.add(ngto.toTemplate(this.world));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		if(this.templates.isEmpty())//フォルダが空ならサンプルをコピー＆ロード
		{
			this.loadSample(folder);
		}
	}

	private void loadSample(File folder)
	{
		for(int i = 0; i < SAMPLES.length; ++i)
		{
			try
			{
				NGTObject sample = NGTObject.load(NGTFileLoader.getInputStream(SAMPLES[i]));
				sample.exportToFile(new File(folder, "sample_" + i + ".ngto"));
				this.templates.add(sample.toTemplate(this.world));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void init(World world)
	{
		//if(world instanceof WorldServer && world.provider.getDimension() == MCTE.DIMID_MEGA_CITY)
		{
			StructureData data = (StructureData)world.loadData(StructureData.class, SAVE_NAME);
			if(data == null)
			{
				data = new StructureData(SAVE_NAME);
				world.setData(SAVE_NAME, data);
			}
			this.data = data;
		}
	}

	/**
     * 地形のベースと海の生成
     * @param par1 chunkX
     * @param par2 chunkZ
     * @param par3 16*16*256
     */
    public void setBlocksInChunk(int par1, int par2, ChunkPrimer par3)
    {
    	int chunkX = par1 << 4;
    	int chunkZ = par2 << 4;
    	boolean flag = (par1 % 2) + (par2 % 2) == 1;//市松模様
    	IBlockState state = flag ? Blocks.STONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();

    	AABBInt aabb = new AABBInt(16, 1, 16);
    	aabb.repeat((x, y, z, count)->{
    		par3.setBlockState(x, y, z, state);
    	});
    }

    public void populate(int par1, int par2)
    {
    	if(this.templates.isEmpty()){return;}

    	int chunkX = par1 << 4;
    	int chunkZ = par2 << 4;

    	Rotation rotation = Rotation.values()[this.world.rand.nextInt(Rotation.values().length)];
    	Mirror mirror = Mirror.values()[this.world.rand.nextInt(Mirror.values().length)];
    	ChunkPos chunkPos = new ChunkPos(par1, par2);
    	PlacementSettings settings = new PlacementSettings().setRotation(rotation).setMirror(mirror).setChunk(chunkPos);

    	TemplateManager tm = this.world.getSaveHandler().getStructureTemplateManager();
    	//Template template = tm.getTemplate(this.world.getMinecraftServer(), id);
    	Template template = this.templates.get(this.world.rand.nextInt(this.templates.size()));
    	BlockPos center = new BlockPos(chunkX + (template.getSize().getX() / 2), 1, chunkZ + (template.getSize().getZ() / 2));
    	template.addBlocksToWorldChunk(this.world, center, settings);
    }

	public class StructureData extends WorldSavedData
	{
		public StructureData(String name)
		{
			super(name);
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt)
		{
			NBTTagList tagList = nbt.getTagList("CustomStructures", 10);
			for(int i = 0; i < tagList.tagCount(); ++i)
	    	{
	    		NBTTagCompound tag = tagList.getCompoundTagAt(i);
	    	}
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt)
		{
			NBTTagList tagList = new NBTTagList();
			for(Formation formation : FormationManager.getInstance().getFormations().values())
			{
				NBTTagCompound tag = new NBTTagCompound();
				formation.writeToNBT(tag, false);
				tagList.appendTag(tag);
			}
			nbt.setTag("CustomStructures", tagList);
			return nbt;
		}
	}

	public class StructureConfig
	{
		;
	}
}