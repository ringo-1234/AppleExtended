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

package jp.ngt.rtm.item;

import java.util.List;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMTooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTicket extends Item
{
	public final int ticketType;

	public ItemTicket(int par1)
	{
		super();
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.ticketType = par1;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
		if(!this.isInCreativeTab(tab)){return;}

		//ダメージ=使用回数
		switch(this.ticketType)
		{
		case 0: list.add(new ItemStack(this, 1, 1));break;
		case 1: list.add(new ItemStack(this, 1, 11));break;
		case 2: list.add(new ItemStack(this, 1, 0));break;
		}
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
		if(this.ticketType == 1)
		{
			String s = NGTUtil.translate(RTMTooltip.TICKET_REMAINING);
			tooltip.add(TextFormatting.GRAY + s + ":" + String.valueOf(stack.getItemDamage()));
		}

		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.getBoolean("Entered"))
		{
			String s = NGTUtil.translate(RTMTooltip.TICKET_ENTERED);
			tooltip.add(TextFormatting.GRAY + s);
		}
    }

	@Override
	@SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack)
    {
        return this.ticketType == 2;
    }

	public static ItemStack consumeTicket(ItemStack itemStack)
    {
    	int damage = itemStack.getItemDamage();
    	Item item = itemStack.getItem();
    	NBTTagCompound nbt = itemStack.getTagCompound();
    	itemStack.shrink(1);

    	if(nbt != null && nbt.getBoolean("Entered"))
    	{
    		if(damage == 0)
    		{
    			return null;
    		}
    		return new ItemStack(item, 1, damage);
    	}
    	else if(damage > 0)
    	{
    		ItemStack itemStack2 = new ItemStack(item, 1, --damage);
    		NBTTagCompound nbt2 = new NBTTagCompound();
    		nbt2.setBoolean("Entered", true);
    		itemStack2.setTagCompound(nbt2);
    		return itemStack2;
    	}
    	return null;
    }
}