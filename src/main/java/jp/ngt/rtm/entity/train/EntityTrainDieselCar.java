package jp.ngt.rtm.entity.train;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.world.World;

public final class EntityTrainDieselCar extends EntityTrainBase
{
	public EntityTrainDieselCar(World world)
	{
		super(world);
	}

	public EntityTrainDieselCar(World world, String s)
	{
		super(world, s);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.TRAIN_DC;
	}
}