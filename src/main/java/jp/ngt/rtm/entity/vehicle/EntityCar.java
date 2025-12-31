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