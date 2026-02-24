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

package jp.ngt.rtm.entity.fluid;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.item.ItemBucketLiquid;
import jp.ngt.rtm.item.ItemPaddle;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class EntityFluid extends Entity
{
	private static final DataParameter<String> TYPE = EntityDataManager.<String>createKey(EntityFluid.class, DataSerializers.STRING);
	private static final DataParameter<Float> TEMP = EntityDataManager.<Float>createKey(EntityFluid.class, DataSerializers.FLOAT);

	public static final float R = 0.125F;
	public static final float SIZE = R * 2.0F;
	public static final float METABALL_RANGE = SIZE * 4.0F;
	private static final float METABALL_RANGE_SQ = METABALL_RANGE * METABALL_RANGE;
	private static AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(
			R * -0.5D, R * -0.5D, R * -0.5D, R * 0.5D, R * 0.5D, R * 0.5D);
	private static final Item ITEM_IRON_ORE = Item.getItemFromBlock(Blocks.IRON_ORE);

	/**メタボール計算用*/
	public final List<EntityFluid> nearFluids = new ArrayList<>();

	/**Side.CLIENT 色グラデーション用、最下層のfluidとの位置差分*/
	public float posDif;

	private int counter;
	private int airCount;

	public FluidVertexHolder fluidVtx;

	public EntityFluid(World world)
	{
		super(world);
		this.setSize(SIZE, SIZE);
		if(world.isRemote)
		{
			this.fluidVtx = new FluidVertexHolder();
		}
	}

	@Override
	protected void entityInit()
	{
		this.getDataManager().register(TYPE, "");
		this.getDataManager().register(TEMP, Float.valueOf(0.0F));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox()
    {
		return COLLISION_BOX.offset(this.posX, this.posY + R, this.posZ);
    }

	@Override
	public boolean canBePushed()
    {
        return false;
    }

	@Override
	public boolean canBeCollidedWith()
    {
        return !this.isDead;//右クリックの対象にする
    }

	@Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
	public boolean canBeAttackedWithItem()
    {
        return true;
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		this.setFluidType(nbt.getString("type"));
		this.setTemperture(nbt.getFloat("temperture"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setString("type", this.getFluidType().toString());
		nbt.setFloat("temperture", this.getTemperature());
	}

	@Override
	public void onUpdate()
    {
        super.onUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY -= 0.03D;//重力

        this.calcFluidMovement();
        this.pushOutOfBlocks(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        this.fixPosByBlockCollision();
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        float f = 0.98F;
        if(this.onGround)
        {
        	//摩擦処理
            BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            IBlockState underState = this.world.getBlockState(underPos);
            f *= underState.getBlock().getSlipperiness(underState, this.world, underPos, this);
        }
        this.motionX *= (double)f;
        this.motionY *= 0.98D;
        this.motionZ *= (double)f;

        if(this.onGround)
        {
            this.motionY *= -0.9D;
        }

        this.updateFluidState();
    }

	protected void updateFluidState()
	{
		float temp = this.getTemperature();
		if(temp >= 500.0F)
		{
			++this.counter;
		}

		if(this.world.isRemote)
        {
			float f0 = 1.0F - this.getNormalizedTemperture();
        	if(this.getFluidType() == FluidType.COKE)
        	{
        		int rand = 100 + (int)(100 * f0);
        		if(this.getTemperature() > 200.0F && this.world.rand.nextInt(rand) == 0)
            	{
            		this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
            				this.posX, this.posY + SIZE, this.posZ, 0.0D, 0.125D, 0.0D);
            	}
        	}
        	else if(this.getFluidType().type == FluidType.Type.LIQUID)
        	{
        		int rand = 250 + (int)(250 * f0);
        		if(this.getTemperature() > 300.0F && this.world.rand.nextInt(rand) == 0)
            	{
            		this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
            				this.posX, this.posY + SIZE, this.posZ, 0.0D, 0.125D, 0.0D);
            	}
        	}

        	this.fluidVtx.update(this);
        }
        else//server
        {
        	if(temp > 0.0F)
        	{
        		if(this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.WATER)
                {
        			temp -= 20.0F;
                }
        		else if(this.getFluidType() != FluidType.COKE && this.world.rand.nextInt(10) == 0)
        		{
        			temp -= 1.0F;
        		}

        		if(temp != this.getTemperature())
        		{
        			this.setTemperture(temp);
        		}
        	}

        	int max = this.getFluidType().life;
        	if(max > 0 && this.counter >= max)
        	{
        		this.setDead();
        	}
        }
	}

	private void calcFluidMovement()
	{
		this.nearFluids.clear();
		this.posDif = 0.0F;

		final double repRange = SIZE * 1.2D;
		final double repulsion = this.getFluidType().viscosity * this.getNormalizedTemperture();//斥力
		double px = this.posX;
		double py = this.posY;
		double pz = this.posZ;
		double maxDif = 0.0D;
		double accumDif = 0.0D;

		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this,
				this.getEntityBoundingBox().grow(METABALL_RANGE, METABALL_RANGE, METABALL_RANGE));
		for(Entity entity2 : list)//反発処理
		{
			if(entity2.isDead){continue;}

			if(entity2 instanceof EntityFluid)
			{
				EntityFluid entity = (EntityFluid)entity2;
				if(entity == this){continue;}

				double difX = px - entity.posX;
				double difY = py - entity.posY;
				double difZ = pz - entity.posZ;
				double distanceSq = this.getDistanceSq(entity);

				if(distanceSq < repRange * repRange)
				{
					double distance = NGTMath.firstSqrt(distanceSq);
					double d0 = (repRange - distance) * repulsion;
					px += (difX / distance) * d0;
					py += (difY / distance) * d0;
					pz += (difZ / distance) * d0;
				}

				if(this.world.isRemote)
				{
					if(distanceSq <= METABALL_RANGE_SQ && this.getFluidType().type != FluidType.Type.SOLID)
					{
						this.nearFluids.add(entity);
						if(!this.onGround && entity.posY < this.posY)
						{
							double dif = this.posY - entity.posY;
							if(dif > maxDif)
							{
								maxDif = dif;
								accumDif = entity.posDif;
							}
						}
					}
				}
				else
				{
					if(distanceSq <= METABALL_RANGE_SQ)
					{
						float temp1 = this.getTemperature();
						float temp2 = entity.getTemperature();
						if(temp1 > temp2)
						{
							float f0 = (temp1 - temp2) * this.getFluidType().thermalConductivity;
							temp1 -= f0;
							temp2 += f0;
						}
						else if(temp2 > temp1)
						{
							float f0 = (temp2 - temp1) * entity.getFluidType().thermalConductivity;
							temp2 -= f0;
							temp1 += f0;
						}
						this.setTemperture(temp1);
						entity.setTemperture(temp2);
					}
				}
				this.onFluidCollide(entity);
			}
			else
			{
				this.onEntityCollideFluid(entity2);
			}
		}

		if(this.world.isRemote)
		{
			this.posDif = (float)(accumDif + maxDif);
		}

		this.motionX += (px - this.posX);
		//this.motionY += (py - this.posY);
		this.motionZ += (pz - this.posZ);
	}

	protected void onFluidCollide(EntityFluid fluid)
	{
		if(!this.world.isRemote)
		{
			if(this.getDistanceSq(fluid) <= METABALL_RANGE_SQ)
			{
				if(fluid.getFluidType() == FluidType.IRON_ORE && fluid.getTemperature() >= FluidType.PIG_IRON.meltingPoint)
				{
					int countInc = this.world.rand.nextInt(2) + 2;
					ItemBucketLiquid.setFluid(this.world, fluid.posX, fluid.posY, fluid.posZ,
							FluidType.PIG_IRON, countInc, this.getTemperature());
					fluid.setFluidType(FluidType.SLAG);
					fluid.setTemperture(this.getTemperature());
					this.counter += 2000;
				}
			}
		}
	}

	protected void onEntityCollideFluid(Entity entity)
	{
		if(!this.world.isRemote)
		{
			if(this.getDistanceSq(entity) <= SIZE * SIZE)
			{
				boolean burnFlag = this.getTemperature() > 300.0F;
				if(entity instanceof EntityItem)
				{
					burnFlag = !this.addItem((EntityItem)entity);
				}

				if(burnFlag && this.world.rand.nextInt(5) == 0)
				{
					entity.attackEntityFrom(DamageSource.LAVA, 2.0F * this.getNormalizedTemperture());
					entity.setFire(5);
				}
			}
		}
	}

	private boolean addItem(EntityItem entity)
	{
		ItemStack stack = entity.getItem();

		if(stack.getItem() == RTMItem.coke)
		{
			ItemBucketLiquid.setFluid(this.world, entity.posX, entity.posY, entity.posZ,
					FluidType.COKE, stack.getCount(), 0.0F);
			entity.setDead();
			return true;
		}
		else if(stack.getItem() == ITEM_IRON_ORE)
		{
			ItemBucketLiquid.setFluid(this.world, entity.posX, entity.posY, entity.posZ,
					FluidType.IRON_ORE, stack.getCount(), 0.0F);
			entity.setDead();
			return true;
		}

		return false;
	}

	protected void fixPosByBlockCollision()
	{
		this.airCount = 0;
		double newX = this.posX + this.motionX;
		double newY = this.posY + (this.onGround ? 0.0F : this.motionY);
		double newZ = this.posZ + this.motionZ;
		int bx = MathHelper.floor(newX);
		int by = MathHelper.floor(newY);
		int bz = MathHelper.floor(newZ);
		for(EnumFacing face : EnumFacing.VALUES)
		{
			int x = bx + face.getDirectionVec().getX();
			int y = by + face.getDirectionVec().getY();
			int z = bz + face.getDirectionVec().getZ();
			boolean isAir = BlockUtil.isAir(this.world, x, y, z);
			this.airCount += isAir ? 1 : 0;
			if(isAir && face == EnumFacing.DOWN)//中心が接地面からはみ出ていれば落下
			{
				AxisAlignedBB aabb = this.getEntityBoundingBox();
				int minX = MathHelper.floor(aabb.minX);
				int maxX = MathHelper.floor(aabb.maxX);
				int minZ = MathHelper.floor(aabb.minZ);
				int maxZ = MathHelper.floor(aabb.maxZ);
				double d0 = 0.25D;
				if(minX < bx)
				{
					this.motionX += ((double)bx - aabb.minX) * d0;
				}
				else if(maxX > bx)
				{
					this.motionX -= (aabb.maxX - (double)(bx + 1)) * d0;
				}

				if(minZ < bz)
				{
					this.motionZ += ((double)bz - aabb.minZ) * d0;
				}
				else if(maxZ > bz)
				{
					this.motionZ -= (aabb.maxZ - (double)(bz + 1)) * d0;
				}
			}
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
		float temp = this.getTemperature();

		if(NGTUtil.isEquippedItem(player, Items.BUCKET) || NGTUtil.isEquippedItem(player, RTMItem.bucketLiquid))
		{
			if(temp > this.getFluidType().meltingPoint)
			{
				return ItemBucketLiquid.pickupFluid(player, this);
			}
		}
		else if(NGTUtil.isEquippedItem(player, RTMItem.bellows))
		{
			if(!this.world.isRemote)
			{
				this.setTemperture(temp - 10.0F);
			}
			return true;
		}
		else if(NGTUtil.isEquippedItem(player, RTMItem.paddle))
		{
			if(!this.world.isRemote)
			{
				ItemPaddle.pushPull(player, this, -1.0F);
			}
			return true;
		}
		else if(NGTUtil.isEquippedItem(player, Items.FLINT_AND_STEEL))
		{
			if(!this.world.isRemote && this.getFluidType() == FluidType.COKE)
			{
				if(this.getTemperature() < 500.0F)
				{
					this.setTemperture(500.0F);
				}
			}
			return true;
		}
		return false;
    }

	@Override
    public boolean attackEntityFrom(DamageSource source, float strength)
    {
		if(!this.world.isRemote && (source.getImmediateSource() instanceof EntityPlayer))
		{
			EntityPlayer player = (EntityPlayer)source.getTrueSource();
			Item item = null;
			if(NGTUtil.isEquippedItem(player, RTMItem.iron_hacksaw))
			{
				if(this.getTemperature() <= 100.0F)
				{
					if(this.getFluidType() == FluidType.STEEL)
					{
						item = RTMItem.steel_ingot;
					}
					else if(this.getFluidType() == FluidType.PIG_IRON)
					{
						item = Items.IRON_INGOT;
					}
					else if(this.getFluidType() == FluidType.SLAG)
					{
						item = Item.getItemFromBlock(Blocks.COBBLESTONE);
					}
					else if(this.getFluidType() == FluidType.IRON_ORE)
					{
						item = Item.getItemFromBlock(Blocks.IRON_ORE);
					}
				}
				else
				{
					NGTLog.showChatMessage(new TextComponentString(
							String.format("Temperture is too hot ! (%5.1f)", this.getTemperature())));
					return true;
				}
			}
			else if(NGTUtil.isEquippedItem(player, RTMItem.paddle))
			{
				ItemPaddle.pushPull(player, this, 1.0F);
				return true;
			}

			if(item == null)
			{
				if(this.getFluidType() == FluidType.COKE)
				{
					item = RTMItem.coke;
				}
			}

			if(item != null)
			{
				player.getHeldItemMainhand().damageItem(1, player);
				player.entityDropItem(new ItemStack(item, 1, 0), 1.0F);
				this.setDead();
			}
		}
		return true;
    }

	@Override
	public boolean shouldRenderInPass(int pass)
    {
        return pass == 0 || pass == 1;
    }

	public float getNormalizedLife()
	{
		int max = this.getFluidType().life;
		return max > 0 ? ((float)(max - this.counter) / (float)max) : 1.0F;
	}

	public FluidType getFluidType()
	{
		String s = this.getDataManager().get(TYPE);
		return FluidType.valueOf(s);
	}

	private void setFluidType(String type)
	{
		if(type == null || type.isEmpty())
		{
			type = FluidType.STEEL.toString();
		}
		this.getDataManager().set(TYPE, type);
	}

	public void setFluidType(FluidType type)
	{
		this.setFluidType(type.toString());
		this.counter = 0;
	}

	public float getTemperature()
	{
		return this.getDataManager().get(TEMP);
	}

	public float getNormalizedTemperture()
	{
		float temp = this.getTemperature();
		if(temp > this.getFluidType().meltingPoint)
		{
			return 1.0F;
		}
		return temp / this.getFluidType().meltingPoint;
	}

	public void setTemperture(float f)
	{
		f = f < 0.0F ? 0.0F : f;
		this.getDataManager().set(TEMP, f);
	}

	public int countAir()
	{
		return this.airCount;
	}
}
