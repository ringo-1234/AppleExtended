package jp.ngt.rtm.entity.train;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.NGTParticle;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMParticle;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.RTMSound;
import jp.ngt.rtm.electric.WireManager;
import jp.ngt.rtm.entity.npc.EntityMotorman;
import jp.ngt.rtm.entity.npc.macro.MacroRecorder;
import jp.ngt.rtm.entity.train.util.BogieController;
import jp.ngt.rtm.entity.train.util.BogieController.MotionState;
import jp.ngt.rtm.entity.train.util.BogieController.UpdateFlag;
import jp.ngt.rtm.entity.train.util.EnumNotch;
import jp.ngt.rtm.entity.train.util.Formation;
import jp.ngt.rtm.entity.train.util.FormationEntry;
import jp.ngt.rtm.entity.train.util.FormationManager;
import jp.ngt.rtm.entity.train.util.TrainState;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.item.ItemTrain;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import jp.ngt.rtm.network.PacketNotice;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.world.IChunkLoader;
import jp.ngt.rtm.world.RTMChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityTrainBase extends EntityVehicleBase<ModelSetTrain> implements IChunkLoader
{
	private static final DataParameter<Integer> BOGIE_ID0 = EntityDataManager.<Integer>createKey(EntityTrainBase.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BOGIE_ID1 = EntityDataManager.<Integer>createKey(EntityTrainBase.class, DataSerializers.VARINT);

	public static final short MAX_AIR_COUNT = 2880;
	public static final short MIN_AIR_COUNT = 2480;
	public static final float TRAIN_WIDTH = 2.75F;
	public static final float TRAIN_HEIGHT = 1.25F - 0.0625F;//レールに合わせ高さ修正

	public final BogieController bogieController = new BogieController();
	private Formation formation;

	private float trainSpeed;
	/**notch x -18*/
	public int brakeCount = 72;
    public int atsCount;
    public boolean onRail = true;

	public int brakeAirCount = MAX_AIR_COUNT;
    public boolean complessorActive;

    /**Roll更新に使用*/
    private float wave;

	public EntityTrainBase(World world)
	{
		super(world);
		this.setSize(TRAIN_WIDTH, TRAIN_HEIGHT);
		this.noClip = true;
	}

	public EntityTrainBase(World world, String s)
	{
		this(world);
	}

	//Init///////////////////////////////////////////////////////////////////////////////////////

	/**ItemTrainから使用*/
	public void spawnTrain(World world)
	{
		world.spawnEntity(this);
		this.formation = FormationManager.getInstance().createNewFormation(this);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(BOGIE_ID0, Integer.valueOf(0));
		this.getDataManager().register(BOGIE_ID1, Integer.valueOf(0));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);

		NBTTagCompound entryData = new NBTTagCompound();
		this.writeFormationData(entryData);
		nbt.setTag("FormationEntry", entryData);

		nbt.setInteger("trainDir", this.getTrainDirection());
	}

	private void writeFormationData(NBTTagCompound nbt)
	{
		if(this.formation == null){return;}//ワールド読み込み時にformationがnull

		FormationEntry entry = this.formation.getEntry(this);
		if(entry != null)
		{
			nbt.setLong("FormationId", this.formation.id);
			nbt.setByte("EntryPos", entry.entryId);
			nbt.setByte("EntryDir", entry.dir);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);

		NBTTagCompound entryData = nbt.getCompoundTag("FormationEntry");
		this.readFormationData(entryData);

		this.setTrainDirection(nbt.getInteger("trainDir"));
	}

	private void readFormationData(NBTTagCompound nbt)
	{
		long id = nbt.getLong("FormationId");
		byte pos = nbt.getByte("EntryPos");
		byte dir = nbt.getByte("EntryDir");
		Formation f0 = FormationManager.getInstance().getFormation(id);
		if(f0 == null)
		{
			this.formation = FormationManager.getInstance().createNewFormation(this);
		}
		else
		{
			this.formation = f0;
			f0.setTrain(this, pos, dir);
		}
	}

	//Property////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Entity[] getParts()
    {
        return null;
    }

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1)
    {
		//return (par1 instanceof EntityVehiclePart) ? null : par1.getEntityBoundingBox();
		return null;
    }

	@Override
	public double getYOffset()
	{
		return 0.0D;//Rider高さのみに使用してる
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDead()
    {
		super.setDead();

		if(!this.world.isRemote)
		{
			this.releaseTicket();
			this.bogieController.setDead(this);

			try
			{
				this.formation.onRemovedTrain(this);
			}
			catch(IndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
		}
    }

	@Override
	public void onVehicleUpdate()
	{
		this.updateSpeed();

		if(this.existBogies())
        {
			this.bogieController.updateBogies(this);
        }

		super.onVehicleUpdate();

		if(this.world.isRemote)
		{
			this.spawnSmoke();
		}
		else//!isRemote
		{
			this.updateChunks();
			this.updateATS();
		}
	}

	@Override
	protected void updateFallState()
	{
		if(this.onRail)
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
		if(this.formation.isFrontCar(this))
		{
			//更新順を先頭車からにするため、一旦Formationへ投げる
			this.formation.updateTrainMovement();
		}
	}

	/**Formationから呼ぶこと*/
	public void updateTrainMovement(MotionState state)
	{
		if(state == MotionState.FLY)
		{
			if(this.onRail)
			{
				float speed = this.getSpeed() * this.getMoveDir();
				Vec3 vec3 = PooledVec3.create(0.0D, 0.0D, speed);
				vec3 = vec3.rotateAroundX(this.rotationPitch + 15.0F);
				vec3 = vec3.rotateAroundY(this.rotationYaw);
				this.motionX = vec3.getX();
				this.motionY = vec3.getY();
				this.motionZ = vec3.getZ();
				this.onRail = false;
				this.noClip = false;
				this.setSpeed(0.0F);
			}
		}
		else if(state == MotionState.MOVE)//この判定除くとFLYとSTOP間で振動
		{
			if(!this.onRail)
			{
				this.motionX = this.motionY = this.motionZ = 0.0D;
				this.onRail = true;
				this.noClip = true;
			}
		}
		else if(state == MotionState.STOP)
		{
			;
		}

		if(!this.onRail)
		{
			super.updateMovement();
			this.bogieController.updateBogiePos(this, 0, UpdateFlag.NONE);
			this.bogieController.updateBogiePos(this, 1, UpdateFlag.NONE);
		}
	}

	@Override
	protected void applyPhysicalEffect()
	{
		double d0 = 0.99D;
		this.motionX *= d0;
        this.motionY *= d0;
        this.motionZ *= d0;

        //motion利用のpitch計算はガクガクする

        float f0 = 0.125F;

        if(this.rotationPitch > 0.0F)
        {
        	this.rotationPitch -= f0;
        }
        else if(this.rotationPitch < 0.0F)
        {
        	this.rotationPitch += f0;
        }

        if(Math.abs(this.rotationPitch) < f0)
        {
        	this.rotationPitch = 0.0F;
        }

        this.getBogie(0).rotationPitch = this.rotationPitch;
        this.getBogie(1).rotationPitch = this.rotationPitch * -1.0F;
	}

	//BCから呼び出し
	public void updateRoll(float par1)
	{
		TrainConfig cfg = this.getResourceState().getResourceSet().getConfig();
		float f0 = -cfg.rolling;
		float pendulum = NGTMath.wrapAngle((this.rotationYaw - this.prevRotationYaw) * f0);
		if(this.getTrainDirection() == 1)
    	{
			pendulum *= -1.0F;
    	}
		float roll = par1 + pendulum;
		//float dif = roll - this.rotationRoll;
		this.wave = (float)((this.wave + this.getSpeed() * cfg.rollSpeedCoefficient) % (2.0D * Math.PI));//総走行距離(2PI区切り)
		float sw = (NGTMath.getSin(this.wave) + NGTMath.getSin(this.wave * cfg.rollVariationCoefficient)) * 0.5F;
		this.rotationRoll = roll + sw * cfg.rollWidthCoefficient;
		//できればPID制御したい
	}

	protected void updateATS()
	{
		if(this.atsCount > 0)
		{
			++this.atsCount;
			if(this.atsCount >= 100)
			{
				this.stopTrain(false);
				this.atsCount = 0;
			}
		}
	}

	@Override
	protected void updateBlockCollisionState()
	{
		int minY = NGTMath.floor(this.posY) - 3;
		TileEntityLargeRailBase rail = TileEntityLargeRailBase.getRailFromCoordinates(this.world, this.posX, this.posY + 1.0D, this.posZ, minY);
		if(rail != null)
		{
			TileEntityLargeRailCore railCore = rail.getRailCore();
			if(railCore != null)
			{
				int signal = railCore.getSignal();
				this.setSignal(signal);
			}
		}

		this.doBlockCollisions();//call Block.onEntityCollidedWithBlock()
	}

	@SideOnly(Side.CLIENT)
	protected void spawnSmoke()
	{
		ModelSetTrain set = this.getResourceState().getResourceSet();
		if(set.getConfig().smoke != null)
		{
			float speed = this.getSpeed();
			int notch = this.getNotch();
			Random random = this.world.rand;

			for(int i = 0; i < set.getConfig().smoke.length; ++i)
			{
				Vec3 vec3 = PooledVec3.create((Double)set.getConfig().smoke[i][0], (Double)set.getConfig().smoke[i][1], (Double)set.getConfig().smoke[i][2]);
				vec3 = vec3.rotateAroundX(this.rotationPitch);
				vec3 = vec3.rotateAroundY(this.rotationYaw);
				double min = (Double)set.getConfig().smoke[i][4];
				double max = (Double)set.getConfig().smoke[i][5];
				int amount = speed > 0.05F ? (int)max : (notch > 0 ? (int)min + 3 : (int)min);
				EnumParticleTypes particle = NGTParticle.getParticle((String)set.getConfig().smoke[i][3]);

				for(int j = 0; j < amount; ++j)
				{
					double d0 = this.posX + vec3.getX() + (double)random.nextFloat() * 0.5D - 0.25D;
		            double d1 = this.posY + vec3.getY() + this.getVehicleYOffset();
		            double d2 = this.posZ + vec3.getZ() + (double)random.nextFloat() * 0.5D - 0.25D;
		            double smokeSpeed = 0.0625D;
		            if(set.getConfig().smoke.length == 7)
		            {
		            	smokeSpeed = (Double)set.getConfig().smoke[i][6];
		            }
		            double vx = (random.nextDouble() * 2.0D - 1.0D) * smokeSpeed;
		            double vz = (random.nextDouble() * 2.0D - 1.0D) * smokeSpeed;
		            this.world.spawnParticle(particle, d0, d1, d2, vx, 0.25D, vz);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void updateAnimation()
	{
		super.updateAnimation();

		ModelSetTrain modelSet = this.getResourceState().getResourceSet();

		//座席//
		if(this.getTrainDirection() == 0 && this.seatRotation > -MAX_SEAT_ROTATION)
		{
			--this.seatRotation;
		}

		if(this.getTrainDirection() == 1 && this.seatRotation < MAX_SEAT_ROTATION)
		{
			++this.seatRotation;
		}

		//方向幕//
		this.setRollsignAnimation(this.getVehicleState(TrainStateType.Destination));

		if(this.rollsignAnimation > this.rollsignV)
		{
			--this.rollsignAnimation;
		}
		else if(this.rollsignAnimation < this.rollsignV)
		{
			++this.rollsignAnimation;
		}

		//コンプレッサー(電車のみ)//
		if(this.getResourceState().type == RTMResource.TRAIN_EC)
		{
			if(this.complessorActive)
			{
				++this.brakeAirCount;
				if(this.brakeAirCount >= MAX_AIR_COUNT)
				{
					this.complessorActive = false;
					RTMCore.proxy.playSound(this, RTMSound.CP_FIN, 1.0F, 1.0F);
				}
			}
			else
			{
				if(this.brakeAirCount < MIN_AIR_COUNT)
				{
					this.complessorActive = true;
				}
			}
		}
	}

	private static final int[] PANTO_POS_ZERO = new int[]{0, 0};

	@Override
	protected int[] getPantographMaxHeight()
	{
		int[] ia;
		ModelSetTrain modelSet = this.getResourceState().getResourceSet();
		if(modelSet.getConfig().pantoPos != null)
		{
			ia = new int[modelSet.getConfig().pantoPos.length];
			for(int i = 0; i < ia.length; ++i)
			{
				float[] fa = modelSet.getConfig().pantoPos[i];
				if(fa[3] > 0.0F)
				{
					double trainY = this.posY + this.getVehicleYOffset();
					Vec3 vec = PooledVec3.create(fa[0], fa[3], fa[1]);
					vec = vec.rotateAroundX(this.rotationPitch);
					vec = vec.rotateAroundY(this.rotationYaw);
					double y = WireManager.INSTANCE.getWireY(this.posX + vec.getX(), trainY + vec.getY(), this.posZ + vec.getZ());
					ia[i] = (int)(((y - trainY) - fa[3]) / (fa[2] - fa[3]) * MAX_PANTOGRAPH_MOVE);

					//架線有り&走行中&寒冷地->スパーク
					long time = this.getEntityWorld().getWorldTime() % 24000;
					if((trainY + vec.getY() != y) && this.getSpeed() != 0.0F && (time >= 11615 && time <= 22925)
							&& this.getEntityWorld().getBiome(this.getPosition()).isSnowyBiome())
					{
						Random rand = this.getEntityWorld().rand;
						if(rand.nextInt(20) == 0)
						{
							int count = rand.nextInt(5) + 1;
							for(int k = 0; k < 5; ++k)
							{
								NGTParticle.INSTANCE.spawnParticle(this.getEntityWorld(), RTMParticle.PARTICLE_SPARK, false,
										this.posX + vec.getX(), y, this.posZ + vec.getZ(),
										rand.nextGaussian() * 0.0625D, -0.25D, rand.nextGaussian() * 0.0625D, ia);
							}
						}
					}
				}
			}
		}
		else
		{
			ia = PANTO_POS_ZERO;
		}
		return ia;
	}

	@SideOnly(Side.CLIENT)
    public float getCouplerYaw(int dir)
    {
		EntityTrainBase conTrain = this.getConnectedTrain(dir);
		if(conTrain == null)
		{
			return 0.0F;
		}
		else
		{
			float dif = NGTMath.getAngleD(this.posZ, this.posX, conTrain.posZ, conTrain.posX);
			float angle = NGTMath.wrapAngle(dif - (this.rotationYaw + (dir == 0 ? 0.0F : 180.0F)));
			return angle + (Math.abs(angle) > 90.0F ? 180.0F : 0.0F);
		}
    }

	protected void playBrakeReleaseSound(boolean isStrong)
	{
		ModelSetTrain modelSet = this.getResourceState().getResourceSet();
		String sound = isStrong ? modelSet.getConfig().sound_BrakeRelease : modelSet.getConfig().sound_BrakeRelease2;
		if(sound != null)
		{
			RTMCore.proxy.playSound(this, sound, 1.0F, 1.0F);
		}
	}

	protected void updateSpeed()
	{
		if(!this.onRail){return;}

		int notch = this.getNotch();

		Entity passenger = this.getFirstPassenger();
		if(passenger == null || !(passenger instanceof EntityPlayer || passenger instanceof EntityMotorman))
		{
			if(notch > 0)
			{
				//this.setNotch(0);//コマンド制御を考慮し、無効化
			}
		}

		boolean isBrakeDisabled = true;
		float speed = this.getSpeed();
		if(notch < 0)
		{
			int max = notch < 0 ? notch * -18 : 0;
			if(this.brakeCount < max)
			{
				++this.brakeCount;
				if(this.world.isRemote)
				{
					--this.brakeAirCount;
				}
			}
			else if(this.brakeCount > max)
			{
				this.brakeCount -= (this.brakeCount - max > 1) ? 2 : 1;
			}
		}
		else if(notch >= 0)//ブレーキ解
		{
			if(this.brakeCount > 0)
			{
				if(speed <= 0.0F)
				{
					isBrakeDisabled = false;
				}
				this.brakeCount -= 2;
			}
			else if(this.brakeCount < 0)
			{
				this.brakeCount = 0;
			}
		}
		if(this.isControlCar())
		{
			if(isBrakeDisabled && !this.world.isRemote)
			{
				ModelSetTrain set = this.getResourceState().getResourceSet();
				float acceleration = EnumNotch.getAcceleration(notch, Math.abs(speed), set.getConfig());

				int role = this.getVehicleState(TrainStateType.Role);
				if((role == 2 && speed > 0.0F) || (role == TrainState.Role_Front.data && speed < 0.0F))
				{
					acceleration = Math.abs(acceleration);
				}

				if(role == 2)
				{
					acceleration *= -1.0F;
				}

				if(notch >= 0)//ブレーキ解
				{
					float deceleration;
					if(this.rotationPitch == 0.0F)
					{
						float f1 = 0.0002F;
						deceleration = speed > 0.0F ? f1 : (speed < 0.0F ? -f1 : 0.0F);
					}
					else//坂
					{
						float dec = 0.0125F;
						float f2 = (this.getTrainDirection() == 0) ? dec : -dec;
						//-1.0~1.0, 斜面で下方向へ加速
						deceleration = NGTMath.sin(this.rotationPitch) * f2;
					}
					speed += acceleration - deceleration;
				}
				else
				{
					speed += acceleration;
				}

				this.setSpeed(speed);
			}
		}
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
    }

	@Override
	protected void setRotation(float yaw, float pitch)
    {
        this.rotationYaw = NGTMath.wrapAngle(yaw);
        this.rotationPitch = NGTMath.wrapAngle(pitch);
    }

    @Override
    public void move(MoverType type, double x, double y, double z)
    {
    	if(!this.onRail)
    	{
    		super.move(type, x, y, z);
    	}
    }

    @Override
	public void addVelocity(double par1, double par3, double par5){}

	/**連結時の車両同士の距離*/
	public double getDefaultDistanceToConnectedTrain(EntityTrainBase par1)
	{
		ModelSetVehicleBase<TrainConfig> modelSet0 = this.getResourceState().getResourceSet();
		ModelSetVehicleBase<TrainConfig> modelSet1 = par1.getResourceState().getResourceSet();
		if(modelSet0 != null && modelSet1 != null)
		{
			double d0 = modelSet0.getConfig().trainDistance;
			double d1 = modelSet1.getConfig().trainDistance;
			return d0 + d1;
		}
		return 20.25D;
	}

	/**車両同士が連結可能な距離内にあるか*/
	public boolean inConnectableRange(EntityTrainBase par1)
	{
		double d0 = this.getDefaultDistanceToConnectedTrain(par1);
		return this.getDistanceSq(par1) <= d0 * d0;
	}

	@Override
	public Vec3 getRiderPos(Entity passenger)
	{
		return super.getRiderPos(passenger).add(0.0D, this.getMountedYOffset(), 0.0D);
	}

	@Override
	protected int getRiderPosIndex()
	{
		return this.getTrainDirection();
	}

	@Override
	public double getMountedYOffset()
    {
        return this.height + this.TRAIN_HEIGHT - 0.93F;
    }

	@Override
	public boolean attackEntityFrom(DamageSource par1, float par2)
    {
		if(this.isEntityInvulnerable(par1) || this.isDead)
        {
            return false;
        }
        else
        {
        	if(!par1.isExplosion() && par1.getTrueSource() instanceof EntityPlayer)
        	{
        		EntityPlayer player = (EntityPlayer)par1.getTrueSource();
        		if(!this.world.isRemote && PermissionManager.INSTANCE.hasPermission(player, RTMCore.EDIT_VEHICLE))
                {
            		if(!player.capabilities.isCreativeMode)
                    {
        				int damage = 0;
        				ModelSetTrain model = this.getResourceState().getResourceSet();
        				if(model != null)
        				{
        					String type = model.getConfig().getSubType();
            				damage = type.equals("DC") ? 0 : (type.equals("EC") ? 1 : (type.equals("CC") ? 2 : 3));
        				}
                		this.entityDropItem(new ItemStack(RTMItem.itemtrain, 1, damage), 0.0F);
                    }
            		this.setDead();
                }
    			return true;
        	}
        }
		return false;
    }

	/**EntityBogieで呼ばれる*/
	protected boolean interactTrain(EntityBogie bogie, EntityPlayer player)
	{
		if(this.getFirstPassenger() != null && !this.getFirstPassenger().equals(player)){return true;}

		if(!this.world.isRemote)
        {
        	ItemStack itemstack = player.inventory.getCurrentItem();
        	int id1 = bogie.getBogieId();
        	if(!itemstack.isEmpty())
        	{
        		if(itemstack.getItem() == RTMItem.crowbar)
        		{
        			this.concatenation(bogie, player);
        		}
        		else if(itemstack.getItem() == RTMItem.itemMotorman)
        		{
        			this.mountMotorman(bogie, player, itemstack);
        		}
        		else if(itemstack.getItem() == RTMItem.paddle)
        		{
        			NGTLog.sendChatMessage(player, "UUID:" + this.getUniqueID().toString() + "(bogie, found train)");
        		}
        	}
        	else
        	{
        		if(id1 >= 0)
            	{
        			this.mountEntityToTrain(player, id1);
                	//player.addStat(RTMAchievement.rideTrain, 1);
            	}
			}
        }
        return true;
	}

	/**連結・解結*/
	protected void concatenation(EntityBogie bogie, EntityPlayer player)
	{
		int id1 = bogie.getBogieId();
		if(id1 >= 0)
		{
			if(this.getConnectedTrain(id1) == null)
			{
				bogie.isActivated = true;
    			NGTLog.sendChatMessage(player, "message.train.concatenation_mode");
			}
			else
			{
				this.formation.onDisconnectedTrain(this, id1);
				NGTLog.sendChatMessage(player, "message.train.deconcatenation");
			}
		}
	}

	protected void mountMotorman(EntityBogie bogie, EntityPlayer player, ItemStack stack)
	{
		int id1 = bogie.getBogieId();
		if(id1 >= 0)
    	{
			EntityMotorman motorman = new EntityMotorman(this.world, player);
			motorman.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
			if(this.world.spawnEntity(motorman))
			{
				this.mountEntityToTrain(motorman, id1);
				stack.shrink(1);
			}
    	}
	}

	private void mountEntityToTrain(Entity entity, int direction)
	{
		if(this.getTrainDirection() != direction)
		{
			this.setSpeed(-this.getSpeed());
		}
		this.setTrainDirection(direction);
		entity.startRiding(this);
	}

	@Override
	protected void removePassengerFromVehicle(Entity passenger)
	{
		Entity riding = this.getBogie(this.getTrainDirection());//台車周辺に降ろす
		if(riding == null || this.getDistanceSq(riding) > 15.0D * 15.0D)//バグ等で台車が離れすぎてる場合の対策
		{
			riding = this;
		}
		this.fixRiderPosOnDismount(passenger, riding);
	}

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

		if(NGTUtil.isEquippedItem(player, RTMItem.paddle))
		{
			//Debug
			if(!this.world.isRemote)
	        {
				NGTLog.sendChatMessage(player, "UUID:" + this.getUniqueID().toString() + "(train)");
	        }
			return true;
		}
		return false;
    }

	@Override
	public void updateResourceState()
	{
		super.updateResourceState();

		//if(!this.world.isRemote)
		{
			this.updateTrainSize();
		}
	}

	//ServerOnly
	private void updateTrainSize()
	{
		TrainConfig cfg = this.getResourceState().getResourceSet().getConfig();
		float[][] bogiePos = cfg.getBogiePos();
		float hBogieZ = Math.abs(bogiePos[0][2] - bogiePos[1][2]) * 0.5F;
		float td2 = (float)(cfg.trainDistance / 3.0D * 2.0D);
		float width = hBogieZ < td2 ? hBogieZ : td2;
		if(width < TRAIN_WIDTH)
		{
			this.setSize(width, TRAIN_HEIGHT);
			if(this.existBogies())
			{
				this.getBogie(0).setBogieSize(width, TRAIN_HEIGHT);
				this.getBogie(1).setBogieSize(width, TRAIN_HEIGHT);
			}
		}
	}

	/**
	 * @param par1 連結される台車
	 * @param par2 連結対象の台車
	 */
	public void connectTrain(EntityBogie par1, EntityBogie par2)
	{
		if(!this.world.isRemote && par2.getTrain() != null)
		{
			int id1 = par1.getBogieId();
			int id2 = par2.getBogieId();
			if(id1 >= 0 && id2 >= 0 && this.getConnectedTrain(id1) == null && par2.getTrain().formation != null)
			{
				this.formation.connectTrain(this, par2.getTrain(), id1, id2, par2.getTrain().formation);
				RTMCore.proxy.playSound(par1, "block.anvil.place", 1.0F, 1.0F);

				EntityPlayer player = null;
				if(this.getFirstPassenger() instanceof EntityPlayer)
				{
					player = (EntityPlayer)this.getFirstPassenger();
				}
				else if(par2.getTrain().getFirstPassenger() instanceof EntityPlayer)
				{
					player = (EntityPlayer)par2.getTrain().getFirstPassenger();
				}

				if(player != null)
				{
					NGTLog.sendChatMessage(player, "message.train.concatenated");
				}
			}
		}
	}

	@Override
	public float getSpeed()
	{
		return this.trainSpeed;
	}

	@Override
	public void setSpeed(float par1)
	{
		if(this.world.isRemote)
		{
			this.trainSpeed = par1;
		}
		else
		{
			if(this.isControlCar())
			{
				this.formation.setSpeed(par1);
			}
		}
	}

	public void setSpeed_NoSync(float par1)
	{
		this.trainSpeed = par1;
	}

	public void stopTrain(boolean changeSpeed)
	{
		if(this.formation != null)
		{
			this.setNotch(-8);
			if(changeSpeed)
			{
				this.setSpeed(0.0F);
			}
		}
	}

	public boolean isControlCar()
	{
		int role = this.getVehicleState(TrainStateType.Role);
		return role == TrainState.Role_Front.data || role == 2;
	}

	/**台車2つが生成済か*/
	public boolean existBogies()
	{
		//当クラスのgetBogie使わないとClientでのBogie登録がされない
		return this.getBogie(0) != null && this.getBogie(1) != null;
	}

	public int getBogieEntityId(int bogieId)
	{
		DataParameter<Integer> key = bogieId == 0 ? BOGIE_ID0 : BOGIE_ID1;
		return this.getDataManager().get(key);
	}

	public void setBogieEntityId(int bogieId, int entityId)
	{
		DataParameter<Integer> key = bogieId == 0 ? BOGIE_ID0 : BOGIE_ID1;
		this.getDataManager().set(key, Integer.valueOf(entityId));
	}

	public EntityBogie getBogie(int bogieId)
	{
		return this.bogieController.getBogie(this, bogieId);
	}

	/**@param par1 0 or 1*/
	public EntityTrainBase getConnectedTrain(int par1)
	{
		if(this.formation != null)
		{
			FormationEntry entry = this.formation.getEntry(this);
			if(entry == null){return null;}
			int pos = entry.entryId;
			int dif = (par1 == 0) ? -1 : 1;
			if(entry.dir == 1)
			{
				dif *= -1;
			}
			pos += dif;
			if(pos < 0 || pos >= this.formation.size())
			{
				return null;
			}
			FormationEntry connected = this.formation.get(pos);
			if(connected != null)
			{
				return connected.train;
			}
		}
		return null;
	}

	@Override
	public Formation getFormation()
    {
    	return this.formation;
    }

	/**
	 * @param par1
	 * @param i 位置
	 * @param par3 向き
	 */
    public void setFormation(Formation par1)
    {
    	this.formation = par1;
    }
	public int getTrainDirection()
	{
		return this.getVehicleState(TrainStateType.Direction);
	}

	public void setTrainDirection(int par1)
	{
		if(this.formation == null)
		{
			this.setTrainDirection_NoSync((byte)par1);
		}
		else
		{
			this.formation.setTrainDirection((byte)par1, this);
		}
	}

	public void setTrainDirection_NoSync(byte par1)
	{
		super.setVehicleState(TrainStateType.Direction, par1);
		int id2 = 1 - par1;
    	if(id2 < 2)
    	{
    		if(this.existBogies())
    		{
    			this.getBogie(par1).setFront(true);
    			this.getBogie(id2).setFront(false);
    		}
    	}
	}

	public int getNotch()
	{
		return this.getVehicleState(TrainStateType.Notch);
	}

	@SideOnly(Side.CLIENT)
	public void syncNotch(int notchInc)
	{
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, "notch:" + notchInc, this));
		MacroRecorder.INSTANCE.recNotch(this.getEntityWorld(), notchInc);
	}

	/**プレーヤーが変更したとき呼ぶ*/
	public boolean addNotch(Entity driver, int par2)
	{
		if(par2 != 0)
		{
			int i = this.getNotch();
			if(this.setNotch(i + par2))
			{
				RTMCore.proxy.playSound(driver, RTMSound.LEVER, 1.0F, 1.0F);
				if(i < 0 && par2 > 0 && !this.world.isRemote)
				{
					this.playBrakeReleaseSound(i == -1);
				}
				return true;
			}
		}
		return false;
	}

	public boolean setNotch(int par1)
	{
		if(this.isControlCar())
		{
			if(par1 <= 5 && par1 >= -8)
			{
				int prevNotch = this.getNotch();
				if(prevNotch != par1)
				{
					super.setVehicleState(TrainStateType.Notch, (byte)par1);
					return true;
				}
			}
		}
		return false;
	}

	/**停止現示でATS確認後は-1*/
	public int getSignal()
	{
		return this.getVehicleState(TrainStateType.Signal);
	}

	public void setSignal(int par1)
	{
		int signal = this.getSignal();
		if(par1 > 0 && signal != -1)
		{
			this.setSignal2(par1);

			if(par1 == 1 && this.getSpeed() > 0.0F)
			{
				++this.atsCount;
			}
		}
	}

	public void setSignal2(int par1)
	{
		if(par1 == -1)
		{
			this.atsCount = 0;
		}

		super.setVehicleState(TrainStateType.Signal, (byte)par1);
	}

    @Override
    public void setVehicleState(TrainStateType type, byte data)
    {
    	byte b = data < type.min ? type.max : (data > type.max ? type.min : data);
    	if(this.formation != null)
    	{
    		this.formation.setTrainStateData(type, b, this);
    	}
    }

    public void setTrainStateData_NoSync(TrainStateType type, byte data)
    {
    	super.setVehicleState(type, data);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected boolean useInteriorLight()
	{
    	return this.getVehicleState(TrainStateType.InteriorLight) > 0;
	}

    @Override
    public float getMoveDir()
	{
    	int i = this.getTrainDirection();
    	if(this.getBogie(i) != null)
    	{
    		boolean b0 = this.getBogie(i).isFront();
    		return ((i == 0 && b0) || (i == 1 && !b0)) ? 1.0F : -1.0F;
    	}
		return  1.0F;
	}

    @Override
    public float getVehicleYOffset()
    {
    	return TRAIN_HEIGHT;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
    	return ItemTrain.convertFormationAsItem(this);
    }

    //ChunkLoader////////////////////////////////////////////////////////////////////////////////////////////////

    private Ticket ticket;
	private final Set<ChunkPos> loadedChunks = new HashSet();
    private int prevChunkCoordX;
    private int prevChunkCoordZ;

    /**ServerTickごとに呼び出し*/
    private void updateChunks()
    {
    	if(this.isChunkLoaderEnable())
		{
    		this.forceChunkLoading();
		}
		else
		{
			this.releaseTicket();
		}

    	this.prevChunkCoordX = this.chunkCoordX;
    	this.prevChunkCoordZ = this.chunkCoordZ;
    }

    @Override
    public boolean isChunkLoaderEnable()
    {
    	return this.getVehicleState(TrainStateType.ChunkLoader) > 0;
    }

    private void releaseTicket()
    {
    	this.loadedChunks.clear();
    	if(this.ticket != null)
    	{
    		ForgeChunkManager.releaseTicket(this.ticket);
            this.ticket = null;
    	}
    }

    private boolean requestTicket()
    {
    	Ticket chunkTicket = RTMChunkManager.INSTANCE.getNewTicket(this.world, Type.ENTITY);
        if(chunkTicket != null)
        {
        	int depth = this.getVehicleState(TrainStateType.ChunkLoader);
        	chunkTicket.getModData();
            chunkTicket.setChunkListDepth(depth);
            chunkTicket.bindEntity(this);
            this.setChunkTicket(chunkTicket);
            return true;
        }
        NGTLog.debug("[RTM] Failed to get ticket (Chunk Loader)");
        return false;
    }

    @Override
    public void setChunkTicket(Ticket par1)
    {
    	if(this.ticket != par1)
    	{
            ForgeChunkManager.releaseTicket(this.ticket);
        }
    	this.ticket = par1;
    }

    @Override
    public void forceChunkLoading()
    {
    	this.forceChunkLoading(this.chunkCoordX, this.chunkCoordZ);
    }

    @Override
    public void forceChunkLoading(int x, int z)
    {
    	if(this.world.isRemote)
    	{
    		//this.setupChunks(x, z);
    	}
    	else
    	{
    		if(this.ticket == null && !this.requestTicket()){return;}

        	if(!(x == this.prevChunkCoordX && z == this.prevChunkCoordZ))
        	{
        		this.setupChunks(x, z);
        	}

            for(ChunkPos chunk : this.loadedChunks)
            {
            	ForgeChunkManager.forceChunk(this.ticket, chunk);
                //ForgeChunkManager.reorderChunk(this.ticket, chunk);//並び替え
            }
            ChunkPos myChunk = new ChunkPos(x, z);//省くと機能しない
            ForgeChunkManager.forceChunk(this.ticket, myChunk);
    	}
    }

    private void setupChunks(int xChunk, int zChunk)
    {
    	int rad = this.getVehicleState(TrainStateType.ChunkLoader);
		RTMChunkManager.INSTANCE.getChunksAround(this.loadedChunks, xChunk, zChunk, rad);
    }
}