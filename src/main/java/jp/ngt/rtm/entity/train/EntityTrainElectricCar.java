package jp.ngt.rtm.entity.train;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.world.World;

public final class EntityTrainElectricCar extends EntityTrainBase
{
	public EntityTrainElectricCar(World world)
	{
		super(world);
	}

	public EntityTrainElectricCar(World world, String s)
	{
		super(world, s);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.TRAIN_EC;
	}
}