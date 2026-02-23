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

package jp.ngt.rtm.entity;

import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityBumpingPost extends EntityInstalledObject
{
	public EntityBumpingPost(World par1)
	{
		super(par1);
		this.setSize(1.5F, 1.5F);
	}

	@Override
	protected void dropItems()
	{
		this.entityDropItem(new ItemStack(RTMItem.installedObject, 1, IstlObjType.BUMPING_POST.id), 0.0F);
	}

	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_BUMPINGPOST;
	}
}