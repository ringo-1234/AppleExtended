package jp.ngt.rtm.item;

import java.util.List;

import javax.annotation.Nullable;

import jp.ngt.rtm.item.ItemGun.GunType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMagazine extends Item
{
	public final GunType magazineType;

	public ItemMagazine(GunType par1)
	{
		super();
		this.magazineType = par1;
		this.maxStackSize = 1;
		this.setMaxDamage(par1.maxSize);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
		int max = stack.getMaxDamage();
		tooltip.add(TextFormatting.GRAY + "Bullet:" + String.valueOf(max - stack.getItemDamage()) + "/" + String.valueOf(max));
    }
}