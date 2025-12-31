package jp.ngt.rtm.entity.vehicle;

import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.cfg.VehicleConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPlane extends EntityVehicle
{
	public EntityPlane(World world)
	{
		super(world);
		this.stepHeight = 0.5F;
	}

	@Override
	protected boolean shouldUpdateMotion()
	{
		return true;
	}

	@Override
	protected void updateFallState()
	{
		boolean hovering = (this.getResourceState().getResourceSet().getConfig().hoveringSpeed != 0.0F);
		if(hovering)
		{
			this.motionY *= 0.8D;
		}
		else if(this.speed == 0.0D)
		{
			super.updateFallState();
		}
	}

	@Override
	protected void updateMotion(EntityLivingBase entity, float moveStrafe, float moveForward)
	{
		VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();
		boolean hovering = (cfg.hoveringSpeed != 0.0F);

		this.speed += moveForward * cfg.getAcceleration(this.onGround);
		float maxSpeed = cfg.getMaxSpeed(this.onGround);
        float f0 = (float)(moveStrafe * cfg.getYawCoefficient(this.onGround));
        if(!cfg.changeYawOnStopping)
        {
        	f0 *= (this.speed / maxSpeed);
        }
        float maxYaw = cfg.getMaxYaw(this.onGround);
        if(f0 > maxYaw)
        {
        	f0 = maxYaw;
        }
        else if(f0 < -maxYaw)
        {
        	f0 = -maxYaw;
        }
        this.rotationYaw += f0;

        if(this.speed > maxSpeed)
        {
        	this.speed = maxSpeed;
        }
        else if(this.speed < 0.0D)
        {
        	if(hovering)
        	{
        		if(this.speed < -maxSpeed)
                {
                	this.speed = -maxSpeed;
                }
        	}
        	else
        	{
        		this.speed = 0.0D;
        	}
        }

        Vec3 vec = this.getMotionVec();
        this.motionX = vec.getX();
        this.motionZ = vec.getZ();
        double d0 = 0.05D * (1.0D - (this.speed / maxSpeed));
        //this.motionY = this.speed > 0.0D ? vec.yCoord : this.motionY - 0.05D;
        if(!hovering)
        {
        	this.motionY = vec.getY() - d0;
        }

        if(moveForward == 0.0F)
        {
        	this.speed *= cfg.getFriction(this.onGround);
        }

        if(Math.abs(this.speed) < 0.001D)
        {
        	this.speed = 0.0D;
        	this.motionX = this.motionZ = 0.0D;
        }

        if(this.speed > 0.0D && !this.onGround)
        {
        	this.rotationRoll = moveStrafe * (float)(this.speed / maxSpeed) * -cfg.getRollCoefficient(this.onGround);
        }
        else
        {
        	this.rotationRoll *= 0.75F;
        }

        if(Math.abs(this.rotationRoll) < 0.01F)
        {
        	this.rotationRoll = 0.0F;
        }
	}

	@Override
	protected Vec3 getMotionVec()
	{
		if((this.onGround && this.rotationPitch < 0.0F) || this.inWater)
		{
			return super.getMotionVec();
		}
		Vec3 vec = PooledVec3.create(0.0D, 0.0D, this.speed);
		vec = vec.rotateAroundX(this.rotationPitch);
		vec = vec.rotateAroundY(this.rotationYaw);
        return vec;
	}

	@Override
	protected void updateRotation()
	{
		this.rotationPitch *= 0.99F;

		if(Math.abs(this.rotationPitch) < 0.01F)
		{
			this.rotationPitch = 0.0F;
		}
	}

	@Override
	public void setUpDown(int par1)
	{
		VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();

		if(cfg.hoveringSpeed != 0.0F)
		{
			this.motionY += (float)par1 * cfg.hoveringSpeed;
		}
		else
		{
			if(this.speed > 0.0D)
			{
				if(par1 != 0)
				{
					this.rotationPitch += cfg.getPitchCoefficient(this.onGround) * (float)par1 * (float)(this.speed / cfg.getMaxSpeed(this.onGround));
					if(this.onGround && this.rotationPitch < 1.0F)
					{
						this.rotationPitch = 0.0F;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean disableUnmount()
	{
		float speed = this.getSpeed2();
		boolean onGround = this.isOnGround();
		boolean hovering = this.getResourceState().getResourceSet().getConfig().hoveringSpeed != 0.0F;
		BlockPos pos = this.getPosition().down();
		AxisAlignedBB aabb = this.getEntityWorld().getBlockState(pos).getCollisionBoundingBox(this.getEntityWorld(), pos);
		if(aabb != null)
		{
			aabb = aabb.offset(pos);
		}
		boolean onBlock = aabb != null && aabb.maxY >= this.posY;
		return !onGround && (speed > 0.0F || hovering) && !onBlock;
	}

	@Override
	protected ItemStack getVehicleItem()
	{
		return new ItemStack(RTMItem.itemVehicle, 1, 2);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.VEHICLE_PLANE;
	}
}