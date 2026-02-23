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

package jp.ngt.rtm.entity.npc;

import jp.ngt.ngtlib.item.ItemUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

public final class InventoryNPC implements IInventory
{
	private final EntityNPC npc;
	/**Size=9x3*/
	public ItemStack[] mainInventory = ItemUtil.getEmptyArray(9 * 3);
	/**Size=4*/
    public ItemStack[] armorInventory = ItemUtil.getEmptyArray(4);
    public boolean isOpening;

    public InventoryNPC(EntityNPC par1)
    {
    	this.npc = par1;
    }

    public NBTTagList writeToNBT(NBTTagList nbtList)
    {
        for(int i = 0; i < this.mainInventory.length; ++i)
        {
            if(!this.mainInventory[i].isEmpty())
            {
            	NBTTagCompound nbt = new NBTTagCompound();
                nbt.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbt);
                nbtList.appendTag(nbt);
            }
        }

        for(int i = 0; i < this.armorInventory.length; ++i)
        {
            if(!this.armorInventory[i].isEmpty())
            {
            	NBTTagCompound nbt = new NBTTagCompound();
                nbt.setByte("Slot", (byte)(i + 100));
                this.armorInventory[i].writeToNBT(nbt);
                nbtList.appendTag(nbt);
            }
        }

        return nbtList;
    }

    public void readFromNBT(NBTTagList nbtList)
    {
        for(int i = 0; i < nbtList.tagCount(); ++i)
        {
            NBTTagCompound nbt = nbtList.getCompoundTagAt(i);
            int j = nbt.getByte("Slot") & 255;
            ItemStack itemstack = new ItemStack(nbt);

            if(!itemstack.isEmpty())
            {
                if(j >= 0 && j < this.mainInventory.length)
                {
                    this.mainInventory[j] = itemstack;
                }

                if(j >= 100 && j < this.armorInventory.length + 100)
                {
                    this.armorInventory[j - 100] = itemstack;
                }
            }
        }
    }

	@Override
	public int getSizeInventory()
	{
		return this.mainInventory.length + this.armorInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		if(index < this.mainInventory.length)
		{
			return this.mainInventory[index];
		}

		index -= this.mainInventory.length;

		if(index < this.armorInventory.length)
		{
			return this.armorInventory[index];
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int size)
	{
		ItemStack[] targetInv = this.mainInventory;
        if(index >= this.mainInventory.length)
        {
            targetInv = this.armorInventory;
            index -= this.mainInventory.length;
            if(index >= this.armorInventory.length)
            {
            	return ItemStack.EMPTY;
            }
        }

        if(!targetInv[index].isEmpty())
        {
            ItemStack itemstack = ItemStack.EMPTY;

            if (targetInv[index].getCount() <= size)
            {
                itemstack = targetInv[index];
                targetInv[index] = ItemStack.EMPTY;
                return itemstack;
            }
            else
            {
                itemstack = targetInv[index].splitStack(size);
                if (targetInv[index].getCount() == 0)
                {
                    targetInv[index] = ItemStack.EMPTY;
                }
                return itemstack;
            }
        }
        else
        {
            return ItemStack.EMPTY;
        }
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack[] targetInv = this.mainInventory;
        if(index >= this.mainInventory.length)
        {
            targetInv = this.armorInventory;
            index -= this.mainInventory.length;
            if(index >= this.armorInventory.length)
            {
            	return ItemStack.EMPTY;
            }
        }

        if (!targetInv[index].isEmpty())
        {
            ItemStack itemstack = targetInv[index];
            targetInv[index] = ItemStack.EMPTY;
            return itemstack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
	}
	@Override
	public void setInventorySlotContents(int index, ItemStack item)
	{
		ItemStack[] targetInv = this.mainInventory;
        if(index >= this.mainInventory.length)
        {
            targetInv = this.armorInventory;
            index -= this.mainInventory.length;
            if(index >= this.armorInventory.length)
            {
            	return;
            }
        }
        targetInv[index] = item;
	}

	@Override
	public String getName()
	{
		return "inventoryNPC";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
		this.npc.onInventoryChanged();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		this.isOpening = true;
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		this.isOpening = false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack item)
	{
		return index < this.getSizeInventory();
	}

	public void dropAllItems()
    {
        for(int i = 0; i < this.mainInventory.length; ++i)
        {
            if(!this.mainInventory[i].isEmpty())
            {
            	this.npc.entityDropItem(this.mainInventory[i], 0.5F);
                this.mainInventory[i] = ItemStack.EMPTY;
            }
        }

        for(int i = 0; i < this.armorInventory.length; ++i)
        {
            if(!this.armorInventory[i].isEmpty())
            {
            	this.npc.entityDropItem(this.armorInventory[i], 0.5F);
                this.armorInventory[i] = ItemStack.EMPTY;
            }
        }
    }

	public int getTotalArmorValue()
    {
		int damageReduce = 0;
        for(int j = 0; j < this.armorInventory.length; ++j)
        {
            if(this.armorInventory[j].getItem() instanceof ItemArmor)
            {
                damageReduce += ((ItemArmor)this.armorInventory[j].getItem()).damageReduceAmount;
            }
        }
        return damageReduce;
    }

	public void damageArmor(EntityLivingBase entity, float damage)
    {
        damage /= 4.0F;
        if(damage < 1.0F)
        {
            damage = 1.0F;
        }

        for(int i = 0; i < this.armorInventory.length; ++i)
        {
            if(this.armorInventory[i].getItem() instanceof ItemArmor)
            {
                this.armorInventory[i].damageItem((int)damage, entity);
                if(this.armorInventory[i].getCount() == 0)
                {
                    this.armorInventory[i] = ItemStack.EMPTY;
                }
            }
        }
    }

	public int hasItem(Class<? extends Item> clazz)
	{
		for(int i = 0; i < this.getSizeInventory(); ++i)
		{
			ItemStack stack = this.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem().getClass() == clazz)
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int getField(int id) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public int getFieldCount() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void clear() {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public boolean isEmpty() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}