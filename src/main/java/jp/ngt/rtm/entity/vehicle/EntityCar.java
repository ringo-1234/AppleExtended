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

package jp.ngt.rtm.entity.vehicle;

import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityCar extends EntityVehicle
{
	public EntityCar(World world)
	{
		super(world);
		this.stepHeight = 2.0F;
	}

	@Override
	protected ItemStack getVehicleItem()
	{
		return new ItemStack(RTMItem.itemVehicle, 1, 0);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.VEHICLE_CAR;
	}
}