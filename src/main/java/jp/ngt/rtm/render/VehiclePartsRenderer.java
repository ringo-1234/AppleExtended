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

package jp.ngt.rtm.render;

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VehiclePartsRenderer extends EntityPartsRenderer<ModelSetVehicleBase>
{
	/**台車の場合はfalse*/
	private boolean isvehicle;

	public VehiclePartsRenderer(String... par1)
	{
		super(par1);

		if(par1.length >= 1)
		{
			if("true".equals(par1[0]))
			{
				this.isvehicle = true;
			}
			else if("false".equals(par1[0]))
			{
				this.isvehicle = false;
			}
		}
	}

	@Override
	public void init(ModelSetVehicleBase par1, ModelObject par2)
	{
		super.init(par1, par2);
	}

	public float getWheelRotationR(Entity entity)
	{
		return this.getWheelRotation(entity, 1);
	}

	public float getWheelRotationL(Entity entity)
	{
		return getWheelRotation(entity, 0);
	}

	/**@param id 0:L, 1:R*/
	private float getWheelRotation(Entity entity, int id)
	{
		if(!this.isvehicle && entity instanceof EntityBogie)
		{
			EntityBogie bogie = (EntityBogie)entity;
			EntityTrainBase train = bogie.getTrain();
			if(train != null)
			{
				float f0 = (id == 0) ? train.wheelRotationL : train.wheelRotationR;
				return f0 * (bogie.getBogieId() == 0 ? 1.0F : -1.0F);
			}
		}
		else if(entity instanceof EntityVehicleBase)
		{
			EntityVehicleBase vehicle = (EntityVehicleBase)entity;
			return (id == 0) ? vehicle.wheelRotationL : vehicle.wheelRotationR;
		}
		return 0.0F;
	}

	public float getDoorMovementR(Entity entity)
	{
		return this.getDoorMovement(entity, 1);
	}

	public float getDoorMovementL(Entity entity)
	{
		return this.getDoorMovement(entity, 0);
	}

	/**@param id 0:L, 1:R*/
	private float getDoorMovement(Entity entity, int id)
	{
		if(this.isvehicle && entity instanceof EntityVehicleBase)
		{
			EntityVehicleBase vehicle = (EntityVehicleBase)entity;
			float f0 = (id == 0) ? vehicle.doorMoveL : vehicle.doorMoveR;
			return f0 / (float)EntityVehicleBase.MAX_DOOR_MOVE;
		}
		return 0.0F;
	}

	public float getPantographMovementFront(Entity entity)
	{
		return this.getPantographMovement(entity, 0);
	}

	public float getPantographMovementBack(Entity entity)
	{
		return this.getPantographMovement(entity, 1);
	}

	/**@param id 0:Front, 1:Back*/
	private float getPantographMovement(Entity entity, int id)
	{
		if(this.isvehicle && entity instanceof EntityVehicleBase)
		{
			EntityVehicleBase vehicle = (EntityVehicleBase)entity;
			float f0 = (id == 0) ? vehicle.pantograph_F : vehicle.pantograph_B;
			return f0 / (float)EntityVehicleBase.MAX_PANTOGRAPH_MOVE;
		}
		return 0.0F;
	}

	public float getPlayerYaw(Entity entity)
	{
		if(this.isRidden(entity))
		{
			Entity rider = entity.getPassengers().get(0);
			return -rider.rotationYaw;
		}
		return 0.0F;
	}

	/**当ClientのプレーヤーがこのEntityに乗ってるのと同一か*/
	public boolean isRidden(Entity entity)
	{
		if(entity != null && entity.isBeingRidden())
		{
			boolean isHostPlayer = NGTUtilClient.getMinecraft().player == entity.getPassengers().get(0);
			return isHostPlayer;
		}
		return false;
	}
}