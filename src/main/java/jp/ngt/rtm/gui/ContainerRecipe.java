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

import jp.ngt.ngtlib.item.craft.RecipeManager;
import jp.ngt.rtm.block.tileentity.TileEntityTrainWorkBench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ContainerRecipe extends Container {
    private static final int itemListHeight = 9;

    private World worldObj;
    private TileEntityTrainWorkBench workBench;
    private GuiRTMWorkBench gui;
    public InventoryUneditable invItems;
    public InventoryUneditable invCraftSample;
    private ItemStack selectedItem;

    public ContainerRecipe(GuiRTMWorkBench par1Gui, World world, TileEntityTrainWorkBench par3) {
        this.worldObj = world;
        this.workBench = par3;
        this.gui = par1Gui;
        this.invItems = new InventoryUneditable(this, 9, itemListHeight);
        this.invCraftSample = new InventoryUneditable(this, 26);
        this.setCurrentPage(1);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public void setCurrentPage(int par1) {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.gui.pageIndex = par1;

        if (par1 == 1)//アイテム一覧
        {
            this.setItemList(0);
        } else if (par1 == 2)//レシピ
        {
            if (this.selectedItem == null) {
                this.setCurrentPage(1);
                return;
            }

            IRecipe recipe = RecipeManager.INSTANCE.getRecipe(this.selectedItem);
            if (recipe == null) {
                this.setCurrentPage(1);
                return;
            }

            ItemStack[] iarray = RecipeManager.INSTANCE.getRecipeItems(recipe);

            this.addSlotToContainer(new SlotSample(this.invCraftSample, this, 0, 138, 80));
            this.invCraftSample.setInventorySlotContents(0, recipe.getRecipeOutput());

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    int index = j + i * 5;
                    if (index >= iarray.length) {
                        break;
                    }
                    this.addSlotToContainer(new SlotSample(this.invCraftSample, this, index + 1, 8 + j * 18, 44 + i * 18));
                    this.invCraftSample.setInventorySlotContents(index + 1, iarray[index]);
                }
            }
        }
    }

    public static int getMaxScroll() {
        List<IRecipe> list = RecipeManager.INSTANCE.getRecipeList();
        int i0 = (int) Math.ceil((double) list.size() / 9.0D);//切り上げ
        return i0 - itemListHeight;
    }

    public void setItemList(int par1) {
        List<IRecipe> list = RecipeManager.INSTANCE.getRecipeList();

        for (int i = 0; i < itemListHeight; ++i) {
            for (int j = 0; j < 9; ++j) {
                int index = j + i * 9;
                int index2 = j + (i + par1) * 9;
                if (index2 >= list.size()) {
                    break;
                }
                this.addSlotToContainer(new SlotSample(this.invItems, this, index, 8 + j * 18, 8 + i * 18));
                ItemStack itemstack = list.get(index2).getRecipeOutput().copy();
                itemstack.setCount(1);
                this.invItems.setInventorySlotContents(index, itemstack);
            }
        }
    }

    //GuiTrainWorkBenchから呼ばれる
    public void onSlotClicked(Slot slot) {
        if (slot.getStack() != null) {
            this.selectedItem = slot.getStack();
            this.setCurrentPage(2);
        }
    }

	/*@Override
    protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer player)
    {
    	;//NEIで無限ループ防止
    }*/

    @SideOnly(Side.CLIENT)
    public class SlotSample extends Slot {
        private ContainerRecipe myContainer;

        public SlotSample(IInventory iinv, ContainerRecipe container, int index, int xPos, int yPos) {
            super(iinv, index, xPos, yPos);
            this.myContainer = container;
        }

        @Override
        public boolean isItemValid(ItemStack p_75214_1_) {
            return false;
        }
    }
}