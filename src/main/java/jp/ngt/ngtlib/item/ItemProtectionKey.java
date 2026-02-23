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