package jp.ngt.rtm;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabRTM extends CreativeTabs
{
	public static final CreativeTabs RAILWAY = new CreativeTabRTM("rtm_railway");
	public static final CreativeTabs INDUSTRY = new CreativeTabRTM("rtm_industry");
	public static final CreativeTabs TOOLS = new CreativeTabRTM("rtm_tools");

	public CreativeTabRTM(String label)
	{
		super(label);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem()
	{
		if(this == RAILWAY)
		{
			return new ItemStack(RTMItem.itemtrain);
		}
		else if(this == INDUSTRY)
		{
			return new ItemStack(RTMItem.steel_ingot);
		}
		else if(this == TOOLS)
		{
			return new ItemStack(RTMItem.crowbar);
		}
		else
		{
			return new ItemStack(RTMItem.crowbar);
		}
	}
}