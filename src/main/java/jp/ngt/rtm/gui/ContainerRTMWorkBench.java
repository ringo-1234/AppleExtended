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

import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityTrainWorkBench;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerRTMWorkBench extends Container {
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 5, 5);
    private InventoryCrafting invBallast = new InventoryCrafting(this, 5, 1);
    private InventoryUneditable sample = new InventoryUneditable(this, 1, 1);
    private InventoryCraftResult craftResult = new InventoryCraftResult();

    private World worldObj;
    private TileEntityTrainWorkBench workBench;
    protected EntityPlayer thePlayer;
    private int lastCraftingTime;
    private boolean isCreativeMode;

    public String modelName;
    public float railHeight = 0.0625F;

    /**
     * 0:通常, 1:レール用
     */
    protected int workbenchType;

    public ContainerRTMWorkBench(InventoryPlayer inventory, World world, TileEntityTrainWorkBench par3, boolean par4) {
        this.worldObj = world;
        this.thePlayer = inventory.player;
        this.workBench = par3;
        this.workBench.readItemsFromTile(this.craftMatrix, this.invBallast);
        this.workbenchType = par3.getBlockMetadata();
        this.isCreativeMode = par4;

        this.modelName = ModelPackManager.INSTANCE.getResourceSet(RTMResource.RAIL, "1067mm_Wood").getConfig().getName();

        //完成品スロット
        this.addSlotToContainer(new SlotWorkBench(inventory.player, this.craftMatrix, this.craftResult, 0, 138, 38));

        //見本スロット
        this.addSlotToContainer(new Slot(this.sample, 0, 138, 12));

        //クラフトスロット
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 5, 8 + j * 18, 12 + i * 18));
            }
        }

        //インベントリ(手持ち)
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 164));//1Slot:18
        }

        if (this.workbenchType == 1) {
            //道床スロット
            for (int i = 0; i < 5; ++i) {
                this.addSlotToContainer(new Slot(this.invBallast, i, 8 + i * 18, 104));
            }
        } else if (this.workbenchType == 0) {
            //インベントリ
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 106 + i * 18));
                }
            }
        }

        this.onCraftMatrixChanged(this.craftMatrix);

        if (this.workbenchType == 1 && this.isCreativeMode) {
            this.setItemToSampleSlot(ItemRail.getRailItem(ItemRail.getDefaultProperty()));
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = (Slot) this.inventorySlots.get(slotId);
            if (slot != null && slot.inventory == this.sample) {
                return ItemStack.EMPTY;//サンプル表示用スロットは除外
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        if (this.workBench.isCrafting()) {
            return;
        }

        IRecipe recipe = CraftingManager.findMatchingRecipe(this.craftMatrix, this.worldObj);
        ItemStack stack = ItemStack.EMPTY;

        if (recipe != null) {
            stack = recipe.getRecipeOutput();

            if (stack.getItem() == RTMItem.itemLargeRail) {
                if (this.workbenchType == 0) {
                    stack = ItemStack.EMPTY;
                } else if (this.workbenchType == 1) {
                    ResourceStateRail prop = this.getProperty();
                    stack = (prop == null) ? ItemStack.EMPTY : ItemRail.getRailItem(prop);
                }
            }
        } else if (this.workbenchType == 1 && this.isCreativeMode) {
            ResourceStateRail prop = this.getProperty();
            stack = (prop == null) ? ItemStack.EMPTY : ItemRail.getRailItem(prop);
        }

        this.setItemToSampleSlot(stack);
    }

    private void setItemToSampleSlot(ItemStack stack) {
        this.sample.setInventorySlotContents(0, stack);
        this.detectAndSendChanges();
    }

    /**
     * Guiでクラフト開始ボタンを押すと呼ばれる
     */
    public void startCrafting() {
        this.consumeItemsInCraftMatrix();
    }

    /**
     * アイテム消費<br>
     * 本来はSlotWorkBench.onPickupFromSlot()で行う
     */
    private void consumeItemsInCraftMatrix() {
        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
            this.decrSlotItem(this.craftMatrix, i);
        }

        for (int i = 0; i < this.invBallast.getSizeInventory(); ++i) {
            this.decrSlotItem(this.invBallast, i);
        }
    }

    private void decrSlotItem(InventoryCrafting inventory, int slotNum) {
        ItemStack itemInSlot = inventory.getStackInSlot(slotNum);
        if (itemInSlot.isEmpty()) {
            return;
        }

        inventory.decrStackSize(slotNum, 1);

        if (itemInSlot.getItem().hasContainerItem(itemInSlot)) {
            ItemStack itemstack2 = itemInSlot.getItem().getContainerItem(itemInSlot);

            if (!itemstack2.isEmpty() && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this.thePlayer, itemstack2, EnumHand.MAIN_HAND));
                return;
            }

            if (!this.thePlayer.inventory.addItemStackToInventory(itemstack2)) {
                if (inventory.getStackInSlot(slotNum).isEmpty()) {
                    inventory.setInventorySlotContents(slotNum, itemstack2);
                } else {
                    this.thePlayer.dropItem(itemstack2, false);
                }
            }
        }
    }

    private ResourceStateRail getProperty() {
        ItemStack stack = this.invBallast.getStackInSlot(0);
        Block block = stack.isEmpty() ? Blocks.AIR : Block.getBlockFromItem(stack.getItem());
        int damage = stack.isEmpty() ? 0 : stack.getItemDamage();

        if (!this.thePlayer.capabilities.isCreativeMode) {
            for (int i = 0; i < this.invBallast.getSizeInventory(); ++i) {
                ItemStack stack2 = this.invBallast.getStackInSlot(i);
                if (stack2.isEmpty()) {
                    block = Blocks.AIR;
                    break;
                }

                Block block2 = Block.getBlockFromItem(stack2.getItem());
                if (block2 != block || stack2.getItemDamage() != damage) {
                    block = Blocks.AIR;
                    break;
                }
            }
        }

        String name = this.modelName;
        if (block != null && name != null) {
            ResourceStateRail state = new ResourceStateRail(RTMResource.RAIL, null);
            state.setResourceName(name);
            state.setBlock(block, damage);
            state.setHeight(this.railHeight);
            return state;
        }
        return null;
    }

    @Override
    public void addListener(IContainerListener crafting) {
        super.addListener(crafting);
        crafting.sendWindowProperty(this, 0, this.workBench.getCraftingTime());
    }

    @Override
    public void detectAndSendChanges() {
        if (this.workBench.getCraftingTime() == TileEntityTrainWorkBench.Max_CraftingTime && !this.getSampeItem().isEmpty()) {
            this.craftResult.setInventorySlotContents(0, this.getSampeItem().copy());
            if (this.workbenchType == 0 || !this.isCreativeMode) {
                this.sample.setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }

        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icrafting = this.listeners.get(i);

            if (this.lastCraftingTime != this.workBench.getCraftingTime()) {
                icrafting.sendWindowProperty(this, 0, this.workBench.getCraftingTime());
            }
        }

        this.lastCraftingTime = this.workBench.getCraftingTime();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            this.workBench.setCraftingTime(par2);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        this.workBench.writeItemsToTile(this.craftMatrix, this.invBallast);
        this.workBench.markDirty();

        if (!player.world.isRemote && !this.getResultItem().isEmpty()) {
            player.entityDropItem(this.getResultItem(), 0.5F);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.worldObj.getBlockState(this.workBench.getPos()).getBlock() != RTMBlock.trainWorkBench ? false
                : player.getDistanceSq(this.workBench.getPos()) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            //0 完成品:0, 見本:1, クラフト:2~26, プレーヤ:27~35 + 36~62
            //1 完成品:0, 見本:1, クラフト:2~26, プレーヤ:27~35, 道床:36~40
            if (slotIndex == 0) {
                int index = (this.workbenchType == 1) ? 35 : 62;
                if (!this.mergeItemStack(itemstack1, 27, index, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (this.workbenchType == 0 && slotIndex >= 36 && slotIndex < 63)//9*3
            {
                if (!this.mergeItemStack(itemstack1, 27, 35, false))//9*1
                {
                    return ItemStack.EMPTY;
                }
            } else if (this.workbenchType == 0 && slotIndex >= 27 && slotIndex < 36)//9*1
            {
                if (!this.mergeItemStack(itemstack1, 36, 62, false))//9*3
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 27, 35, false))//9*1
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
    }

    public ItemStack getSampeItem() {
        return this.sample.getStackInSlot(0);
    }

    public ItemStack getResultItem() {
        return this.craftResult.getStackInSlot(0);
    }

    public void setRailProp(String name, float h) {
        this.modelName = name;
        this.railHeight = h;
        this.onCraftMatrixChanged(this.craftMatrix);
    }
}