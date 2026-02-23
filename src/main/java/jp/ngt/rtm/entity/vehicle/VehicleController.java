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

public final class VehicleController
{
	private static final float MOVE_STRAFE = 1.0F;
	private static final float MOVE_FORWARD = 1.0F;

	private final double[] prevPos = new double[3];
	private double moveDistance;

	private float prevYaw;
	private float rotation;

	/**無人の場合のみ更新*/
	public void onUpdate(EntityVehicle vehicle)
	{
		float moveStrafe = 0.0F;
		float moveForward = 0.0F;

		if(this.moveDistance > 0.0D)
		{
			double d0 = vehicle.getDistance(this.prevPos[0], this.prevPos[1], this.prevPos[2]);
			if(d0 < this.moveDistance)
			{
				moveForward = MOVE_FORWARD * (float)(1.0D - (d0 / this.moveDistance));
			}
			else
			{
				this.moveDistance = 0.0D;
			}
		}

		if(this.rotation != 0.0F)
		{
			float f0 = vehicle.rotationYaw - this.prevYaw;
			if(Math.abs(this.rotation) > Math.abs(f0))
			{
				moveStrafe = MOVE_STRAFE;
			}
			else
			{
				this.rotation = 0.0F;
			}
		}

		vehicle.updateMotion(null, moveStrafe, moveForward);
	}

	public void setMoveDistance(EntityVehicle vehicle, double par1)
	{
		this.prevPos[0] = vehicle.posX;
		this.prevPos[1] = vehicle.posY;
		this.prevPos[2] = vehicle.posZ;
		this.moveDistance = par1;
	}

	public void addYaw(EntityVehicle vehicle, float par1)
	{
		this.prevYaw = vehicle.rotationYaw;
		this.rotation = par1;
	}
}