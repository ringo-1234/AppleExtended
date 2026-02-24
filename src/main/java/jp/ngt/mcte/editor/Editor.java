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

package jp.ngt.mcte.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.block.BlockMinesweeper.MinesweeperType;
import jp.ngt.mcte.block.TileEntityMinesweeper;
import jp.ngt.mcte.editor.filter.Repeatable;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.AABBInt;
import jp.ngt.ngtlib.util.Stack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import jp.apple.log.AppleLogger;

public class Editor
{
	//public static final byte EditType_Delete = 0;
	//public static final byte EditType_Cut = 1;
	//public static final byte EditType_Copy = 2;
	//public static final byte EditType_Paste = 3;
	//public static final byte EditType_Fill_1 = 4;
	//public static final byte EditType_Fill_2 = 5;
	public static final byte EditType_Replace = 6;
	public static final byte EditType_Clone = 7;
	//public static final byte EditType_DelEntity = 8;
	public static final byte EditType_Minesweeper = 9;
	public static final byte EditType_Miniature = 10;

	public static final byte EditMode_0 = 0;
	public static final byte EditMode_1 = 1;
	public static final byte EditMode_VisibleBox_0 = 2;
	public static final byte EditMode_VisibleBox_1 = 3;
	public static final byte EditMode_Max = 3;

	private final EntityEditor editorEntity;

	private WorldSnapshot clipboard;
	private Stack<WorldSnapshot> history = new Stack<>(MCTE.numberOfUndo);

	public Editor(EntityEditor par1)
	{
		this.editorEntity = par1;
	}

	public static EntityEditor getNewEditor(World world, EntityPlayer player, int x, int y, int z)
	{
		if(world.isRemote){return null;}
		EntityEditor entity = new EntityEditor(world, player, x, y, z);
		Editor editor = new Editor(entity);
		EditorManager.INSTANCE.add(player.getName(), editor);
		return entity;
	}

	public EntityEditor getEntity()
	{
		return this.editorEntity;
	}

	public World getWorld()
	{
		return this.getEntity().getEntityWorld();
	}

