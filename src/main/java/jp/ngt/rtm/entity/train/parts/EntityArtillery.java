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

package jp.ngt.rtm.entity.train.parts;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.RTMSound;
import jp.ngt.rtm.entity.EntityBullet;
import jp.ngt.rtm.entity.FirearmController;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.item.ItemAmmunition;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.cfg.FirearmConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetFirearm;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityArtillery extends EntityCargoWithModel<ModelSetFirearm>
{
	public static final DataParameter<Integer> AMMO = EntityDataManager.<Integer>createKey(EntityArtillery.class, DataSerializers.VARINT);
	public static final DataParameter<Float> B_PITCH = EntityDataManager.<Float>createKey(EntityArtillery.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> B_YAW = EntityDataManager.<Float>createKey(EntityArtillery.class, DataSerializers.FLOAT);

	private int recoilCount;
	private int ammoCount;

	public final FirearmController controller = new FirearmController();

	public EntityArtillery(World par1)
	{
		super(par1);
		this.setSize(3.0F, 2.5F);
	}

	public EntityArtillery(World par1, ItemStack itemStack, int x, int y, int z)
	{
		super(par1, itemStack, x, y, z);
	}

	public EntityArtillery(World par1, EntityVehicleBase par2, ItemStack par3, float[] par4Pos, byte id)
	{
		super(par1, par2, par3, par4Pos, id);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(AMMO, Integer.valueOf(-1));
		this.getDataManager().register(B_PITCH, Float.valueOf(0.0F));
		this.getDataManager().register(B_YAW, Float.valueOf(0.0F));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.setBarrelYaw(nbt.getFloat("barrelYaw"));
		this.setBarrelPitch(nbt.getFloat("barrelPitch"));
		this.setAmmo(nbt.getInteger("ammo"));
	}

	@Override
	protected void readCargoFromNBT(NBTTagCompound nbt)
	{
		super.readCargoFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		nbt.setFloat("barrelYaw", this.getBarrelYaw());
		nbt.setFloat("barrelPitch", this.getBarrelPitch());
		nbt.setInteger("ammo", this.hasAmmo());
	}

	@Override
	protected void writeCargoToNBT(NBTTagCompound nbt)
	{
		super.writeCargoToNBT(nbt);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(this.recoilCount > 0)
		{
			--this.recoilCount;
		}

		if(this.ammoCount > 0)
		{
			if(this.recoilCount <= 0)
			{
				BulletType type = BulletType.getBulletType(this.hasAmmo());
				boolean b = this.getEntityWorld().isRemote ? this.fireOnClient(type) : this.fireOnServer(type);
				this.recoilCount = this.getResourceState().getResourceSet().getConfig().rateOfFire;
				--this.ammoCount;
			}
		}
		else
		{
			if(!this.getEntityWorld().isRemote && this.ammoCount == 0 && this.hasAmmo() >= 0)
			{
				this.setAmmo(-1);
			}
		}

		if(this.getPassengers().isEmpty())
		{
			this.controller.onUpdate(this);
		}
		else
		{
			Entity ridden = this.getPassengers().get(0);
			float yaw = NGTMath.wrapAngle(-ridden.rotationYaw - this.rotationYaw);
			this.setBarrelYaw(yaw);
			this.setBarrelPitch(ridden.rotationPitch);
		}
	}

	@Override
	public void updatePassenger(Entity passenger)
	{
		if(this.isPassenger(passenger))
		{
			FirearmConfig cfg = this.getResourceState().getResourceSet().getConfig();
			Vec3 v31 = PooledVec3.create(cfg.playerPos[0], cfg.playerPos[1], cfg.playerPos[2]);
			v31 = v31.rotateAroundX(this.rotationPitch);
			v31 = v31.rotateAroundY(this.rotationYaw);
			Vec3 v32 = v31.add(this.posX, this.posY, this.posZ);
			passenger.setPosition(v32.getX(), v32.getY() + passenger.getYOffset(), v32.getZ());
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateYawAndPitch(EntityPlayer player)
	{
		FirearmConfig cfg = this.getResourceState().getResourceSet().getConfig();
		float speed = 0.0F;
		float riderYaw = NGTMath.wrapAngle(-player.rotationYaw - this.rotationYaw);
		float yaw = this.getBarrelYaw();

		{
			speed = cfg.rotationSpeedY;
			yaw += (riderYaw - yaw) * speed;

			if(yaw > cfg.yaw[0])
			{
				yaw = cfg.yaw[0];
			}

			if(yaw < cfg.yaw[1])
			{
				yaw = cfg.yaw[1];
			}

			player.rotationYaw = player.prevRotationYaw = -yaw - this.rotationYaw;
		}

		float riderPitch = player.rotationPitch;
		float pitch = this.getBarrelPitch();

		{
			speed = cfg.rotationSpeedX;
			pitch += (riderPitch - pitch) * speed;

			if(pitch > -cfg.pitch[1])
			{
				pitch = -cfg.pitch[1];
			}

			if(pitch < -cfg.pitch[0])
			{
				pitch = -cfg.pitch[0];
			}

			player.rotationPitch = player.prevRotationPitch = pitch;
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if(super.processInitialInteract(player, hand))
		{
			return true;
		}

		if(this.isBeingRidden())
		{
			return true;
		}
		else
		{
			ItemStack itemstack = player.inventory.getCurrentItem();
			if(itemstack != null)
			{
				int type = this.getAmmoType(itemstack.getItemDamage());
				if(itemstack.getItem() instanceof ItemAmmunition && type >= 0)
				{
					if(!this.world.isRemote)
					{
						if(this.hasAmmo() < 0 || this.hasAmmo() == type)
						{
							if(-this.ammoCount < this.getResourceState().getResourceSet().getConfig().magazineSize)
							{
								this.setAmmo(type);
								SoundEvent sound = SoundEvents.BLOCK_GRASS_PLACE;
								this.world.playSound(this.posX, this.posY, this.posZ,
										sound, SoundCategory.BLOCKS, 1.0F, 0.8F, false);
								itemstack.shrink(1);
								--this.ammoCount;
							}
						}
					}
					return true;
				}
				else if(itemstack.getItem() == RTMItem.paddle)
				{
					if(!this.world.isRemote)
					{
						if(this.hasAmmo() >= 0)
						{
							this.fire(player, BulletType.getBulletType(this.hasAmmo()), -this.ammoCount);
						}
					}
					return true;
				}
			}

			if(!this.world.isRemote)
			{
				player.startRiding(this);
			}
			return true;
		}
	}

	public void onFireKeyDown(EntityPlayer player)
	{
		for(int i = 0; i < player.inventory.mainInventory.size(); ++i)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() == RTMItem.bullet)
			{
				int type = this.getAmmoType(stack.getItemDamage());
				if(type >= 0)
				{
					if(!player.capabilities.isCreativeMode)
					{
						stack.shrink(1);
						if(stack.getCount() <= 0)
						{
							player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						}
					}

					if(!this.getEntityWorld().isRemote)
					{
						this.fire(player, BulletType.getBulletType(type), 1);
					}
					return;
				}
			}
		}
	}

	public boolean fire(@Nullable EntityPlayer player, BulletType type, int count)
	{
		if(player != null && !PermissionManager.INSTANCE.hasPermission(player, RTMCore.USE_CANNON)){return false;}

		this.setAmmo(type.id);
		int max = this.getResourceState().getResourceSet().getConfig().magazineSize;
		if(count > max)
		{
			count = max;
		}
		this.ammoCount = count;
		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, "fire," + count, this));
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void onFirePacket(String msg)
	{
		this.ammoCount = Integer.valueOf(msg.split(",")[1]);
	}

	protected boolean fireOnServer(BulletType type)
	{
		FirearmConfig cfg = this.getResourceState().getResourceSet().getConfig();
		Vec3 vMuzzle = PooledVec3.create(cfg.muzzlePos[0] - cfg.modelPartsX.pos[0], cfg.muzzlePos[1] - cfg.modelPartsX.pos[1], cfg.muzzlePos[2] - cfg.modelPartsX.pos[2]);
		vMuzzle = vMuzzle.rotateAroundX(-this.getBarrelPitch());
		vMuzzle = vMuzzle.add(cfg.modelPartsX.pos[0] - cfg.modelPartsY.pos[0], cfg.modelPartsX.pos[1] - cfg.modelPartsY.pos[1], cfg.modelPartsX.pos[2] - cfg.modelPartsY.pos[2]);
		vMuzzle = vMuzzle.rotateAroundY(this.getBarrelYaw());
		vMuzzle = vMuzzle.add(cfg.modelPartsY.pos[0] - cfg.modelPartsN.pos[0], cfg.modelPartsY.pos[1] - cfg.modelPartsN.pos[1], cfg.modelPartsY.pos[2] - cfg.modelPartsN.pos[2]);
		vMuzzle = vMuzzle.rotateAroundY(this.rotationYaw);
		vMuzzle = vMuzzle.add(this.posX, this.posY, this.posZ);

		Vec3 vBolt = PooledVec3.create(cfg.modelPartsBarrel.pos[0] - cfg.modelPartsX.pos[0], cfg.modelPartsBarrel.pos[1] - cfg.modelPartsX.pos[1], cfg.modelPartsBarrel.pos[2] - cfg.modelPartsX.pos[2]);
		vBolt = vBolt.rotateAroundX(-this.getBarrelPitch());
		vBolt = vBolt.add(cfg.modelPartsX.pos[0] - cfg.modelPartsY.pos[0], cfg.modelPartsX.pos[1] - cfg.modelPartsY.pos[1], cfg.modelPartsX.pos[2] - cfg.modelPartsY.pos[2]);
		vBolt = vBolt.rotateAroundY(this.getBarrelYaw());
		vBolt = vBolt.add(cfg.modelPartsY.pos[0] - cfg.modelPartsN.pos[0], cfg.modelPartsY.pos[1] - cfg.modelPartsN.pos[1], cfg.modelPartsY.pos[2] - cfg.modelPartsN.pos[2]);
		vBolt = vBolt.rotateAroundY(this.rotationYaw);
		vBolt = vBolt.add(this.posX, this.posY, this.posZ);

		double mX = vMuzzle.getX() - vBolt.getX();
		double mY = vMuzzle.getY() - vBolt.getY();
		double mZ = vMuzzle.getZ() - vBolt.getZ();

		EntityBullet entity = new EntityBullet(this.world, this, type, vMuzzle.getX(), vMuzzle.getY(), vMuzzle.getZ(), mX, mY, mZ);
		this.world.spawnEntity(entity);

		return true;
	}

	@SideOnly(Side.CLIENT)
	protected boolean fireOnClient(BulletType type)
	{
		FirearmConfig cfg = this.getResourceState().getResourceSet().getConfig();
		Vec3 vMuzzle = PooledVec3.create(cfg.muzzlePos[0] - cfg.modelPartsX.pos[0], cfg.muzzlePos[1] - cfg.modelPartsX.pos[1], cfg.muzzlePos[2] - cfg.modelPartsX.pos[2]);
		vMuzzle = vMuzzle.rotateAroundX(-this.getBarrelPitch());
		vMuzzle = vMuzzle.add(cfg.modelPartsX.pos[0] - cfg.modelPartsY.pos[0], cfg.modelPartsX.pos[1] - cfg.modelPartsY.pos[1], cfg.modelPartsX.pos[2] - cfg.modelPartsY.pos[2]);
		vMuzzle = vMuzzle.rotateAroundY(this.getBarrelYaw());
		vMuzzle = vMuzzle.add(cfg.modelPartsY.pos[0] - cfg.modelPartsN.pos[0], cfg.modelPartsY.pos[1] - cfg.modelPartsN.pos[1], cfg.modelPartsY.pos[2] - cfg.modelPartsN.pos[2]);
		vMuzzle = vMuzzle.rotateAroundY(this.rotationYaw);
		vMuzzle = vMuzzle.add(this.posX, this.posY, this.posZ);

		if(type == BulletType.rocket)
		{
			this.world.playSound(vMuzzle.getX(), vMuzzle.getY(), vMuzzle.getZ(),
					SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
		}
		else if(type == BulletType.cannon_40cm || type == BulletType.cannon_Atomic)
		{
			for(int i = 0; i < 6; ++i)
			{
				double xRand = this.world.rand.nextDouble() * 2.0D - 1.0D;
				double yRand = this.world.rand.nextDouble() * 2.0D - 1.0D;
				double zRand = this.world.rand.nextDouble() * 2.0D - 1.0D;
				this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, vMuzzle.getX() + xRand, vMuzzle.getY() + yRand, vMuzzle.getZ() + zRand, 0.0D, 0.0D, 0.0D);
				this.world.playSound(vMuzzle.getX() + xRand, vMuzzle.getY() + yRand, vMuzzle.getZ() + zRand,
						SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
		}
		else
		{
			RTMCore.proxy.playSound(this, RTMSound.GUN, RTMCore.gunSoundVol, 1.0F);
		}

		return true;
	}

	public float getRecoil()
	{
		return (float)this.recoilCount / (float)this.getResourceState().getResourceSet().getConfig().rateOfFire;
	}

	public float getBarrelYaw()
	{
		return this.getDataManager().get(B_YAW);
	}

	public void setBarrelYaw(float par1)
	{
		if(!this.getEntityWorld().isRemote)
		{
			par1 = MathHelper.wrapDegrees(par1);
			FirearmConfig cfg = this.getResourceState().getResourceSet().getConfig();
			if(par1 > cfg.yaw[0])
			{
				par1 = cfg.yaw[0];
			}

			if(par1 < cfg.yaw[1])
			{
				par1 = cfg.yaw[1];
			}
			this.getDataManager().set(B_YAW, par1);
		}
	}

	public float getBarrelPitch()
	{
		return this.getDataManager().get(B_PITCH);
	}

	public void setBarrelPitch(float par1)
	{
		if(!this.getEntityWorld().isRemote)
		{
			par1 = MathHelper.wrapDegrees(par1);
			FirearmConfig cfg = this.getResourceState().getResourceSet().getConfig();
			if(par1 > -cfg.pitch[1])
			{
				par1 = -cfg.pitch[1];
			}

			if(par1 < -cfg.pitch[0])
			{
				par1 = -cfg.pitch[0];
			}
			this.getDataManager().set(B_PITCH, par1);
		}
	}

	public void setAmmo(int par1)
	{
		this.getDataManager().set(AMMO, Integer.valueOf(par1));
	}

	public int hasAmmo()
	{
		return this.getDataManager().get(AMMO);
	}

	public int getAmmoType(int par1)
	{
		int i0 = par1 / 4;
		if(par1 % 4 == 0 && (i0 == 0 || i0 == 5))
		{
			return i0;
		}
		return -1;
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.FIREARM;
	}

	@Override
	public void updateResourceState()
	{
		super.updateResourceState();
		this.setBarrelYaw(0.0F);
		this.setBarrelPitch(0.0F);
	}

	@Override
	protected ItemStack getItem() {
		return new ItemStack(RTMItem.itemCargo, 1, 1);
	}
}