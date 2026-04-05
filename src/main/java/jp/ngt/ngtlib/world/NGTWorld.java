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

package jp.ngt.ngtlib.world;

import com.google.common.base.Predicate;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.*;

public class NGTWorld extends World implements IBlockAccessNGT {
    public final World world;
    public final NGTObject blockObject;
    /**
     * 明るさの取得に使用
     */
    public final int posX, posY, posZ;
    private List<TileEntity> renderTileEntities = new ArrayList<TileEntity>();
    private Map<Integer, Entity> entityIdMap = new HashMap<Integer, Entity>();
    public boolean tileEntityLoaded;

    /**
     * TileENtityのキャッシュ(描画用とは別)
     */
    protected Map<BlockPos, TileEntity> worldTileEntities = new HashMap<>();

    private int nextEntityId;

    public NGTWorld(World par1, NGTObject par2) {
        this(par1, par2, 0, -1, 0);
    }

    public NGTWorld(World par1, NGTObject par2, int x2, int y2, int z2) {
        //Server用コンストラクタ呼び出し
        super(new SaveHandlerDummy(),
                new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.FLAT), "NGT"),
                new WorldProviderDummy(), new Profiler(), (par1 == null ? true : par1.isRemote));

        this.provider.setWorld(this);
        this.villageCollection = new VillageCollection(this);

        this.world = par1;
        this.blockObject = par2;
        this.posX = x2;
        this.posY = y2;
        this.posZ = z2;
        this.loadTileEntity();
        this.loadEntity();
    }

    private void loadTileEntity() {
        //鯖側でできない処理含むためNGTRendererに移動
        this.tileEntityLoaded = false;
    }

    private void loadEntity() {
        NBTTagList tagList = this.blockObject.getEntityList();
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbt = tagList.getCompoundTagAt(i);
            Entity entity2 = this.getEntityFromNBT(nbt);

            if (entity2 != null) {
                this.addEntity(entity2);
                Entity entity = entity2;

                for (NBTTagCompound nbt2 = nbt; nbt2.hasKey("Riding", 10); nbt2 = nbt2.getCompoundTag("Riding")) {
                    Entity entity1 = EntityList.createEntityFromNBT(nbt2.getCompoundTag("Riding"), this);

                    if (entity1 != null) {
                        this.addEntity(entity1);
                        entity.startRiding(entity1);
                    }

                    entity = entity1;
                }
            }
        }

        for (int pass = 0; pass < 2; ++pass)//EntityBogieの関連付けタイミング
        {
            for (int j = 0; j < this.loadedEntityList.size(); ++j) {
                Entity entity = (Entity) this.loadedEntityList.get(j);
                double x = entity.posX;
                double y = entity.posY - entity.getYOffset();
                double z = entity.posZ;
                try {
                    entity.onUpdate();
                } catch (Exception e)//etc. cast WorldServer
                {
                    ;
                }
                entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            }
        }
    }

    private Entity getEntityFromNBT(NBTTagCompound nbt) {
        if ("Player".equals(nbt.getString("id"))) {
            ;
        }
        return EntityList.createEntityFromNBT(nbt, this);
    }

    private void addEntity(Entity entity) {
        double x = entity.posX - this.blockObject.origX;
        double y = entity.posY - this.blockObject.origY - entity.getYOffset();
        double z = entity.posZ - this.blockObject.origZ;
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
        this.loadedEntityList.add(entity);

        entity.setEntityId(++this.nextEntityId);
        this.entityIdMap.put(entity.getEntityId(), entity);
    }

    //AnvilChunkLoader
    public static NBTTagList writeEntitiesToNBT(List list) {
        NBTTagList tagList = new NBTTagList();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            //if(entity instanceof EntityPlayer){continue;}
            NBTTagCompound nbt = new NBTTagCompound();

            try {
                if (entity.writeToNBTOptional(nbt)) {
                    tagList.appendTag(nbt);
                }
            } catch (Exception e) {
                FMLLog.log(Level.ERROR, e,
                        "An Entity type %s has thrown an exception trying to write NBT.",
                        entity.getClass().getName());
            }
        }
        return tagList;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return this.blockObject.isValidPos(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }

    @Override
    public Chunk getChunkFromChunkCoords(int x, int z) {
        return null;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        int meta = newState.getBlock().getMetaFromState(newState);
        if (this.blockObject.setBlockSet(pos.getX(), pos.getY(), pos.getZ(), newState.getBlock(), meta)) {
            return true;
        }
        return false;
    }

    @Override
    public void markAndNotifyBlock(BlockPos pos, Chunk chunk, IBlockState old, IBlockState new_, int flags) {
    }

    @Override
    public Entity getEntityByID(int id) {
        return this.entityIdMap.get(id);
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        return new ArrayList();
    }

    @Override
    public boolean spawnEntity(Entity par1) {
        this.loadedEntityList.add(par1);
        return true;
    }

    /******************************************************************/

    public List<TileEntity> getTileEntityList() {
        return this.renderTileEntities;
    }

    public void setTileEntityList(List<TileEntity> list) {
        this.renderTileEntities.addAll(list);
    }

    public List<Entity> getEntityList() {
        return this.loadedEntityList;
    }

    @Override
    public BlockSet getBlockSet(int x, int y, int z) {
        return this.blockObject.getBlockSet(x, y, z);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        BlockSet set = this.getBlockSet(pos.getX(), pos.getY(), pos.getZ());
        return set.toBlockState();
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        if (this.worldTileEntities.containsKey(pos)) {
            return this.worldTileEntities.get(pos);
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockSet set = this.getBlockSet(x, y, z);
        if (set.block.hasTileEntity(set.block.getStateFromMeta(set.metadata))) {
            TileEntity tile = null;
            try {
                tile = set.block.createTileEntity(this, set.toBlockState());
                tile.setWorld(this);
                if (set.nbt != null) {
                    tile.readFromNBT((NBTTagCompound) set.nbt.copy());
                }
            } catch (Exception e) {
                return null;
            }

            if (tile instanceof TileEntityCustom) {
                ((TileEntityCustom) tile).setPos(x, y, z, x + this.blockObject.origX, y + this.blockObject.origY, z + this.blockObject.origZ);
            } else {
                tile.setPos(pos);
            }

            this.worldTileEntities.put(pos, tile);
            return tile;
        }
        return null;
    }

    @Override
    public float getLightBrightness(BlockPos pos) {
        return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
    }

    //旧getBlockLightValue
    @Override
    public int getLightFromNeighbors(BlockPos pos) {
        return this.getLightValue(pos.getX(), pos.getY(), pos.getZ(), 0, false);
    }

    /**
     * Block.getMixedBrightnessForBlock()で使用
     */
    @SideOnly(Side.CLIENT)
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
        BlockSet set = this.getBlockSet(pos.getX(), pos.getY(), pos.getZ());
        if (set.block.getUseNeighborBrightness(set.toBlockState())) {
            int up = this.getLightValue(pos.up(), type.defaultLightValue, false);//this.getLightFor(type, pos.up());
            int east = this.getLightValue(pos.east(), type.defaultLightValue, false);//this.getLightFor(type, pos.east());
            int west = this.getLightValue(pos.west(), type.defaultLightValue, false);//this.getLightFor(type, pos.west());
            int south = this.getLightValue(pos.up(), type.defaultLightValue, false);//this.getLightFor(type, pos.south());
            int north = this.getLightValue(pos.up(), type.defaultLightValue, false);//this.getLightFor(type, pos.north());
            int res = up;

            if (east > up) {
                res = east;
            }

            if (west > res) {
                res = west;
            }

            if (south > res) {
                res = south;
            }

            if (north > res) {
                res = north;
            }

            return res;
        } else {
            return this.canSeeSky(pos) ? type.defaultLightValue : this.getLightValue(pos, type.defaultLightValue, false);
        }
        //return this.getLightFor(type, pos);
    }

    @Override
    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (this.blockObject.isValidPos(x, y, z)) {
            return this.blockObject.getBlockSet(x, y, z).block.getLightValue(this.getBlockState(pos), this, pos);
        }
        return type.defaultLightValue;
    }

    private int getLightValue(BlockPos pos, int defaultValue, boolean blend) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return this.getLightValue(x, y, z, defaultValue, blend);
    }

    private int getLightValue(int x, int y, int z, int defaultValue, boolean blend) {
        BlockPos pos = new BlockPos(x, y, z);
        int skyLight = this.getLightFor(EnumSkyBlock.SKY, pos);
        int blockLight = this.getLightFor(EnumSkyBlock.BLOCK, pos);

        if (blockLight < defaultValue) {
            blockLight = defaultValue;
        }

        int brightness = NGTUtil.getLightValue(this.world, this.posX, this.posY, this.posZ);
        if (blockLight < brightness) {
            blockLight = brightness;
        }

        if (this.posY < 0) {
            skyLight = blockLight = 15;
        }

        return blend ? (skyLight << 20 | blockLight << 4) : (skyLight > blockLight ? skyLight : blockLight);
    }

    @Override
    public boolean canSeeSky(BlockPos pos) {
        int y0 = pos.getY() + 1;
        while (y0 < this.blockObject.ySize) {
            IBlockState state = this.getBlockState(new BlockPos(pos.getX(), y0, pos.getZ()));
            if (state.getBlock() != Blocks.AIR) {
                return false;
            }
            ++y0;
        }
        return true;//this.world.canBlockSeeTheSky(this.posX, this.posY, this.posZ);
    }

	/*@Override
	public int isBlockProvidingPowerTo(int x, int y, int z, int side)
	{
		return 0;
	}*/

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockSet(pos.getX(), pos.getY(), pos.getZ()).block == Blocks.AIR;
    }

    @Override
    public Biome getBiome(final BlockPos pos) {
        return Biomes.OCEAN;
    }

    @Override
    public Biome getBiomeForCoordsBody(final BlockPos pos) {
        return this.getBiome(pos);
    }

    @Override
    public int getHeight() {
        return 64;
    }

    //1.10では存在しない
	/*@Override
	public boolean extendedLevelsInChunkCache()
	{
		return false;
	}*/

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (this.blockObject.isValidPos(x, y, z)) {
            BlockSet set = this.getBlockSet(x, y, z);
            return set.block.isSideSolid(set.toBlockState(), this, pos, side);
        }
        return _default;
    }

    @Override
    public boolean checkLight(BlockPos pos)//ミニチュアのreadNBTで光更新したときのぬるぽを防ぐ
    {
        return false;
    }

    @Override
    public void markChunkDirty(BlockPos pos, TileEntity tile) {
    }
}