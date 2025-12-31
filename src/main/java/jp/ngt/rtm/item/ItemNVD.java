package jp.ngt.rtm.item;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemNVD extends ItemArmor //implements ISpecialArmor
{
	public ItemNVD()
	{
		super(ArmorMaterial.IRON, 2, EntityEquipmentSlot.HEAD);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
	    //return "rtm:textures/models/nvd_layer_" + (this.armorType == 2 ? "2" : "1") + ".png";
	    return "rtm:textures/models/nvd_layer_1.png";
	}
}