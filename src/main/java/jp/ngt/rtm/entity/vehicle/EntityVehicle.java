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

import java.util.List;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.cfg.VehicleConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicle;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityVehicle extends EntityVehicleBase<ModelSetVehicle>
{
	private static final DataParameter<Byte> ON_GROUND = EntityDataManager.<Byte>createKey(EntityVehicle.class, DataSerializers.BYTE);
	private static final DataParameter<Float> SPEED = EntityDataManager.<Float>createKey(EntityVehicle.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> SPEED2 = EntityDataManager.<Float>createKey(EntityVehicle.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> MOV_FOR = EntityDataManager.<Float>createKey(EntityVehicle.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> MOV_STR = EntityDataManager.<Float>createKey(EntityVehicle.class, DataSerializers.FLOAT);
	private static final DataParameter<NBTTagCompound> NGTO = EntityDataManager.<NBTTagCompound>createKey(EntityVehicle.class, DataSerializers.COMPOUND_TAG);

	protected double speed;
	public float vibration;
	private float prevPitchDif, prevRollDif;
	public float accelerationStrafe, accelerationForward;//SUからの参照用
	private int accelerationDecCount;
	private VehicleNGTO vngto;

	public final VehicleController controller = new VehicleController();

	//rivate final List aabbList = new ArrayList();

	public EntityVehicle(World world)
	{
		super(world);
		this.setSize(2.0F, 2.0F);

		if(world.isRemote)
		{
			this.seatRotation = -MAX_SEAT_ROTATION;
		}
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(ON_GROUND, Byte.valueOf((byte)0));
		this.getDataManager().register(SPEED, Float.valueOf(0.0F));
		this.getDataManager().register(SPEED2, Float.valueOf(0.0F));
		this.getDataManager().register(MOV_FOR, Float.valueOf(0.0F));
		this.getDataManager().register(MOV_STR, Float.valueOf(0.0F));
		this.getDataManager().register(NGTO, new NBTTagCompound());
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.canBePushed() ? null : entity.getEntityBoundingBox();
    }

	@Override
	public double getMountedYOffset()
    {
		return 0.0D;
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.setNGTO(VehicleNGTO.readFromNBT(nbt.getCompoundTag("NGTO"), false));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		if(this.getNGTO() != null)
		{
			nbt.setTag("NGTO", this.getNGTO().writeToNBT());
		}
	}

	@Override
	public void onVehicleUpdate()
    {
        super.onVehicleUpdate();

        this.updateVibration();

        if(this.world.isRemote)
        {
            //NGTLog.debug("" + this.getAccelerationForward());
        }
        else
        {
        	if(this.getFirstPassenger() == null)
            {
        		this.controller.onUpdate(this);
            }

        	//Packet送信しまくり
        	//this.updateResourceState();//座席更新

        	this.setOnGround(this.onGround);
        	this.setSpeed((float)this.speed);
        	this.setSpeed2((float)this.speed);
        	this.setAccelerationForward(this.accelerationForward);
        	this.setAccelerationStrafe(this.accelerationStrafe);

        	this.updateRotation();
        	this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

	@Override
	protected void updateFallState()
	{
		if(this.onGround)
        {
        	this.motionY = 0.0D;
        }
        else
        {
        	super.updateFallState();
        }
	}

	@Override
	protected void updateMovement()
	{
		if(this.accelerationDecCount > 0)
		{
			//livingの加速度判定が5tick毎なので、livingの加速度が0の場合でもこちらの加速度が直ちに0にならないように
			float f0 = (float)(this.accelerationDecCount - 1) / (float)this.accelerationDecCount;
			this.accelerationForward *= f0;
	        this.accelerationStrafe *= f0;
		}

		if(this.shouldUpdateMotion())
    	{
    		if(this.getFirstPassenger() != null && this.getFirstPassenger() instanceof EntityLivingBase)
            {
                EntityLivingBase living = (EntityLivingBase)this.getFirstPassenger();
                this.updateMotion(living, living.moveStrafing, living.moveForward);
            }
    	}

    	super.updateMovement();
	}

	@Override
	protected void applyPhysicalEffect()
	{
		VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();

		if(!this.shouldUpdateMotion())
    	{
			if(this.onGround)
    		{
    			this.speed *= cfg.getFriction(this.onGround);
    			this.motionX *= 0.9D;
    			this.motionZ *= 0.9D;
                if(cfg.hoveringSpeed == 0.0F || this.motionY < 0.0D)
                {
                	this.motionY = 0.0D;
                }
    		}
    		else
    		{
    			this.speed *= 0.9999D;

    			super.applyPhysicalEffect();

    			if(cfg.hoveringSpeed != 0.0F)
                {
    				this.motionX *= 0.9D;
        			this.motionZ *= 0.9D;
                }
    		}
    	}
	}

	protected void updateVibration()
	{
		double dxz = this.motionX * this.motionX + this.motionZ * this.motionZ;

        if(this.vibration > 0.0F)
        {
        	this.vibration = 0.0F;
        }
        else
        {
        	float[] v = this.getResourceState().getResourceSet().getConfig().vibration;
        	this.vibration = dxz > 0.0D ? v[1] : v[0];
        }
	}

	/**プレーヤーの操作を反映するか*/
	protected boolean shouldUpdateMotion()
	{
		return this.onGround;
	}

	@Override
	protected void updateBlockCollisionState()
	{
		int x = NGTMath.floor(this.posX);
		int y = (int)this.getEntityBoundingBox().minY;
		int z = NGTMath.floor(this.posZ);
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = this.getEntityWorld().getBlockState(pos);
		boolean isAir = state.getBlock() == Blocks.AIR;
		boolean isLiquid = state.getMaterial().isLiquid();
		if(isAir || isLiquid || state.getBoundingBox(this.world, pos) == null)
		{
			pos = pos.down();
			state = this.getEntityWorld().getBlockState(pos);
			this.inWater = isLiquid;
		}

		/*AxisAlignedBB aabb = null;
		if(block instanceof BlockLargeRailBase)
		{
			this.aabbList.clear();
			block.addCollisionBoxesToList(this.worldObj, x, y, z,
					AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), this.aabbList, this);
			if(!this.aabbList.isEmpty())
			{
				aabb = (AxisAlignedBB)this.aabbList.get(0);
			}
		}
		else
		{
			aabb = block.getCollisionBoundingBoxFromPool(this.worldObj, x, y, z);
		}
		this.onGround = (aabb == null) ? false : (this.boundingBox.minY - aabb.maxY <= 0.0625D);*/
	}

	@Override
	protected void updateEntityCollisionState()
	{
		List list = this.world.getEntitiesWithinAABBExcludingEntity(this,
    			this.getEntityBoundingBox().expand(0.25D, 0.25D, 0.25D));
        if (list != null && !list.isEmpty())
        {
            for (int k = 0; k < list.size(); ++k)
            {
                Entity entity = (Entity)list.get(k);
                if (entity != this.getFirstPassenger() && entity.canBePushed())
                {
                    this.applyEntityCollision(entity);
                }
            }
        }
	}

	protected void updateMotion(EntityLivingBase entity, float moveStrafe, float moveForward)
	{
		VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();

        this.speed += moveForward * cfg.getAcceleration(this.onGround);
        float maxSpeed = cfg.getMaxSpeed(this.onGround);
        float f0 = (float)(moveStrafe * cfg.getYawCoefficient(this.onGround));
        f0 *= cfg.changeYawOnStopping ? ((this.speed >= 0.0F) ? 1.0F : -1.0F) : (this.speed / maxSpeed);
        float maxYaw = cfg.getMaxYaw(this.onGround);
        f0 = (f0 > maxYaw) ? maxYaw : ((f0 < -maxYaw) ? -maxYaw : f0);
        this.rotationYaw += f0;

        this.speed = (this.speed > maxSpeed) ? maxSpeed : ((this.speed < -maxSpeed) ? -maxSpeed : this.speed);

        Vec3 vec = this.getMotionVec();
        this.motionX = vec.getX();
        this.motionZ = vec.getZ();
        if(moveForward == 0.0F)
        {
        	this.speed *= cfg.getFriction(this.onGround);
        }

        if(Math.abs(this.speed) < 0.001D)
        {
        	this.speed = 0.0D;
        	this.motionX = this.motionZ = 0.0D;
		}

        this.accelerationForward = moveForward;
        this.accelerationStrafe = moveStrafe;
        this.accelerationDecCount = 5;
	}

	protected Vec3 getMotionVec()
	{
		VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();
		float maxSpeed = cfg.getMaxSpeed(this.onGround);
		//滑り再現:1.0~0.0
		float f0 = (float)(1.0D - (this.speed / maxSpeed));
		float f1 = this.prevRotationYaw + (NGTMath.wrapAngle(this.rotationYaw - this.prevRotationYaw) * f0);
		float yaw2 = (this.onGround || this.inWater) ? f1 : this.rotationYaw;
		Vec3 vec = PooledVec3.create(0.0D, 0.0D, this.speed);
        vec = vec.rotateAroundY(yaw2);
        return vec;
	}

	/**ブロック高さからPitchとRollを設定*/
	protected void updateRotation()
	{
		float prevPitch = this.rotationPitch;
		float prevRoll = this.rotationRoll;
		float pitch = this.rotationPitch;
		float roll = this.rotationRoll;

    	if(this.onGround)
		{
    		if(this.motionX != 0.0D || this.motionZ != 0.0D)
    		{
    			double hFront = this.getBlockHeight(this.rotationYaw);
        		double hBack = this.getBlockHeight(this.rotationYaw + 180.0F);
        		double hLeft = this.getBlockHeight(this.rotationYaw + 90.0F);
        		double hRight = this.getBlockHeight(this.rotationYaw - 90.0F);
        		pitch = (float)NGTMath.toDegrees(Math.atan2(hFront - hBack, this.width));
        		roll = (float)NGTMath.toDegrees(Math.atan2(hLeft - hRight, this.width));
        		//NGTLog.debug("" + hFront + "," + hBack + "," + hLeft + "," + hRight);
    		}
    		else
    		{
    			pitch *= 0.75F;
    			roll *= 0.75F;
    		}
		}
		else
		{
			pitch *= 0.75F;
			roll *= 0.75F;
		}

		if(Math.abs(pitch) < 0.01F)
		{
			pitch = 0.0F;
		}

		if(Math.abs(roll) < 0.01F)
		{
			roll = 0.0F;
		}

		float pitchDif = pitch - prevPitch;
		pitch = prevPitch + (pitchDif + this.prevPitchDif) * 0.5F;
		this.prevPitchDif = pitch - prevPitch;

		float rollDif = roll - prevRoll;
		roll = prevRoll + (rollDif + this.prevRollDif) * 0.5F;
		this.prevRollDif = roll - prevRoll;

		this.rotationPitch = pitch;
		this.rotationRoll = roll;
	}

	private double getBlockHeight(float yaw)
	{
		float rad = NGTMath.toRadians(yaw);
		double r = (double)this.width * 0.5D;
		int blockX = NGTMath.floor(this.posX + (double)MathHelper.sin(rad) * r);
		int blockZ = NGTMath.floor(this.posZ + (double)MathHelper.cos(rad) * r);
		int blockY = NGTMath.floor(this.posY) + (int)this.stepHeight;
		BlockPos pos = new BlockPos(blockX, blockY, blockZ);
		IBlockState state = null;
		AxisAlignedBB aabb = null;
		for(; blockY > 0; --blockY)
		{
			state = this.world.getBlockState(pos);
			//aabb = state.getCollisionBoundingBox(this.world, pos);
			aabb = state.getBlock().getCollisionBoundingBox(state, world, pos);//レール関係
			if(aabb != null)
			{
				double y = aabb.maxY;
				if(state.getBlock() instanceof BlockLargeRailBase)
				{
					y -= BlockLargeRailBase.AABB_ADD_Y;
				}
				return y + (double)blockY - 1;
			}
			pos = pos.down();
		}
		return this.posY;
	}

	@Override
	public Vec3 getRiderPos(Entity passenger)
	{
		Vec3 vec = super.getRiderPos(passenger);
		if(this.getNGTO() != null)
		{
			VehicleNGTO obj = this.getNGTO();
			vec = PooledVec3.create(obj.riderPosX * obj.scale, obj.riderPosY * obj.scale, obj.riderPosZ * obj.scale);
		}
		return vec;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
		super.updateFallState(y, onGroundIn, state, pos);
    }

	@Override
	public void fall(float distance, float damageMultiplier)
    {
		;
    }

	/**キー入力*/
	public void setUpDown(int par1){}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
		if(player.isSneaking())
    	{
			if(this.world.isRemote)
	        {
				player.openGui(RTMCore.instance, RTMCore.guiIdSelectEntityModel, player.world, this.getEntityId(), 0, 0);
	        }
			return true;
		}

        if(this.getFirstPassenger() instanceof EntityPlayer && this.getFirstPassenger() != player)
        {
            return true;
        }
        else
        {
            if(!this.world.isRemote)
            {
                player.startRiding(this);
            }
            return true;
        }
    }

	@Override
	public boolean attackEntityFrom(DamageSource source, float par2)
    {
		if(!this.world.isRemote && source.getTrueSource() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			if(PermissionManager.INSTANCE.hasPermission(player, RTMCore.EDIT_VEHICLE))
			{
				this.setDead();

				if(!player.capabilities.isCreativeMode)
				{
					this.entityDropItem(this.getVehicleItem(), 0.5F);
				}
			}
		}
		return true;
    }

	protected abstract ItemStack getVehicleItem();

	@Override
	public void applyEntityCollision(Entity entity)
    {
		if(!this.world.isRemote)
        {
            if(entity != this.getFirstPassenger() && (entity instanceof EntityLivingBase))
            {
            	double dxz = this.motionX * this.motionX + this.motionZ * this.motionZ;
        		if(dxz > 0.0D)
        		{
        			VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();
        			float strength = (float)(dxz / cfg.getMaxSpeed(this.onGround));
        			if(strength > 0.5F)
        			{
        				entity.attackEntityFrom(DamageSource.causeThornsDamage(this), strength);
        			}
        		}
            }
        }
    }

	@SideOnly(Side.CLIENT)
	@Override
    public void setRollAndSpeed(float speed, float roll)
	{
		//SpeedはDMで同期
		this.vehicleRoll = roll;
	}

	@Override
	public float getSpeed()
	{
		return this.getDataManager().get(SPEED);
	}

	@Override
	public void setSpeed(float par1)
	{
		this.getDataManager().set(SPEED, par1);
	}

	protected float getSpeed2()
	{
		return this.getDataManager().get(SPEED2);
	}

	protected void setSpeed2(float par1)
	{
		this.getDataManager().set(SPEED2, par1);
	}

	public boolean isOnGround()
	{
		return this.getDataManager().get(ON_GROUND) == 1;
	}

	protected void setOnGround(boolean par1)
	{
		byte value = (byte)(par1 ? 1 : 0);
		this.getDataManager().set(ON_GROUND, value);
	}

	public float getAccelerationForward()
	{
		return this.getDataManager().get(MOV_FOR);
	}

	public void setAccelerationForward(float par1)
	{
		this.getDataManager().set(MOV_FOR, par1);
	}

	public float getAccelerationStrafe()
	{
		return this.getDataManager().get(MOV_STR);
	}

	public void setAccelerationStrafe(float par1)
	{
		this.getDataManager().set(MOV_STR, par1);
	}

	@Override
	public VehicleNGTO getNGTO()
	{
		if(this.vngto == null)
		{
			//ngto使わない場合はダミーが入る
			this.vngto = VehicleNGTO.readFromNBT(this.getDataManager().get(NGTO), false);
		}
		return this.vngto;
	}

	public void setNGTO(VehicleNGTO ngto)
	{
		if(ngto != null)
		{
			this.getDataManager().set(NGTO, ngto.writeToNBT());
		}
	}

	@Override
	public void updateResourceState()
	{
		super.updateResourceState();

		VehicleConfig cfg = this.getResourceState().getResourceSet().getConfig();
		this.setSize(cfg.getSize()[0], cfg.getSize()[1]);
	}
}