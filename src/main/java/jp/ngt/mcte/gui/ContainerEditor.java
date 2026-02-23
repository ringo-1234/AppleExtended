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

package jp.ngt.mcte.gui;

import jp.ngt.mcte.editor.EntityEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEditor extends Container
{
	private final EntityEditor editor;

	public ContainerEditor(EntityEditor par1)
	{
		this.editor = par1;
		this.editor.openInventory(null);

		this.addSlotToContainer(new Slot(this.editor, 0, 72, 152));
		this.addSlotToContainer(new Slot(this.editor, 1, 72, 172));

		for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(this.editor.getPlayer().inventory, i, 8 + i * 20, 142));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(this.editor) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par2)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(par2);
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.editor.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.editor.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.editor.getSizeInventory(), false))
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
        return itemstack;
    }

	@Override
	public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if(!player.world.isRemote)
        {
        	for(int i = 0; i < this.editor.getSizeInventory(); ++i)
        	{
        		ItemStack itemstack = this.editor.removeStackFromSlot(i);
        		if(!itemstack.isEmpty())
                {
        			player.dropItem(itemstack, false);
                }
        	}
            this.editor.setCloneBox(0, 0, 0, 0);//GUI閉じる際クローンboxの設定は保持しない
        }
    }
}