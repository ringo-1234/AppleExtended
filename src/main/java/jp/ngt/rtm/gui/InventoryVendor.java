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

package jp.ngt.rtm.gui;

import jp.ngt.ngtlib.item.ItemUtil;
import jp.ngt.rtm.gui.vendor.ContainerTicketVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryVendor implements IInventory
{
	public static final byte SLOT_SIZE = 12;

	private ContainerTicketVendor container;
	private ItemStack[] stackList;

    public InventoryVendor(ContainerTicketVendor par1)
    {
    	this.container = par1;
        this.stackList = ItemUtil.getEmptyArray(SLOT_SIZE);//money_in:1, card:1, money_out:9, ticket:1
    }

    @Override
    public int getSizeInventory()
    {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int par1)
    {
        return par1 >= this.stackList.length ? null : this.stackList[par1];
    }

    @Override
    public String getName()
    {
        return "Vendor";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot(int par1)
    {
        return this.getStackInSlot(par1);
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
    	ItemStack stack = this.getStackInSlot(par1);
    	if(!stack.isEmpty())
    	{
    		if(stack.getCount() <= par2)
            {
                this.setInventorySlotContents(par1, ItemStack.EMPTY);
                return stack;
            }
            else
            {
            	stack = stack.splitStack(par2);
                if(this.getStackInSlot(par1).getCount() == 0)
                {
                	this.setInventorySlotContents(par1, ItemStack.EMPTY);
                }

                return stack;
            }
    	}
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
    	if(index < this.stackList.length)
    	{
    		this.stackList[index] = stack;
    	}
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty(){}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player){}

    @Override
    public void closeInventory(EntityPlayer player){}

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack par2)
    {
        return true;
    }

    @Override
	public ITextComponent getDisplayName()
	{
		return new TextComponentString(this.getName());
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