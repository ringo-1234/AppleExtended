package jp.ngt.rtm.entity.train;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.world.World;

public final class EntityTrainTest extends EntityTrainBase
{
	public EntityTrainTest(World world)
	{
		super(world);
	}

	public EntityTrainTest(World world, String s)
	{
		super(world, s);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.TRAIN_TEST;
	}
}