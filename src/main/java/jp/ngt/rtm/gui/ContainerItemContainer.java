package jp.ngt.rtm.gui;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerItemContainer extends Container
{
	private final IInventory inventory;
	private int numRows;
	private int field_94535_f = -1;
	private int dragEvent;
	private final Set<Slot> slots = new HashSet();
	private final int maxContainerSlotNumber;

	public ContainerItemContainer(EntityPlayer player, IInventory invMain)
	{
		this.inventory = invMain;
		invMain.openInventory(player);
		this.numRows = this.inventory.getSizeInventory() / 9;
		int i = (this.numRows - 4) * 18;

        for(int j = 0; j < this.numRows; ++j)
        {
            for(int k = 0; k < 9; ++k)
            {
            	this.addSlotToContainer(new Slot(invMain, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int j = 0; j < 3; ++j)
        {
            for(int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for(int j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 161 + i));
        }

        this.maxContainerSlotNumber = this.inventory.getSizeInventory() - 1;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return this.inventory.isUsableByPlayer(var1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par2)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(par2);
        if(slot != null)
        {
        	if(slot.getHasStack())
        	{
        		ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();
                if(par2 < this.inventory.getSizeInventory())
                {
                    if(!this.mergeItemStack(itemstack1, this.inventory.getSizeInventory(), this.inventorySlots.size(), true))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(!this.mergeItemStack(itemstack1, 0, this.inventory.getSizeInventory(), false))
                {
                    return ItemStack.EMPTY;
                }

                if (itemstack1.getCount() == 0)
                {
                    slot.putStack(ItemStack.EMPTY);
                }
                else
                {
                    slot.onSlotChanged();
                }
        	}
        	else
        	{
        		;
        	}
        }
        return itemstack;
    }

	@Override
	public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        this.inventory.closeInventory(player);
    }

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        InventoryPlayer invPlayer = player.inventory;
        ItemStack itemstack3;

        if(clickTypeIn == ClickType.QUICK_CRAFT)
        {
            int l = this.dragEvent;
            this.dragEvent = getDragEvent(dragType);

            if ((l != 1 || this.dragEvent != 2) && l != this.dragEvent)
            {
                this.resetDrag();
            }
            else if (invPlayer.getItemStack() == ItemStack.EMPTY)
            {
                this.resetDrag();
            }
            else if (this.dragEvent == 0)
            {
                this.field_94535_f = extractDragMode(dragType);

                if (isValidDragMode(this.field_94535_f, player))
                {
                    this.dragEvent = 1;
                    this.slots.clear();
                }
                else
                {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1)
            {
                Slot slot = (Slot)this.inventorySlots.get(slotId);

                if (slot != null && func_94527_a(slot, invPlayer.getItemStack(), true) && slot.isItemValid(invPlayer.getItemStack()) && invPlayer.getItemStack().getCount() > this.slots.size() && this.canDragIntoSlot(slot))
                {
                    this.slots.add(slot);
                }
            }
            else if (this.dragEvent == 2)
            {
                if (!this.slots.isEmpty())
                {
                    itemstack3 = invPlayer.getItemStack().copy();
                    int i1 = invPlayer.getItemStack().getCount();
                    for(Slot slot1 : this.slots)
                    {
                    	if (slot1 != null && func_94527_a(slot1, invPlayer.getItemStack(), true) && slot1.isItemValid(invPlayer.getItemStack()) && invPlayer.getItemStack().getCount() >= this.slots.size() && this.canDragIntoSlot(slot1))
                        {
                            ItemStack itemstack1 = itemstack3.copy();
                            int j1 = slot1.getHasStack() ? slot1.getStack().getCount() : 0;
                            computeStackSize(this.slots, this.field_94535_f, itemstack1, j1);

                            if (itemstack1.getCount() > itemstack1.getMaxStackSize())
                            {
                            	itemstack1.setCount(itemstack1.getMaxStackSize());
                            }

                            if (itemstack1.getCount() > slot1.getSlotStackLimit())
                            {
                            	itemstack1.setCount(slot1.getSlotStackLimit());
                            }

                            i1 -= itemstack1.getCount() - j1;
                            slot1.putStack(itemstack1);
                        }
                    }

                    itemstack3.setCount(i1);

                    if (itemstack3.getCount() <= 0)
                    {
                        itemstack3 = ItemStack.EMPTY;
                    }

                    invPlayer.setItemStack(itemstack3);
                }

                this.resetDrag();
            }
            else
            {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0)
        {
            this.resetDrag();
        }
        else
        {
            Slot slot2;
            int l1;
            ItemStack itemstack5;

            if((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1))
            {
                if (slotId == -999)
                {
                    if (invPlayer.getItemStack() != ItemStack.EMPTY && slotId == -999)
                    {
                        if (dragType == 0)
                        {
                            player.dropItem(invPlayer.getItemStack(), true);
                            invPlayer.setItemStack(ItemStack.EMPTY);
                        }

                        if (dragType == 1)
                        {
                            player.dropItem(invPlayer.getItemStack().splitStack(1), true);

                            if (invPlayer.getItemStack().getCount() == 0)
                            {
                                invPlayer.setItemStack(ItemStack.EMPTY);
                            }
                        }
                    }
                }
                else if(clickTypeIn == ClickType.QUICK_MOVE)
                {
                    if(slotId < 0)
                    {
                        return ItemStack.EMPTY;
                    }

                    slot2 = this.inventorySlots.get(slotId);

                    if(slot2 != null && slot2.canTakeStack(player))
                    {
                        itemstack3 = this.transferStackInSlot(player, slotId);

                        if(itemstack3 != ItemStack.EMPTY)
                        {
                            Item item = itemstack3.getItem();
                            itemstack = itemstack3.copy();

                            if(slot2.getStack() != ItemStack.EMPTY && slot2.getStack().getItem() == item)
                            {
                            	this.slotClick(slotId, dragType, ClickType.SWAP, player);//Type合ってる？
                                //this.retrySlotClick(slotId, dragType, true, player);
                            }
                        }
                    }
                }
                else
                {
                    if (slotId < 0)
                    {
                        return ItemStack.EMPTY;
                    }

                    slot2 = this.inventorySlots.get(slotId);

                    if (slot2 != null)
                    {
                        itemstack3 = slot2.getStack();
                        ItemStack itemstack4 = invPlayer.getItemStack();

                        if (itemstack3 != ItemStack.EMPTY)
                        {
                            itemstack = itemstack3.copy();
                        }

                        if (itemstack3 == ItemStack.EMPTY)
                        {
                            if (itemstack4 != ItemStack.EMPTY && slot2.isItemValid(itemstack4))
                            {
                                l1 = dragType == 0 ? itemstack4.getCount() : 1;

                                if (l1 > slot2.getSlotStackLimit())
                                {
                                    l1 = slot2.getSlotStackLimit();
                                }

                                if (itemstack4.getCount() >= l1)
                                {
                                    slot2.putStack(itemstack4.splitStack(l1));
                                }

                                if (itemstack4.getCount() == 0)
                                {
                                    invPlayer.setItemStack(ItemStack.EMPTY);
                                }
                            }
                        }
                        else if (slot2.canTakeStack(player))
                        {
                            if (itemstack4 == ItemStack.EMPTY)
                            {
                                l1 = dragType == 0 ? itemstack3.getCount() : (itemstack3.getCount() + 1) / 2;
                                itemstack5 = slot2.decrStackSize(l1);
                                invPlayer.setItemStack(itemstack5);

                                if (itemstack3.getCount() == 0)
                                {
                                    slot2.putStack(ItemStack.EMPTY);
                                }

                                slot2.onTake(player, invPlayer.getItemStack());
                            }
                            else if (slot2.isItemValid(itemstack4))
                            {
                            	//スロット内と保持してるアイテムを合体
                                if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getItemDamage() == itemstack4.getItemDamage() && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4))
                                {
                                    l1 = dragType == 0 ? itemstack4.getCount() : 1;

                                    if (l1 > slot2.getSlotStackLimit() - itemstack3.getCount())
                                    {
                                        l1 = slot2.getSlotStackLimit() - itemstack3.getCount();
                                    }

                                    if(slot2.slotNumber > this.maxContainerSlotNumber)
                                    {
                                    	if (l1 > itemstack4.getMaxStackSize() - itemstack3.getCount())
                                        {
                                            l1 = itemstack4.getMaxStackSize() - itemstack3.getCount();
                                        }
                                    }

                                    itemstack4.splitStack(l1);

                                    if (itemstack4.getCount() == 0)
                                    {
                                        invPlayer.setItemStack(ItemStack.EMPTY);
                                    }

                                    itemstack3.grow(l1);
                                }
                                else if (itemstack4.getCount() <= slot2.getSlotStackLimit())
                                {
                                    slot2.putStack(itemstack4);
                                    invPlayer.setItemStack(itemstack3);
                                }
                            }
                            else if (itemstack3.getItem() == itemstack4.getItem() && itemstack4.getMaxStackSize() > 1 && (!itemstack3.getHasSubtypes() || itemstack3.getItemDamage() == itemstack4.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4))
                            {
                                l1 = itemstack3.getCount();

                                if (l1 > 0 && l1 + itemstack4.getCount() <= itemstack4.getMaxStackSize())
                                {
                                	itemstack4.grow(l1);
                                    itemstack3 = slot2.decrStackSize(l1);

                                    if (itemstack3.getCount() == 0)
                                    {
                                        slot2.putStack(ItemStack.EMPTY);
                                    }

                                    slot2.onTake(player, invPlayer.getItemStack());
                                }
                            }
                        }

                        slot2.onSlotChanged();
                    }
                }
            }
            else if(clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
            {
                slot2 = this.inventorySlots.get(slotId);

                if (slot2.canTakeStack(player))
                {
                    itemstack3 = invPlayer.getStackInSlot(dragType);
                    boolean flag = itemstack3 == ItemStack.EMPTY || slot2.inventory == invPlayer && slot2.isItemValid(itemstack3);
                    l1 = -1;

                    if (!flag)
                    {
                        l1 = invPlayer.getFirstEmptyStack();
                        flag |= l1 > -1;
                    }

                    if (slot2.getHasStack() && flag)
                    {
                        itemstack5 = slot2.getStack();
                        invPlayer.setInventorySlotContents(dragType, itemstack5.copy());

                        if ((slot2.inventory != invPlayer || !slot2.isItemValid(itemstack3)) && itemstack3 != ItemStack.EMPTY)
                        {
                            if (l1 > -1)
                            {
                                invPlayer.addItemStackToInventory(itemstack3);
                                slot2.decrStackSize(itemstack5.getCount());
                                slot2.putStack(ItemStack.EMPTY);
                                slot2.onTake(player, itemstack5);
                            }
                        }
                        else
                        {
                            slot2.decrStackSize(itemstack5.getCount());
                            slot2.putStack(itemstack3);
                            slot2.onTake(player, itemstack5);
                        }
                    }
                    else if (!slot2.getHasStack() && itemstack3 != ItemStack.EMPTY && slot2.isItemValid(itemstack3))
                    {
                        invPlayer.setInventorySlotContents(dragType, (ItemStack)null);
                        slot2.putStack(itemstack3);
                    }
                }
            }
            else if(clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && invPlayer.getItemStack() == ItemStack.EMPTY && slotId >= 0)
            {
                slot2 = this.inventorySlots.get(slotId);

                if (slot2 != null && slot2.getHasStack())
                {
                    itemstack3 = slot2.getStack().copy();
                    itemstack3.setCount(itemstack3.getMaxStackSize());
                    invPlayer.setItemStack(itemstack3);
                }
            }
            else if(clickTypeIn == ClickType.THROW && invPlayer.getItemStack().isEmpty() && slotId >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotId);

                if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player))
                {
                    itemstack3 = slot2.decrStackSize(dragType == 0 ? 1 : slot2.getStack().getCount());
                    slot2.onTake(player, itemstack3);
                    player.dropItem(itemstack3, true);
                }
            }
            else if(clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0)
            {
                slot2 = this.inventorySlots.get(slotId);
                itemstack3 = invPlayer.getItemStack();

                if (!itemstack3.isEmpty() && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(player)))
                {
                    int i1 = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                    l1 = dragType == 0 ? 1 : -1;

                    for (int i2 = 0; i2 < 2; ++i2)
                    {
                        for (int j2 = i1; j2 >= 0 && j2 < this.inventorySlots.size() && itemstack3.getCount() < itemstack3.getMaxStackSize(); j2 += l1)
                        {
                            Slot slot3 = (Slot)this.inventorySlots.get(j2);

                            if (slot3.getHasStack() && func_94527_a(slot3, itemstack3, true) && slot3.canTakeStack(player) && this.canMergeSlot(itemstack3, slot3) && (i2 != 0 || slot3.getStack().getCount() != slot3.getStack().getMaxStackSize()))
                            {
                                int k1 = Math.min(itemstack3.getMaxStackSize() - itemstack3.getCount(), slot3.getStack().getCount());
                                ItemStack itemstack2 = slot3.decrStackSize(k1);
                                itemstack3.grow(k1);

                                if (itemstack2.getCount() <= 0)
                                {
                                    slot3.putStack(ItemStack.EMPTY);
                                }

                                slot3.onTake(player, itemstack2);
                            }
                        }
                    }
                }

                this.detectAndSendChanges();
            }
        }

        return itemstack;
    }

	public static boolean func_94527_a(Slot slot, ItemStack itemStack, boolean par2)
    {
        boolean flag1 = slot == null || !slot.getHasStack();

        if (slot != null && slot.getHasStack() && itemStack != null && itemStack.isItemEqual(slot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), itemStack))
        {
            int i = par2 ? 0 : itemStack.getCount();
            if(slot.slotNumber <= 53)
            {
            	flag1 |= slot.getStack().getCount() + i <= slot.getSlotStackLimit();
            }
            else
            {
            	flag1 |= slot.getStack().getCount() + i <= itemStack.getMaxStackSize();
            }
        }

        return flag1;
    }

	@Override
	protected boolean mergeItemStack(ItemStack itemStack, int par2, int par3, boolean par4)
    {
        boolean flag1 = false;
        int k = par2;

        if (par4)
        {
            k = par3 - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (itemStack.isStackable())
        {
            while (itemStack.getCount() > 0 && (!par4 && k < par3 || par4 && k >= par2))
            {
                slot = this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (!itemstack1.isEmpty() && itemstack1.getItem() == itemStack.getItem() && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, itemstack1))
                {
                    int l = itemstack1.getCount() + itemStack.getCount();
                    //コンテナのスロットか他のスロットか
                    int maxSize = slot.slotNumber <= this.maxContainerSlotNumber ? slot.getSlotStackLimit() : itemStack.getMaxStackSize();

                    if (l <= maxSize)
                    {
                    	itemStack.setCount(0);
                    	itemstack1.setCount(l);
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.getCount() < maxSize)
                    {
                    	itemStack.shrink(maxSize - itemstack1.getCount());
                    	itemstack1.setCount(maxSize);
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (par4)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        if (itemStack.getCount() > 0)
        {
            if (par4)
            {
                k = par3 - 1;
            }
            else
            {
                k = par2;
            }

            while (!par4 && k < par3 || par4 && k >= par2)
            {
                slot = this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 == null)
                {
                    slot.putStack(itemStack.copy());
                    slot.onSlotChanged();
                    itemStack.setCount(0);
                    flag1 = true;
                    break;
                }

                if (par4)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        return flag1;
    }
}