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

import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityMechanism;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityLift extends EntityVehicle
{
	private TileEntityMechanism currentPulley;
	//同期なし
	private BlockPos currentPulleyPos = new BlockPos(0, 0, 0);
	//同期なし
	private float totalMove;
	//同期なし
	private long prevTime;

	public EntityLift(World world)
	{
		super(world);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		int[] ia = nbt.getIntArray("pulley_pos");
		if(ia.length == 3)
		{
			this.currentPulleyPos = new BlockPos(ia[0], ia[1], ia[2]);
		}
		this.totalMove = nbt.getFloat("total_move");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		int[] ia = {this.currentPulleyPos.getX(), this.currentPulleyPos.getY(), this.currentPulleyPos.getZ()};
		nbt.setIntArray("pulley_pos", ia);
		nbt.setFloat("total_move", this.totalMove);
	}

	@Override
	protected boolean shouldUpdateMotion()
	{
		return true;
	}

	@Override
	protected void updateMotion(EntityLivingBase entity, float moveStrafe, float moveForward)
	{
		;
	}

	@Override
	protected void updateMovement()
	{
		if(this.currentPulley == null)
		{
			TileEntity tileEntity = this.getEntityWorld().getTileEntity(this.currentPulleyPos);
			if(tileEntity instanceof TileEntityMechanism)
			{
				this.currentPulley = (TileEntityMechanism)tileEntity;
			}
		}

		if(this.currentPulley != null)
		{
			long time = System.currentTimeMillis();
			int timeDif = (this.prevTime <= 0) ? 0 : (int)(time - this.prevTime);//前回値未設定時はDif算出しない
			this.prevTime = time;
			LiftMotion motion = this.currentPulley.getMotion(this, this.totalMove, timeDif);
			this.posX = motion.pos.getX();
			this.posY = motion.pos.getY();
			this.posZ = motion.pos.getZ();
			this.rotationYaw = motion.yaw;
			this.rotationPitch = motion.pitch;
			this.totalMove = motion.move;
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(this.posX, this.posY, this.posZ));

			if(motion.mecha != this.currentPulley)
			{
				this.setMecha(motion.mecha);
			}
		}
	}

	@Override
	protected void updateFallState()
	{
		;
	}

	@Override
	protected Vec3 getMotionVec()
	{
		return Vec3.ZERO;
	}

	public void setMecha(TileEntityMechanism mecha)
	{
		this.currentPulley = mecha;
		this.currentPulleyPos = mecha.getPos();
	}

	@Override
	protected ItemStack getVehicleItem()
	{
		return new ItemStack(RTMItem.itemVehicle, 1, VehicleType.LIFT.id);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.VEHICLE_LIFT;
	}
}
