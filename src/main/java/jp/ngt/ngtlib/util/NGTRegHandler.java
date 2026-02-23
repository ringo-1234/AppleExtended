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

package jp.ngt.ngtlib.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class NGTRegHandler
{
	public static final NGTRegHandler INSTANCE = new NGTRegHandler();

	private final List<Block> blocks = new ArrayList<>();
	private final List<Item> items = new ArrayList<>();

	private NGTRegHandler(){}

	private static void checkName(String name)
	{
		if(name.contains(":"))
		{
			throw new IllegalArgumentException("Name contains ':' -> " + name);
		}

		String lower = name.toLowerCase();
		if(!lower.equals(name))
		{
			throw new IllegalArgumentException("Name has upper case letter ':' -> " + name);
		}
	}

	public static <T extends Block> T register(T block, String name, String unlocalizedName, CreativeTabs tab, String modID)
	{
		return register(block, name, unlocalizedName, tab, null, modID);
	}

	public static <T extends Block> T register(T block, String name, String unlocalizedName, CreativeTabs tab, Class<? extends ItemBlock> itemClass, String modID)
	{
		checkName(name);
		INSTANCE.blocks.add(block);
		block.setRegistryName(modID, name);
		block.setUnlocalizedName(unlocalizedName);
		if(tab != null)
		{
			block.setCreativeTab(tab);
		}

		ItemBlock item = null;

		if(itemClass != null)
		{
			try
			{
				Constructor<? extends ItemBlock> constructor = itemClass.getConstructor(Block.class);
				item = constructor.newInstance(block);

			}
			catch(ReflectiveOperationException e)
			{
				e.printStackTrace();
				return block;
			}
		}
		else
		{
			item = new ItemBlock(block);
		}

		ForgeRegistries.BLOCKS.register(block);
		register(item, name, unlocalizedName, null, modID);
		return block;
	}

	public static <T extends Item> T register(T item, String name, String unlocalizedName, CreativeTabs tab, String modID)
	{
		checkName(name);
		INSTANCE.items.add(item);
		item.setRegistryName(modID, name);
		item.setUnlocalizedName(unlocalizedName);
		if(tab != null)
		{
			item.setCreativeTab(tab);
		}
		ForgeRegistries.ITEMS.register(item);
		return item;
	}

	/*public void registerItems(RegistryEvent.Register<Item> event)
	{
		Item[] array = this.items.toArray(new Item[this.items.size()]);
		event.getRegistry().registerAll(array);
		NGTLog.debug("[NGTR] Registered " + array.length + " items.");
	}

	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		Block[] array = this.blocks.toArray(new Block[this.blocks.size()]);
		event.getRegistry().registerAll(array);
		NGTLog.debug("[NGTR] Registered " + array.length + " blocks.");
	}*/

	public static void register(Class<? extends Entity> clazz, String regName, String name, int id, int range, int freq, boolean velUpdate, IMod mod)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(mod.getId(), regName), clazz, name, id, mod, range, freq, velUpdate);
	}
}