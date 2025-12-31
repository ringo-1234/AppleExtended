package jp.ngt.rtm.entity.train;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class EntityTanker extends EntityTrainBase
{
	public EntityTanker(World world)
	{
		super(world);
	}

	public EntityTanker(World world, String s)
	{
		super(world, s);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.TRAIN_TC;
	}
}