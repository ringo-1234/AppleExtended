package jp.ngt.rtm.entity.vehicle;

import jp.ngt.ngtlib.io.NGTLog;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**VehicleをWeatherEffectとして追加するためのラッパー*/
public final class WeatherEffectDummy extends Entity
{
	private final EntityVehicleBase parent;

	public WeatherEffectDummy(World world, EntityVehicleBase vehicle)
	{
		super(world);
		this.parent = vehicle;
		NGTLog.debug("[WED] Spawn %d", this.parent.getEntityId());
	}

	public EntityVehicleBase getParent()
	{
		return this.parent;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		this.posX = this.parent.posX;
		this.posY = this.parent.posY;
		this.posZ = this.parent.posZ;
		this.rotationYaw = this.parent.rotationYaw;
		this.rotationPitch = this.parent.rotationPitch;

		if(this.parent.isDead)
		{
			this.setDead();
			NGTLog.debug("[WED] Remove %d", this.parent.getEntityId());
		}
	}

	@Override
	public int getEntityId()
    {
		return this.parent.getEntityId();
    }

	@Override
	public boolean shouldRenderInPass(int pass)
    {
		return this.parent.shouldRenderInPass(pass);
    }

	@Override
	public boolean isInRangeToRender3d(double x, double y, double z)
    {
		return true;
    }

	@Override
	public int getBrightnessForRender()
    {
		return this.parent.getBrightnessForRender();
    }

	@Override
	protected void entityInit() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
