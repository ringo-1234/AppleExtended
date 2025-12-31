package jp.ngt.rtm.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SlotWorkBench extends Slot
{
	private final IInventory craftMatrix;
    private EntityPlayer thePlayer;
    private int amountCrafted;

    public SlotWorkBench(EntityPlayer player, IInventory inventory1, IInventory inventory2, int p_i1823_4_, int p_i1823_5_, int p_i1823_6_)
    {
        super(inventory2, p_i1823_4_, p_i1823_5_, p_i1823_6_);
        this.thePlayer = player;
        this.craftMatrix = inventory1;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int p_75209_1_)
    {
        if(this.getHasStack())
        {
            this.amountCrafted += Math.min(p_75209_1_, this.getStack().getCount());
        }

        return super.decrStackSize(p_75209_1_);
    }

    @Override
    protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_)
    {
        this.amountCrafted += p_75210_2_;
        this.onCrafting(p_75210_1_);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
    protected void onCrafting(ItemStack itemStack)
    {
        itemStack.onCrafting(this.thePlayer.world, this.thePlayer, this.amountCrafted);
        this.amountCrafted = 0;

        //本来なら実績解除処理入れる（更新面倒なので削除）
    }

    /**完成品取り出し*/
    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack itemStack)
    {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemStack, this.craftMatrix);
        this.onCrafting(itemStack);
        return itemStack;

        //本来はここでアイテム消費
    }
}