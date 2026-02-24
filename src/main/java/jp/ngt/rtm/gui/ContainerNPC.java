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

import jp.ngt.rtm.entity.npc.EntityNPC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ContainerNPC extends Container
{
	private static final int INV_ROW = 3;
	private static final int INV_COL = 9;
	private static final int INV_SIZE = INV_ROW * INV_COL;

	private EntityPlayer player;
	private EntityNPC npc;

	public ContainerNPC(EntityPlayer pPlayer, EntityNPC pNPC)
	{
		this.player = pPlayer;
		this.npc = pNPC;

		int y = 84 + (18 * 3) + 4;

		//Playerインベントリ上段
		for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 9; ++j)
            {
            	int index = (j + i * 9) + 9;
                this.addSlotToContainer(new Slot(pPlayer.inventory, index, 8 + j * 18, y));
            }
            y += 18;
        }

		y += 4;

		//Playerインベントリ下段
        for(int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(pPlayer.inventory, i, 8 + i * 18, y));
        }

		//NPCインベントリArmor、上:靴~下:ヘルム
        for(int i = 0; i < 4; ++i)
        {
			int index = i + INV_SIZE;
			this.addSlotToContainer(new SlotNPCArmor(pNPC.inventory, index, 8, 8 + i * 18, i));
        }

		//NPCインベントリ
		for(int i = 0; i < INV_ROW; ++i)
        {
            for(int j = 0; j < INV_COL; ++j)
            {
            	int index = j + i * INV_COL;
                this.addSlotToContainer(new Slot(pNPC.inventory, index, 8 + j * 18, 84 + i * 18));
            }
        }
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)//チェストの処理と同じ
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotItem = slot.getStack();
            itemstack = slotItem.copy();

            if (index < INV_SIZE)
            {
                if (!this.mergeItemStack(slotItem, INV_SIZE, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(slotItem, 0, INV_SIZE, false))
            {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty())
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
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(this.npc) < 64.0D;
	}

	public class SlotNPCArmor extends Slot
	{
		private final int index;

		public SlotNPCArmor(IInventory inventoryIn, int index, int xPosition, int yPosition, int armorIndex)
		{
			super(inventoryIn, index, xPosition, yPosition);
			this.index = armorIndex;
		}

		@Override
        public int getSlotStackLimit()
        {
            return 1;
        }

        @Override
        public boolean isItemValid(ItemStack item)
        {
            if(item == null) return false;
            EntityEquipmentSlot slot;
            switch(this.index)
            {
            case 0: slot = EntityEquipmentSlot.FEET;break;
            case 1: slot = EntityEquipmentSlot.LEGS;break;
            case 2: slot = EntityEquipmentSlot.CHEST;break;
            case 3: slot = EntityEquipmentSlot.HEAD;break;
            default: slot = EntityEquipmentSlot.HEAD;
            }
            return item.getItem().isValidArmor(item, slot, ContainerNPC.this.npc);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getSlotTexture()
        {
            return ItemArmor.EMPTY_SLOT_NAMES[this.index];
        }
	}
}