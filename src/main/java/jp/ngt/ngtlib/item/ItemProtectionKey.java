package jp.ngt.ngtlib.item;

import java.util.List;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.protection.ProtectionManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemProtectionKey extends ItemCustom
{
	public ItemProtectionKey()
	{
		this.setMaxStackSize(1);
	}

	public static ItemStack getKey(String code)
	{
		ItemStack stack = new ItemStack(NGTCore.protection_key, 1, 0);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString(ProtectionManager.KEY_ID, code);
		stack.setTagCompound(nbt);
		return stack;
	}

	@Override
    @SideOnly(Side.CLIENT)
	protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag)
    {
		NBTTagCompound nbt = holder.getItemStack().getTagCompound();
		list.add(TextFormatting.GRAY + "ID:" + nbt.getString(ProtectionManager.KEY_ID));
    }
}