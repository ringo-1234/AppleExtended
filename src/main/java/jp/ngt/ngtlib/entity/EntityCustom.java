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

package jp.ngt.ngtlib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public abstract class EntityCustom extends Entity
{
	public EntityCustom(World world)
	{
		super(world);
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player)
    {
		this.syncData();//モデルデータ同期
    }

	/**DataManagerを使わないデータの同期*/
	public abstract void syncData();

	public Entity getFirstPassenger()
	{
		if(this.isBeingRidden())
		{
			return this.getPassengers().get(0);
		}
		return null;
	}

	public double getMotionX()
	{
		return this.motionX;
	}

	public void setMotionX(double par1)
	{
		this.motionX = par1;
	}

	public double getMotionY()
	{
		return this.motionY;
	}

	public void setMotionY(double par1)
	{
		this.motionY = par1;
	}

	public double getMotionZ()
	{
		return this.motionZ;
	}

	public void setMotionZ(double par1)
	{
		this.motionZ = par1;
	}
}