	public AABBInt getSelectBox()
	{
		int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;

        int[] start = this.getEntity().getPos(EntityEditor.START_POS);
        int[] end = this.getEntity().getPos(EntityEditor.END_POS);
		if((start[0] == 0 && start[1] == 0 && start[2] == 0) || (end[0] == 0 && end[1] == 0 && end[2] == 0))
    	{
    		return null;//0バグ回避
    	}

        minX = (start[0] < end[0]) ? start[0] : end[0];
        maxX = (start[0] < end[0]) ? end[0] : start[0];
        minY = (start[1] < end[1]) ? start[1] : end[1];
        maxY = (start[1] < end[1]) ? end[1] : start[1];
        minZ = (start[2] < end[2]) ? start[2] : end[2];
        maxZ = (start[2] < end[2]) ? end[2] : start[2];
    	maxX += 1;
    	maxY += 1;
    	maxZ += 1;
    	return new AABBInt(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public AABBInt getPasteBox()
	{
		int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;

        byte b = this.getEntity().getEditMode();
    	if(!(b == EditMode_VisibleBox_0 || b == EditMode_VisibleBox_1))
    	{
    		NGTLog.debug("[MCTE](Edit) Not paste mode");
    		return null;
    	}

    	RayTraceResult target = this.getEntity().getTarget(true);
		if(target == null)
    	{
			NGTLog.debug("[MCTE](Edit) MovingObjectPosition not found");
    		return null;
    	}

		//貼り付け範囲とクリップボードの範囲が異なる場合
		int[] box = this.getEntity().getPos(EntityEditor.PASTE_BOX);
		if(this.clipboard.getSize() != box[0] * box[1] * box[2])
		{
			this.getEntity().updateBlockList(null);
			NGTLog.debug("[MCTE](Edit) Illegal block list size");
			return null;
		}

    	minX = target.getBlockPos().getX();
    	minY = target.getBlockPos().getY();
    	minZ = target.getBlockPos().getZ();
    	maxX = minX + box[0];
    	maxY = minY + box[1];
    	maxZ = minZ + box[2];

    	return new AABBInt(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * 選択領域の編集
	 * @return 選択領域のサイズ+始点 (NGTO出力で使用)
	 */
	public boolean editBlocks(byte editType, float par2)
	{
		World world = this.getWorld();

		if(!this.getEntity().isSelectEnd())
		{
			NGTLog.debug("[MCTE](Edit) Not select end");
			return false;
		}

		if(world.isRemote)
		{
			NGTLog.debug("[MCTE](Edit) Can't edit in Client");
			return false;
		}

        AABBInt box = this.getSelectBox();
        if(box == null)
        {
        	return false;
        }

        if(editType == EditType_Minesweeper)
		{
        	if(box.maxY - box.minY != 1)
        	{
        		return false;
        	}
		}

        if(editType == EditType_Replace || editType == EditType_Minesweeper)
        {
        	this.record(box);
        }

        List<BlockSet> list = new ArrayList<>();//コピー用

        Repeatable repeator = null;

        if(editType == EditType_Replace)
        {
        	repeator = (rbox, index, rep, rx, ry, rz)->{
        		Block block0 = this.getEntity().getSlotBlock(0);
    			Block block1 = this.getEntity().getSlotBlock(1);
    			if(block0 != null && block1 != null)
				{
    				int meta0 = this.getEntity().getSlotBlockMetadata(0);
    				int meta1 = this.getEntity().getSlotBlockMetadata(1);
    				boolean flag0 = (BlockUtil.getBlock(world, rx, ry, rz) == block0);
    				boolean flag1 = (BlockUtil.getMetadata(world, rx, ry, rz) == meta0);
    				if(flag0 && flag1)
    				{
    					this.setBlock(rx, ry, rz, block1, meta1, true);
    				}
				}
            };
        }
        else if(editType == EditType_Clone)
        {
        	repeator = (rbox, index, rep, rx, ry, rz)->{
        		if(this.getEntity().hasCloneBox())
    			{
    				BlockSet blockSet = this.getBlockSet(rx, ry, rz);
	    			int[] box1 = this.getEntity().getCloneBox();
	    			for(int l = 1; l < box1[3] + 1; ++l)
	    			{
	    				int x = rx + (box1[0] * l);
	    				int y = ry + (box1[1] * l);
	    				int z = rz + (box1[2] * l);
	    				this.setBlock(x, y, z, blockSet, true);
	    			}
    			}
            };
        }
        else if(editType == EditType_Minesweeper)
		{
        	repeator = (rbox, index, rep, rx, ry, rz)->{
        		int random = world.rand.nextInt((int)par2);
    			int meta = (random == 0) ? MinesweeperType.MINE.id : MinesweeperType.NONE.id;
    			this.setBlock(rx, ry, rz, MCTE.minesweeper, meta, true);
    			TileEntityMinesweeper tile = (TileEntityMinesweeper)BlockUtil.getTileEntity(world, rx, ry, rz);
    			tile.setCenter(box.minX, box.minZ);
    			tile.setSize(box.maxX - box.minX, box.maxZ - box.minZ);
            };
		}

        if(repeator != null)
        {
        	this.repeat(box, repeator, 1);
        }

    	if(editType == EditType_Miniature)
    	{
    		//クリップボードの同期はしない(ItemのNGTO出力と合わせ2倍の負荷になるから)
    		NGTObject object = this.copy(this.getSelectBox(), "notSync").convertNGTO();
			this.getEntity().dropMiniature(object, par2);
    	}

    	return true;
	}

	/**クリップボード内のブロックを行列変換*/
	public void transformBlocks(EditorTransform type)
	{
		BlockSet[] blocks = new BlockSet[this.clipboard.getSize()];
		int[] box = this.getEntity().getPos(EntityEditor.PASTE_BOX);
        int xSize = box[0];
        int ySize = box[1];
        int zSize = box[2];
        //元のサイズ
        int xSize2 = xSize;
        int ySize2 = ySize;
        int zSize2 = zSize;

        if(type == EditorTransform.Transform_RotateX)
		{
    		ySize = zSize2;
    		zSize = ySize2;
		}
    	else if(type == EditorTransform.Transform_RotateY)
		{
    		xSize = zSize2;
    		zSize = xSize2;
		}
    	else if(type == EditorTransform.Transform_RotateZ)
		{
    		xSize = ySize2;
    		ySize = xSize2;
		}

        final int xSizeF = xSize;
        final int ySizeF = ySize;
        final int zSizeF = zSize;
        AABBInt box2 = new AABBInt(xSize2, ySize2, zSize2);
        box2.repeat((i, j, k, count)->{
        	BlockSet set = this.clipboard.getBlocks().get(count);
        	IBlockState state = set.toBlockState();
        	int x1 = i;
        	int y1 = j;
        	int z1 = k;
        	if(type == EditorTransform.Transform_RotateX)
    		{
        		y1 = zSize2 - k - 1;
        		z1 = j;
    		}
        	else if(type == EditorTransform.Transform_RotateY)
    		{
        		z1 = xSize2 - i - 1;
        		x1 = k;
        		state = state.withRotation(Rotation.COUNTERCLOCKWISE_90);
    		}
        	else if(type == EditorTransform.Transform_RotateZ)
    		{
        		x1 = ySize2 - j - 1;
        		y1 = i;
    		}
        	else if(type == EditorTransform.Transform_MirrorX)
    		{
    			x1 = xSize2 - i - 1;
    			state = state.withMirror(Mirror.FRONT_BACK);
    		}
        	else if(type == EditorTransform.Transform_MirrorY)
    		{
        		y1 = ySize2 - j - 1;;
    		}
        	else if(type == EditorTransform.Transform_MirrorZ)
    		{
        		z1 = zSize2 - k - 1;
        		state = state.withMirror(Mirror.LEFT_RIGHT);
    		}

        	int index2 = (x1 * ySizeF * zSizeF) + (y1 * zSizeF) + z1;
        	blocks[index2] = new BlockSet(set.block, set.block.getMetaFromState(state), set.nbt);
        });

        this.clipboard = new WorldSnapshot(Arrays.asList(blocks), new AABBInt(xSize, ySize, zSize));

        //rotateBlockはstateを順に回してくだけだから使えない
        /*NGTWorld world = new NGTWorld(this.getWorld(), this.clipboard.convertNGTO());
		for(int i = 0; i < world.blockObject.xSize; ++i)
        {
        	for(int j = 0; j < world.blockObject.ySize; ++j)
            {
        		for(int k = 0; k < world.blockObject.zSize; ++k)
                {
        			BlockSet set = world.blockObject.getBlockSet(i, j, k);
        			BlockPos pos = new BlockPos(i, j, k);
        			set.block.rotateBlock(world, pos, type.axis1);
        			if(type.axis2 != null)
        			{
        				set.block.rotateBlock(world, pos, type.axis2);
        			}
                }
            }
        }
		this.clipboard = new WorldSnapshot(world.blockObject);*/

        this.getEntity().setPos(EntityEditor.PASTE_BOX, xSize, ySize, zSize);
        this.getEntity().updateBlockList(this.clipboard.convertNGTO());
	}

	/**エディタのスロットにブロックがあればそれを優先、無ければ手に持ったアイテムを使用*/
	public BlockSet getFillItem()
	{
		Block block = this.getEntity().getSlotBlock(0);
		int meta = this.getEntity().getSlotBlockMetadata(0);
		if(block == Blocks.AIR)
		{
			ItemStack stack = this.getEntity().getPlayer().inventory.getCurrentItem();
			if(stack != null)
			{
				if(stack.getItem() == Items.WATER_BUCKET)
				{
					block = Blocks.WATER;
				}
				else if(stack.getItem() == Items.LAVA_BUCKET)
				{
					block = Blocks.LAVA;
				}
				else
				{
					block = Block.getBlockFromItem(stack.getItem());
				}

				meta = stack.getItemDamage();
			}
		}

		if(block != null)
		{
			return new BlockSet(block, meta);
		}

		return BlockSet.AIR;
	}

	public BlockSet getBlockSet(int x, int y, int z)
	{
		return BlockSet.getBlockSet(this.getWorld(), x, y, z, true);
	}

	public void setBlock(int px, int py, int pz, Block block, int metadata, boolean syncClient)
	{
		this.setBlock(px, py, pz, new BlockSet(block, metadata), syncClient);
	}

	public void setBlock(int px, int py, int pz, BlockSet blockSet, boolean syncClient)
	{
		World world = this.getWorld();
		/*
		ここからAppleLibLoggerだよ
		 */
		if (!world.isRemote) {
			AppleLogger.logBlockChange(
					this.getEntity().getPlayer(),
					new BlockPos(px, py, pz),
					blockSet.toBlockState(),
					"MCTE_SET"
			);
		}
		/*
		終わり
		 */
		int meta = blockSet.metadata;
		if(blockSet.block instanceof BlockLeaves && (meta < 4 || meta > 7))
		{
			meta = (meta & 3) + 4;//4~7:手置き, 8~11:コピペ(消える)
		}
		int flag = syncClient ? 2 : 0;//flag 1:Update, 2:Client同期, 4:再描画防止
		BlockUtil.setBlock(world, px, py, pz, blockSet.block, meta, flag);

		if(blockSet.block != Blocks.AIR)
		{
			world.checkLight(new BlockPos(px, py, pz));//明るさ更新
		}

		if(blockSet.block.hasTileEntity(blockSet.block.getStateFromMeta(meta)))
		{
			TileEntity tile = BlockUtil.getTileEntity(world, px, py, pz);
			if(tile != null)
			{
				this.setTileEntityData(tile, blockSet.nbt, px, py, pz);
			}
		}
	}

	private void setTileEntityData(TileEntity tile, NBTTagCompound nbt, int x, int y, int z)
	{
		int prevX = 0;
		int prevY = 0;
		int prevZ = 0;

		if(nbt != null)
		{
			NBTTagCompound nbt0 = (NBTTagCompound)nbt.copy();
			prevX = nbt0.getInteger("x");
			prevY = nbt0.getInteger("y");
			prevZ = nbt0.getInteger("z");
			nbt0.setInteger("x", x);
			nbt0.setInteger("y", y);
			nbt0.setInteger("z", z);
			tile.readFromNBT(nbt0);
		}

		if(tile instanceof TileEntityCustom)
		{
			((TileEntityCustom)tile).setPos(x, y, z, prevX, prevY, prevZ);
		}
		else
		{
			tile.setPos(new BlockPos(x, y, z));
		}
	}

	/**クリップボードにコピー*/
	public WorldSnapshot copy(AABBInt box, String options)
	{
		this.clipboard = new WorldSnapshot(this, box, options);
		this.getEntity().setPos(EntityEditor.PASTE_BOX, box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ);
		if(!options.contains("notSync"))
		{
			this.getEntity().updateBlockList(this.clipboard.convertNGTO());
		}
		return this.clipboard;
	}

	public void loadData(NGTObject ngto)
	{
		this.clipboard = new WorldSnapshot(ngto);
	}

	/**クリップボードの内容を貼り付け*/
	public void paste(AABBInt box, String options)
	{
		this.clipboard.setBlocks(this, box.minX, box.minY, box.minZ, options);
	}

	public void delete(AABBInt box, String options)
	{
		this.fill(box, BlockSet.AIR, options);
	}

	public void fill(AABBInt box, final BlockSet blockSet, final String options)
	{
		this.repeat(box, (box2, index, rep, x, y, z)->{
			if(options.contains(WorldSnapshot.IGNORE_WATER))
			{
				IBlockState state = Editor.this.getWorld().getBlockState(new BlockPos(x, y, z));
				if(state.getMaterial().isLiquid())
				{
					return;
				}
			}

			this.setBlock(x, y, z, blockSet, true);
		}, 1);
		this.updateBlocks(box);
	}

	/**ClientのBlockを更新*/
	public void updateBlocks(AABBInt box)
	{
		int minCX = (box.minX >> 4);
		int minCY = (box.minY >> 4);
		int minCZ = (box.minZ >> 4);
		int maxCX = (box.maxX >> 4) + 1;
		int maxCY = (box.maxY >> 4) + 1;
		int maxCZ = (box.maxZ >> 4) + 1;
		/*AABBInt chunkBox = new AABBInt(minCX, minCY, minCZ, maxCX, maxCY, maxCZ);
		this.repeat(chunkBox, (box2, index, rep, x, y, z)->{
			BlockUtil.markBlockForUpdate(this.getWorld(), x << 4, y << 4, z << 4);
		}, 1);*/

		/*this.repeat(box, (box2, index, rep, x, y, z)->{
			BlockUtil.markBlockForUpdate(this.getWorld(), x, y, z);
		}, 1);*/
		this.getWorld().markBlockRangeForRenderUpdate(minCX << 4, minCY << 4, minCZ << 4, maxCX << 4, maxCY << 4, maxCZ << 4);
	}

	public void repeat(AABBInt box, Repeatable repeater, int rep)
	{
		for(int i = 0; i < rep; ++i)
		{
			final int i2 = i;
			box.repeat((x, y, z, count)->{
				repeater.processing(box, count, i2, x, y, z);
			});
		}
	}

	/**指定範囲の状態を保存*/
	public void record(AABBInt box)
	{
		this.history.push(new WorldSnapshot(this, box, ""));
	}

	/**操作前の状態に戻す*/
	public void undo()
	{
		WorldSnapshot snapshot = this.history.pop();
		if(snapshot != null)
		{
			snapshot.restore(this);
		}
	}
}
