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

package jp.ngt.rtm.entity;

import java.util.List;

import jp.ngt.rtm.block.tileentity.TileEntityMovingMachine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMMBoundingBox extends Entity
{
	private static final DataParameter<Rotations> AABB_F = EntityDataManager.<Rotations>createKey(EntityMMBoundingBox.class, DataSerializers.ROTATIONS);
	private static final DataParameter<Rotations> AABB_L = EntityDataManager.<Rotations>createKey(EntityMMBoundingBox.class, DataSerializers.ROTATIONS);

	private TileEntityMovingMachine movingMachine;
	private boolean checkCollision;
	private AxisAlignedBB aabb = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	public EntityMMBoundingBox(World world)
	{
		super(world);
		this.setSize(1.0F, 1.0F);
	}

	public EntityMMBoundingBox(World world, TileEntityMovingMachine tile, boolean p3)
	{
		this(world);
		this.movingMachine = tile;
		this.checkCollision = p3;
	}

	@Override
	protected void entityInit()
	{
		this.getDataManager().register(AABB_F, new Rotations(-1.0F, 0.0F, 0.0F));
		this.getDataManager().register(AABB_L, new Rotations(0.0F, 0.0F, 0.0F));
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1)
    {
		return (par1 instanceof EntityMMBoundingBox) ? null : par1.getEntityBoundingBox();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

	@Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}

	public void setAABB(AxisAlignedBB par1)
	{
		this.setEntityBoundingBox(par1);
		this.getDataManager().set(AABB_F, new Rotations((float)(par1.minX - 0.5F), (float)(par1.minY), (float)(par1.minZ - 0.5F)));
		this.getDataManager().set(AABB_L, new Rotations((float)(par1.maxX - 0.5F), (float)(par1.maxY), (float)(par1.maxZ - 0.5F)));
	}

	/**オフセットなしのAABB(元ブロックのサイズ由来)*/
	private AxisAlignedBB getAABB()
	{
		if(this.getDataManager() == null){return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);}
		Rotations roF = this.getDataManager().get(AABB_F);
		Rotations roL = this.getDataManager().get(AABB_L);
		if(roF.getX() == -1.0F){return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);}
		return new AxisAlignedBB(roF.getX(), roF.getY(), roF.getZ(), roL.getX(), roL.getY(), roL.getZ());
	}

	@Override
    public void onUpdate()
    {
		if(!this.world.isRemote)
		{
			if(this.movingMachine == null)
			{
				this.setDead();
			}
		}
    }

	private void applyCollisionToEntities(double dx, double dy, double dz)
	{
		this.aabb = this.getAABB();
		double d0 = 0.41999998688697815D + (dy < 0.0D ? -dy : 0.0D);
		this.aabb = this.aabb.offset(this.posX, this.posY + d0, this.posZ);

		List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.aabb);
        for(int i = 0; i < list.size(); ++i)
        {
        	Entity entity = (Entity)list.get(i);
        	if(!(entity instanceof EntityMMBoundingBox))
        	{
        		this.moveEntity(entity, dx, dy, dz);
        	}
        }
	}

	private void moveEntity(Entity entity, double dx, double dy, double dz)
	{
		AxisAlignedBB entityBB = entity.getEntityBoundingBox();
		AxisAlignedBB myBB = this.getEntityBoundingBox();
		double y0 = myBB.maxY - entityBB.minY;
		boolean flag = false;
		if(this.inY(entity, dy))//接してるor上部に入り込んでる
		{
			if(this.onY(entity, dy))
			{
				if(this.inXAndZ(entity))//XZの範囲内に居る時のみ処理
				{
					double y1 = myBB.maxY - (entityBB.minY + dy);
					if(y1 != 0.0D)
					{
						dy += y1;
					}
					entity.fallDistance = 0.0F;
					entity.motionY = 0.0D;
					entity.onGround = true;//falseだとXZ移動がぬるっとする
					flag = true;
				}
			}
			else//上に乗ってないものとする
			{
				if(this.inXOrZ(entity))
				{
					dy = 0.0D;
					dx = myBB.calculateXOffset(entityBB, dx);
					dz = myBB.calculateZOffset(entityBB, dz);
					flag = true;
				}
			}

			if(flag)
			{
				double d0 = entityBB.minY + dy + (double)entity.getYOffset();

				/*if(entity.canBePushed() || (entity instanceof EntityPlayer))
				{
					entity.moveEntity(dx, dy, dz);

				}
				else
				{
					entity.setPosition(entity.posX + dx, d0, entity.posZ + dz);
				}*/

				if(this.world.isRemote)
				{
					//Playerは鯖側での位置更新は無効化される?
					//C03PacketPlayer,EntityClientPlayerMP,NetHandlerPlayServer.processPlayer(C03)
					//if(entity instanceof EntityPlayer)
					{
						entity.setPosition(entity.posX + dx, d0, entity.posZ + dz);
					}
				}
				else
				{
					if(!(entity instanceof EntityPlayer))
					{
						entity.setPosition(entity.posX + dx, d0, entity.posZ + dz);
						entity.velocityChanged = true;
					}
				}
			}
		}
	}

	private boolean inY(Entity entity, double moveY)
	{
		AxisAlignedBB entityBB = entity.getEntityBoundingBox();
		AxisAlignedBB myBB = this.getEntityBoundingBox();
		if(moveY > 0.0D)
		{
			return entityBB.minY <= myBB.maxY && entityBB.maxY > myBB.minY;
		}
		else
		{
			return entityBB.minY <= myBB.maxY - moveY && entityBB.maxY > myBB.minY;
		}
	}

	private boolean onY(Entity entity, double moveY)
	{
		AxisAlignedBB entityBB = entity.getEntityBoundingBox();
		AxisAlignedBB myBB = this.getEntityBoundingBox();
		double d0 = 0.21;
		if(moveY > 0.0D)
		{
			return entityBB.minY >= myBB.maxY - moveY - d0 && entityBB.minY <= myBB.maxY;
		}
		else
		{
			return entityBB.minY >= myBB.maxY - d0 && entityBB.minY <= myBB.maxY - moveY;
		}
	}

	private boolean inXAndZ(Entity entity)
	{
		return entity.posX >= this.aabb.minX && entity.posX < this.aabb.maxX
				&& entity.posZ >= this.aabb.minZ && entity.posZ < this.aabb.maxZ;
	}

	private boolean inXOrZ(Entity entity)
	{
		return (entity.posX >= this.aabb.minX && entity.posX < this.aabb.maxX)
				|| (entity.posZ >= this.aabb.minZ && entity.posZ < this.aabb.maxZ);
	}

	@Override
    public boolean attackEntityFrom(DamageSource damage, float par2)
    {
		return false;
    }

	public void moveMM(double x, double y, double z)
	{
		//this.setPosition(this.posX + x, this.posY + y, this.posZ + z);
		this.applyCollisionToEntities(x, y, z);
		this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
	}

	@Override
	public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.setEntityBoundingBox(this.getAABB().offset(x, y, z));
    }

	@Override
	public void move(MoverType type, double x, double y, double z){}

	@Override
	public void addVelocity(double par1, double par3, double par5){}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
		this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
		this.moveMM(0.0D, 0.0D, 0.0D);
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int count, boolean pb){}

	@SideOnly(Side.CLIENT)
	public static void handleMMMovement(World world, int[] ids, double moveX, double moveY, double moveZ)
	{
		/*for(int i = 0; i < ids.length; ++i)
		{
			EntityMMBoundingBox entity = (EntityMMBoundingBox)world.getEntityByID(ids[i]);
			entity.moveMM(moveX, moveY, moveZ);
		}*/

		for(int pass = 0; pass < 2; ++pass)
		{
			for(int i = 0; i < ids.length; ++i)
			{
				EntityMMBoundingBox entity = (EntityMMBoundingBox)world.getEntityByID(ids[i]);
				if(entity != null)
				{
					switch(pass)
					{
					case 0: entity.setPosition(entity.posX + moveX, entity.posY + moveY, entity.posZ + moveZ);break;
					case 1: entity.moveMM(moveX, moveY, moveZ);break;
					}
				}
			}
		}
	}
}