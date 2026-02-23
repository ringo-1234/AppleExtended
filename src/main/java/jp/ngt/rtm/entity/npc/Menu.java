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

package jp.ngt.rtm.entity.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Menu
{
	private final List<MenuEntry> menuList = new ArrayList<>();

	public Menu(String s, Role role)
	{
		this.init(s, role);
	}

	/**重複時は上書き*/
	public boolean add(MenuEntry entry)
	{
		if(!entry.item.isEmpty())
		{
			int index = this.menuList.indexOf(entry);
			if(index >= 0)
			{
				NGTLog.debug("[Menu] 重複アイテムを削除 : %s", entry.item.getDisplayName());
				this.menuList.remove(index);
			}
			return this.menuList.add(entry);
		}
		return false;
	}

	public void remove(int index)
	{
		this.menuList.remove(index);
	}

	public MenuEntry get(int index)
	{
		return this.menuList.get(index);
	}

	public List<MenuEntry> getList()
	{
		return this.menuList;
	}

	public boolean init(String s, Role role)
	{
		this.menuList.clear();

		if(s != null && !s.isEmpty())
		{
			try
			{
				NBTTagCompound nbt0 = JsonToNBT.getTagFromJson(s);
				NBTTagList nbttaglist = nbt0.getTagList("list", 10);
				for(int i = 0; i < nbttaglist.tagCount(); ++i)
				{
					NBTTagCompound nbt = nbttaglist.getCompoundTagAt(i);
					MenuEntry entry = MenuEntry.readFromNBT(nbt);
					if(entry != null)
					{
						this.add(entry);
					}
				}

				if(!this.menuList.isEmpty())
				{
					return true;
				}
			}
			catch(NBTException e)
			{
				e.printStackTrace();
			}
		}

		if(role == Role.SALESPERSON)
		{
			this.add(new MenuEntry(new ItemStack(Items.COOKIE, 10), 200));
			this.add(new MenuEntry(new ItemStack(Items.COOKED_FISH, 5), 500));
		}
		else if(role == Role.BUYER)
		{
			this.add(new MenuEntry(new ItemStack(Items.COOKIE, 1), 20));
			this.add(new MenuEntry(new ItemStack(Items.COOKED_FISH, 1), 100));
		}

		return false;
	}

	@Override
	public String toString()
	{
		NBTTagList nbttaglist = new NBTTagList();
		for(MenuEntry entry : this.menuList)
		{
			nbttaglist.appendTag(entry.writeToNBT());
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("list", nbttaglist);
		return nbt.toString();
	}

	public boolean exportToText()
	{
		File file = NGTFileLoader.saveFile(FileType.JSON);
		if(file != null)
		{
			return NGTText.writeToText(file, this.toString());
		}
		return false;
	}

	public boolean importFromText(Role role)
	{
		File file = NGTFileLoader.selectFile(FileType.JSON);
		if(file != null)
		{
			try
			{
				return this.init(NGTText.readText(file, false, "UTF-8"), role);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
}
