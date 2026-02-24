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

package jp.ngt.rtm.entity.train.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public final class FormationData extends WorldSavedData
{
	public FormationData(String name)
	{
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = nbt.getTagList("Formations", 10);
		for(int i = 0; i < tagList.tagCount(); ++i)
    	{
    		NBTTagCompound tag = tagList.getCompoundTagAt(i);
    		Formation formation = Formation.readFromNBT(tag, false);
    		//登録はFormationコンストラクタで行ってる
    	}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = new NBTTagList();
		for(Formation formation : FormationManager.getInstance().getFormations().values())
		{
			NBTTagCompound tag = new NBTTagCompound();
			formation.writeToNBT(tag, false);
			tagList.appendTag(tag);
		}
		nbt.setTag("Formations", tagList);
		return nbt;
	}
}