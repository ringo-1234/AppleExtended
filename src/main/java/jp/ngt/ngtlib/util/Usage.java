package jp.ngt.ngtlib.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.ngt.ngtlib.io.NGTLog;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public final class Usage
{
	public static final Usage INSTANCE = new Usage();

	private final Map<UsageKey, UsageEntry> usages = new HashMap();

	private Usage(){}

	public void requestUsage(EntityPlayer player)
	{
		for(Entry<UsageKey, UsageEntry> entry : this.usages.entrySet())
		{
			if(entry.getValue().pattern.match(player))
			{
				entry.getValue().sendMessage(player);
				return;
			}
		}
		NGTLog.sendChatMessage(player, "usage.none");
	}

	public void addTooltip(Object par1, int par2, List<String> tooltip)
	{
		UsageEntry entry = this.getEntry(par1, par2);
		if(entry != null)
		{
			for(String s : entry.messages)
			{
				tooltip.add(TextFormatting.YELLOW + NGTUtil.translate(s));
			}
		}
	}

	private UsageEntry getEntry(Object par1, int par2)
	{
		UsageKey key = new UsageKey(par1, par2);
		return this.usages.get(key);
	}

	public UsageEntry add(Block block, int meta, String... par2)
	{
		UsageEntry entry = new UsageEntry(new UsagePattern(){
			@Override
			public boolean match(EntityPlayer player)
			{
				Item item = Item.getItemFromBlock(block);
				ItemStack stack = player.getHeldItemMainhand();
				if(stack != null && stack.getItem() == item && (meta < 0 || meta == stack.getItemDamage()))
				{
					return true;
				}
				return false;
			}
		}, par2);
		this.usages.put(new UsageKey(block, meta), entry);
		return entry;
	}

	public UsageEntry add(Item item, int meta, String... par2)
	{
		UsageEntry entry = new UsageEntry(new UsagePattern(){
			@Override
			public boolean match(EntityPlayer player)
			{
				ItemStack stack = player.getHeldItemMainhand();
				if(stack != null && stack.getItem() == item && (meta < 0 || meta == stack.getItemDamage()))
				{
					return true;
				}
				return false;
			}
		}, par2);
		this.usages.put(new UsageKey(item, meta), entry);
		return entry;
	}

	public UsageEntry add(Class<? extends Entity> clazz, String... par2)
	{
		UsageEntry entry = new UsageEntry(new UsagePattern(){
			@Override
			public boolean match(EntityPlayer player)
			{
				Entity entity = player.getRidingEntity();
				if(entity != null && isInstance(entity.getClass(), clazz))
				{
					return true;
				}
				return false;
			}
		}, par2);
		this.usages.put(new UsageKey(clazz, 0), entry);
		return entry;
	}

	private static boolean isInstance(Class<?> target, Class<?> clazz)
	{
		if(target == clazz)
		{
			return true;
		}
		else if(target.getSuperclass() != null)
		{
			return isInstance(target.getSuperclass(), clazz);
		}
		return false;
	}

	public static class UsageEntry
	{
		public final UsagePattern pattern;
		public final String[] messages;

		public UsageEntry(UsagePattern par1, String... par2)
		{
			this.pattern = par1;
			this.messages = par2;
		}

		public void sendMessage(EntityPlayer player)
		{
			for(String s : this.messages)
			{
				NGTLog.sendChatMessage(player, s);
			}
		}
	}

	public static interface UsagePattern
	{
		boolean match(EntityPlayer player);
	}

	public static class UsageKey
	{
		public final Object key1;
		public final int key2;

		public UsageKey(Object par1, int par2)
		{
			this.key1 = par1;
			this.key2 = par2;
		}

		@Override
		public int hashCode()
		{
			return this.key1.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof UsageKey)
			{
				UsageKey key = (UsageKey)obj;
				return key.key1 == this.key1 && (this.key2 < 0 || key.key2 < 0 || key.key2 == this.key2);
			}
			return false;
		}
	}
}