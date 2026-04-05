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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryUneditable implements IInventory {
    private ItemStack[] stackList;
    private int inventoryWidth;

    public InventoryUneditable(Container container, int size) {
        this.stackList = ItemUtil.getEmptyArray(size);
        this.inventoryWidth = 1;
    }

    public InventoryUneditable(Container container, int width, int height) {
        int k = width * height;
        this.stackList = ItemUtil.getEmptyArray(k);
        this.inventoryWidth = width;
    }

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int par1) {
        return par1 >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList[par1];
    }

    @Override
    public String getName() {
        return "container.uneditable";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int size) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < this.stackList.length) {
            this.stackList[index] = stack;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
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