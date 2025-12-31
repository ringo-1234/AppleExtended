package jp.ngt.mcte.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.block.MiniatureBlockState;
import jp.ngt.mcte.item.ItemEditor;
import jp.ngt.mcte.item.ItemMiniature;
import jp.ngt.mcte.item.ItemMiniature.MiniatureMode;
import jp.ngt.mcte.network.PacketEditorBlockData;
import jp.ngt.mcte.network.PacketRenderBlocks;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.item.ItemUtil;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityEditor extends Entity implements IInventory
{
	private static final byte XZ_MASK_BIT = 9;
	private static final int XZ_OFFSET = 1 << (XZ_MASK_BIT - 1);
	//BlockPos
	//X,Z_BITS=28bit(<-30000000=0x01C9C380)
	//Y_BITS=64-28*2=8(->0xFF=255)
	public static final DataParameter<String>    PLAYER =    EntityDataManager.<String>createKey(EntityEditor.class, DataSerializers.STRING);
	public static final DataParameter<BlockPos>  START_POS = EntityDataManager.<BlockPos>createKey(EntityEditor.class, DataSerializers.BLOCK_POS);
	public static final DataParameter<BlockPos>  END_POS =   EntityDataManager.<BlockPos>createKey(EntityEditor.class, DataSerializers.BLOCK_POS);
	public static final DataParameter<BlockPos>  PASTE_BOX = EntityDataManager.<BlockPos>createKey(EntityEditor.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> CLONE_BOX = EntityDataManager.<BlockPos>createKey(EntityEditor.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Byte>     MODE =      EntityDataManager.<Byte>createKey(EntityEditor.class, DataSerializers.BYTE);

	private EntityPlayer player;
	private ItemStack[] slots = ItemUtil.getEmptyArray(2);
	public int fillMode = 0;

	private List<EditEntry> entries = Collections.synchronizedList(new ArrayList());

	@SideOnly(Side.CLIENT)
	public NGTObject blocksForRenderer;
	@SideOnly(Side.CLIENT)
	public World dummyWorld;
	@SideOnly(Side.CLIENT)
	public GLObject displayList;
	@SideOnly(Side.CLIENT)
	private boolean needsUpdate;

	public EntityEditor(World world)
	{
		super(world);
		this.ignoreFrustumCheck = true;
	}

	protected EntityEditor(World world, EntityPlayer player, int x, int y, int z)
	{
		this(world);

		this.setPlayer(player);
		this.setPos(START_POS, x, y, z);
	}

	@Override
	protected void entityInit()//max:31
	{
		this.getDataManager().register(PLAYER, "");
		this.getDataManager().register(START_POS, BlockPos.ORIGIN);
		this.getDataManager().register(END_POS,   BlockPos.ORIGIN);
		this.getDataManager().register(PASTE_BOX, BlockPos.ORIGIN);
		this.getDataManager().register(CLONE_BOX, new BlockPos(XZ_OFFSET, 0, XZ_OFFSET));
		this.getDataManager().register(MODE, Byte.valueOf((byte)0));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(this.getPlayer() != null)
		{
			this.posX = this.getPlayer().posX;
			this.posY = this.getPlayer().posY;
			this.posZ = this.getPlayer().posZ;
			this.setPosition(this.posX, this.posY, this.posZ);
		}

		if(!this.world.isRemote)
		{
			if(this.getPlayer() == null || this.getPlayer().isDead)
			{
				this.setDead();
			}

			if(!this.entries.isEmpty())
			{
				for(EditEntry entry : this.entries)
				{
					entry.edit();
				}
				this.entries.clear();
			}
		}
	}

	@Override
	public void setDead()
    {
		super.setDead();

		if(this.world.isRemote)
		{
			GLHelper.deleteGLList(this.displayList);
		}
		else
		{
			EditorManager.INSTANCE.remove(this);
		}
    }

	//パケット受信時にeditor.editBlocks()するとConcurrentModificationException発生する場合があるので
	//プールしてonUpdate()で処理
	public void post(EditEntry entry)
	{
		this.entries.add(entry);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double par1, double par3, double par5, float par7, float par8, int par9, boolean par10){}

	/**@param selectSide Blockそのものではなく、その側面(=空気ブロック)を選択するか*/
	public RayTraceResult getTarget(boolean selectSide)
	{
		EntityPlayer player = this.getPlayer();
		if(player != null)
		{
			byte mode = this.getEditMode();
			boolean flag = (mode == Editor.EditMode_0 || mode == Editor.EditMode_VisibleBox_0);
			return ItemEditor.getTarget(player, flag, selectSide);
		}
		return null;
	}

	public EntityPlayer getPlayer()
    {
		if(this.player == null)
		{
			String name = this.getDataManager().get(PLAYER);
			this.player = this.world.getPlayerEntityByName(name);
		}
		return this.player;
    }

	public void setPlayer(EntityPlayer par1)
    {
		this.player = par1;
		this.getDataManager().set(PLAYER, par1.getName());
    }

	public int[] getPos(DataParameter<BlockPos> type)
	{
		return BlockUtil.toArray(this.getDataManager().get(type));
	}

	public void setPos(DataParameter<BlockPos> type, int x, int y, int z)
	{
		if(type == START_POS)
		{
			//終点を初期化
			this.getDataManager().set(END_POS, new BlockPos(0, 0, 0));
		}
		this.getDataManager().set(type, new BlockPos(x, y, z));
	}

	public boolean isSelectEnd()
	{
		int[] start = this.getPos(START_POS);
		int[] end = this.getPos(END_POS);
		return start[1] > 0 && end[1] > 0;//両方のYが1以上
	}

	/**@return {x, y, z, repeat} 相対座標*/
	public int[] getCloneBox()
	{
		int[] ia = this.getPos(CLONE_BOX);
		int mask = (1 << XZ_MASK_BIT) - 1;
		int x = (ia[0] & mask) - XZ_OFFSET;
		int z = (ia[2] & mask) - XZ_OFFSET;
		int r = (ia[0] >> XZ_MASK_BIT) + ((ia[2] >> XZ_MASK_BIT) << 4);
		return new int[]{x, ia[1], z, r};
	}

	public void setCloneBox(int x, int y, int z, int r)
	{
		//BlockPos内で座標がマスクされるので、Yに大きい値は設定できない
		r = MathHelper.clamp(r, 0, 255);
		//x:9bit + r:下4bit
		x = MathHelper.clamp(x, -256, 255) + XZ_OFFSET + ((r & 0xF) << XZ_MASK_BIT);
		//z:9bit + r:上4bit
		z = MathHelper.clamp(z, -256, 255) + XZ_OFFSET + ((r >> 4) << XZ_MASK_BIT);
		this.setPos(CLONE_BOX, x, y, z);
	}

	public boolean hasCloneBox()
	{
		int[] ia = this.getCloneBox();
		return ia[3] > 0;//repが1以上
	}

	public byte getEditMode()
	{
		return this.getDataManager().get(MODE);
	}

	public void setEditMode(byte par1)
	{
		this.getDataManager().set(MODE, Byte.valueOf(par1));
	}

	/**@param index 0 or 1*/
	public Block getSlotBlock(int index)
	{
		ItemStack stack = this.slots[index];
		if(stack == null)
		{
			return Blocks.AIR;
		}
		else
		{
			return Block.getBlockFromItem(stack.getItem());
		}
	}

	/**@param index 0 or 1*/
	public int getSlotBlockMetadata(int index)
	{
		ItemStack stack = this.slots[index];
		return (stack == null) ? 0 : stack.getItemDamage();
	}

	public void dropMiniature(NGTObject par1, float par2)
	{
		ItemStack stack = ItemMiniature.createMiniatureItem(par1, par2, 0.0F, 0.0F, 0.0F, MiniatureMode.miniature, MiniatureBlockState.create((ItemStack)null));
		this.entityDropItem(stack, 1.0F);
	}

	/**Clientのブロックリストの更新*/
	public void updateBlockList(NGTObject ngto)
	{
		MCTE.NETWORK_WRAPPER.sendToAll(new PacketRenderBlocks(this, ngto));
	}

	/**ブロックとの当たり判定*/
	@Override
	protected void doBlockCollisions(){}

	@SideOnly(Side.CLIENT)
	public void setUpdate(boolean par1)
	{
		this.needsUpdate = par1;
		if(par1)
		{
			this.dummyWorld = new NGTWorld(NGTUtil.getClientWorld(), this.blocksForRenderer);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldUpdate()
	{
		return this.needsUpdate;
	}

	@Override
	public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

	//IInventory********************************************************************/

	@Override
	public int getSizeInventory()
	{
		return this.slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.slots[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if(!this.slots[par1].isEmpty())
        {
            ItemStack itemstack;
            if(this.slots[par1].getCount() <= par2)
            {
                itemstack = this.slots[par1];
                this.slots[par1] = ItemStack.EMPTY;
                return itemstack;
            }
            else
            {
                itemstack = this.slots[par1].splitStack(par2);
                if(this.slots[par1].getCount() == 0)
                {
                    this.slots[par1] = ItemStack.EMPTY;
                }
                return itemstack;
            }
        }
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int par1)
	{
		if(!this.slots[par1].isEmpty())
        {
            ItemStack itemstack = this.slots[par1];
            this.slots[par1] = ItemStack.EMPTY;
            return itemstack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2)
	{
		this.slots[par1] = par2;
        if(!par2.isEmpty() && par2.getCount() > this.getInventoryStackLimit())
        {
        	par2.setCount(this.getInventoryStackLimit());
        }
	}

	@Override
	public String getName()
	{
		return "Inventory_Editor";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void markDirty(){}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int id, ItemStack stack)
	{
		return true;
	}

	@Override
	public int getField(int id)
	{
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public int getFieldCount()
	{
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void clear()
	{
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : this.slots)
		{
			if(!stack.isEmpty() && stack.getCount() > 0)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	//IO************************************************************************/

	@SideOnly(Side.CLIENT)
	public void importBlocks(File file, float scale)
	{
		NGTObject obj = NGTObject.importFromFile(file, scale);
		if(obj != null)
		{
			MCTE.NETWORK_WRAPPER.sendToServer(new PacketEditorBlockData(this, obj.writeToNBT()));
		}
	}

	/**Server Only*/
	public void importBlocksFromNBT(NBTTagCompound nbt)
	{
		NGTObject ngto = NGTObject.readFromNBT(nbt);
		Editor editor = EditorManager.INSTANCE.getEditor(this.getPlayer());
		if(ngto != null && editor != null)
		{
			editor.loadData(ngto);
			this.setPos(PASTE_BOX, ngto.xSize, ngto.ySize, ngto.zSize);
			this.updateBlockList(ngto);
		}
	}
}