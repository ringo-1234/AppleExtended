package jp.ngt.ngtlib.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMultiIcon extends ItemCustom
{
	private SerializableItemType[] types;

	public ItemMultiIcon(SerializableItemType[] par1)
	{
		super();
		this.setHasSubtypes(true);
		this.types = par1;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1)
    {
        return super.getUnlocalizedName() + "." + par1.getItemDamage();
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
		if(!this.isInCreativeTab(tab)){return;}

		for(SerializableItemType type : types)
		{
			list.add(new ItemStack(this, 1, type.getId()));
		}
    }
}