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

package jp.ngt.ngtlib.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSet implements Comparable<BlockSet> {
    public static final BlockSet AIR = new BlockSet(Blocks.AIR, 0);

    public final int x;
    public final int y;
    public final int z;
    public final Block block;
    public final byte metadata;
    public final NBTTagCompound nbt;

    private final int blockId;//hashCode()の負荷低減

    /**
     * @param par1 Block
     * @param par2 MetaData
     *
     */
    public BlockSet(Block par1, int par2) {
        this(0, -1, 0, par1, par2);
    }

    public BlockSet(Block par1, int par2, NBTTagCompound par3) {
        this(0, -1, 0, par1, par2, par3);
    }

    /**
     * @param par1 x
     * @param par2 y
     * @param par3 z
     * @param par4 Block
     * @param par5 MetaData
     *
     */
    public BlockSet(int par1, int par2, int par3, Block par4, int par5) {
        this(par1, par2, par3, par4, par5, null);
    }

    public BlockSet(int par1, int par2, int par3, Block par4, int par5, NBTTagCompound par6) {
        this.x = par1;
        this.y = par2;
        this.z = par3;
        this.block = par4;
        this.metadata = (byte) par5;
        this.nbt = par6;

        this.blockId = Block.getIdFromBlock(par4);
    }

    public boolean hasNBT() {
        return this.nbt != null;
    }

    /**
     * @return NBTをセットした新しいBlockSet
     */
    public BlockSet setNBT(NBTTagCompound nbt) {
        return new BlockSet(this.block, this.metadata, nbt);
    }

    /**
     * 座標保存しない
     */
    public static BlockSet readFromNBT(NBTTagCompound nbt) {
        Block block = Block.getBlockFromName(nbt.getString("Block"));
        if (block == null) {
            return AIR;
        }

        int meta = 0;
        if (nbt.hasKey("Meta", 3)) {
            meta = nbt.getInteger("Meta");//データ互換性
        } else {
            meta = nbt.getByte("Meta");
        }

        if (nbt.hasKey("TagData")) {
            NBTTagCompound tagCompound = nbt.getCompoundTag("TagData");
            return new BlockSet(block, meta, tagCompound);
        } else {
            return new BlockSet(block, meta);
        }
    }

    /**
     * 座標保存しない
     */
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        String name = Block.REGISTRY.getNameForObject(this.block).toString();
        if (name == null) {
            return nbt;
        }

        nbt.setString("Block", name);
        nbt.setByte("Meta", (byte) this.metadata);
        if (this.nbt != null) {
            nbt.setTag("TagData", this.nbt);
        }
        return nbt;
    }

    public static BlockSet toBlockSet(BlockPos pos) {
        return new BlockSet(pos.getX(), pos.getY(), pos.getZ(), null, 0);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public IBlockState toBlockState() {
        return this.block.getStateFromMeta(this.metadata);
    }

    public static BlockSet getBlockSet(World world, int x, int y, int z, boolean savePos) {
        Block block = BlockUtil.getBlock(world, x, y, z);
        int meta = BlockUtil.getMetadata(world, x, y, z);
        NBTTagCompound nbt = null;
        if (block.hasTileEntity(block.getStateFromMeta(meta))) {
            TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
            if (tile != null) {
                nbt = new NBTTagCompound();
                tile.writeToNBT(nbt);
            }
        }

        if (savePos) {
            return new BlockSet(x, y, z, block, meta, nbt);
        } else {
            return new BlockSet(block, meta, nbt);
        }
    }

    /**
     * Mapのキーとして使用可能な形に変換(Y<0)
     */
    public BlockSet asKey() {
        BlockSet set = new BlockSet(0, -1, 0, this.block, this.metadata, this.nbt);
        return set;
    }

    @Override
    public boolean equals(Object par1) {
        if (par1 instanceof BlockSet) {
            BlockSet bs = (BlockSet) par1;
            boolean flagMeta = this.block == bs.block && bs.metadata == this.metadata;
            boolean falgNBT = (this.nbt != null && bs.nbt != null) ? (this.nbt.equals(bs.nbt)) : true;
            if (this.y < 0 && bs.y < 0) {
                return flagMeta && falgNBT;
            }
            return bs.x == this.x && bs.y == this.y && bs.z == this.z && flagMeta && falgNBT;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.blockId;
    }

    /**
     * Y>=0の場合、正常な結果を返さないので注意
     */
    @Override
    public int compareTo(BlockSet obj) {
        //Comparable実装するとMapが早くなるそう
        //本当はY<0とそうでないときで分けるべき
        return this.blockId - obj.blockId;
    }
}