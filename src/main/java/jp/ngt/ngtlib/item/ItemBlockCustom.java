package jp.ngt.ngtlib.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**ブロックをダメージ別名付けする*/
public class ItemBlockCustom extends ItemBlock
{
	public ItemBlockCustom(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
    {
        return damage;
    }

	@Override
	public String getUnlocalizedName(ItemStack par1)
	{
		return super.getUnlocalizedName() + "." + par1.getItemDamage();
	}
}