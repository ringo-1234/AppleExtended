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

package jp.ngt.ngtlib.item.craft;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RepairRecipe implements IRecipe
{
	private Item toolItem;
	private ItemStack materialItem;
	private ResourceLocation name;

    public RepairRecipe(Item par1, ItemStack par2)
    {
        this.toolItem = par1;
        this.materialItem = par2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(this.toolItem, 1, 1);
    }

    /**Used to check if a recipe matches current crafting inventory*/
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
    	ItemStack[] stacks = this.getToolAndMaterial(inventory);
        if(stacks[0] != null && stacks[1] != null)
        {
        	return true;
        }

        return false;
    }

    /**Returns an Item that is the result of this recipe*/
    @Override
    public ItemStack getCraftingResult(InventoryCrafting par1)
    {
    	ItemStack[] stacks = this.getToolAndMaterial(par1);
        if(stacks[0] != null && stacks[1] != null)
        {
        	ItemStack tool = stacks[0].copy();
        	tool.setItemDamage(tool.getItemDamage() - 1);
        	return tool;
        }
        return null;
    }

    /**{tool, material}*/
    private ItemStack[] getToolAndMaterial(InventoryCrafting par1)
    {
    	ItemStack[] stacks = new ItemStack[2];
        for(int i = 0; i < par1.getSizeInventory(); ++i)
        {
        	ItemStack stack = par1.getStackInSlot(i);
            if(stack != null)
            {
            	if(stack.getItem() == this.toolItem && stack.getItemDamage() > 0)
            	{
            		stacks[0] = stack;break;
            	}
            }
        }

        for(int i = 0; i < par1.getSizeInventory(); ++i)
        {
        	ItemStack stack = par1.getStackInSlot(i);
            if(stack != null)
            {
            	if(stack.getItem() == this.materialItem.getItem() && stack.getItemDamage() == this.materialItem.getItemDamage())
            	{
            		stacks[1] = stack;break;
            	}
            }
        }

        return stacks;
    }

    public ItemStack[] getToolAndMaterial(int par1)
    {
    	ItemStack[] stacks = new ItemStack[par1];
    	stacks[0] = new ItemStack(this.toolItem, 1, 2);
    	stacks[1] = this.materialItem.copy();
    	return stacks;
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            list.add(ForgeHooks.getContainerItem(itemstack));
        }
        return list;
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name)
	{
		this.name = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName()
	{
		return this.name;
	}

	@Override
	public Class<IRecipe> getRegistryType()
	{
		return IRecipe.class;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}
}