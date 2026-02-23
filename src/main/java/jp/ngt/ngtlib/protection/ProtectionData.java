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

package jp.ngt.ngtlib.protection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public class ProtectionData extends WorldSavedData
{
	private Map<String, NBTTagCompound> protectedObjs = new HashMap<String, NBTTagCompound>();

	public ProtectionData(String par1)
	{
		super(par1);
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = nbt.getTagList("Objects", 10);
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagElement = tagList.getCompoundTagAt(i);
            String objName = tagElement.getString("ObjName");
            this.protectedObjs.put(objName, tagElement);
        }
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = new NBTTagList();
        Iterator iterator = this.protectedObjs.entrySet().iterator();
        while(iterator.hasNext())
        {
        	Entry<String, NBTTagCompound> entry = (Entry<String, NBTTagCompound>)iterator.next();
        	NBTTagCompound tagElement = entry.getValue();
        	tagElement.setString("ObjName", entry.getKey());
            tagList.appendTag(tagElement);
        }
        nbt.setTag("Objects", tagList);
        return nbt;
	}

	public boolean hasObject(String id)
	{
		return this.protectedObjs.containsKey(id);
	}

	public NBTTagCompound getObject(String id)
	{
		return this.protectedObjs.get(id);
	}

	public void setObject(String id, NBTTagCompound data)
	{
		this.protectedObjs.put(id, data);
		this.markDirty();
	}

	public void removeObject(String id)
	{
		this.protectedObjs.remove(id);
		this.markDirty();
	}

	public Map<String, NBTTagCompound> getDatas()
	{
		return this.protectedObjs;
	}
}