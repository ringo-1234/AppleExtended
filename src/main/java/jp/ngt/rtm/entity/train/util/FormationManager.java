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

import java.util.HashMap;
import java.util.Map;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class FormationManager
{
	private static final String SAVE_NAME = "rtm_formations";

	private final boolean isRemote;
	private FormationData saveData;
	private final Map<Long, Formation> formationMap = new HashMap<Long, Formation>();

	public FormationManager(boolean par1)
	{
		this.isRemote = par1;
	}

	public static FormationManager getInstance()
	{
		return RTMCore.proxy.getFormationManager();
	}

	public void loadData(World world)
	{
		if(world instanceof WorldServer && world.provider.getDimension() == 0)
		{
			FormationData data = (FormationData)world.loadData(FormationData.class, SAVE_NAME);
			if(data == null)
			{
				data = new FormationData(SAVE_NAME);
				world.setData(SAVE_NAME, data);
			}
			this.saveData = data;
		}
	}

	public Map<Long, Formation> getFormations()
	{
		return this.formationMap;
	}

	public Formation getFormation(long id)
	{
		return this.formationMap.get(id);
	}

	public void setFormation(long id, Formation formation)
	{
		this.formationMap.put(id, formation);
		if(!this.isRemote && this.saveData != null)//NBT読み込み時に、先に編成登録が行われるため
		{
			this.saveData.markDirty();
		}
	}

	public void removeFormation(long id)
	{
		this.formationMap.remove(id);
		if(!this.isRemote && this.saveData != null)
		{
			this.saveData.markDirty();
		}
		//パケット送る
	}

	//TickEventから呼び出し
	/*@Deprecated
	public void updateFormations(World world)
	{
		for(Entry<Long, Formation> entry : this.formationMap.entrySet())
		{
			if(!entry.getValue().onUpdate(world))
			{
				this.removingFormations.add(entry.getKey());
			}
        }

		if(!this.removingFormations.isEmpty())
		{
			for(long id : this.removingFormations)
			{
				this.removeFormation(id);
			}
			this.removingFormations.clear();
		}
	}*/

	public long getNewFormationId()
	{
		return System.currentTimeMillis();
	}

	/**編成を新規に作成(車両設置時のみ使用)*/
	public Formation createNewFormation(EntityTrainBase par1)
	{
		long newId = this.getNewFormationId();
		Formation formation = new Formation(newId, 1);
		formation.setTrain(par1, 0, 0);
		return formation;
	}
}