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

import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import net.minecraft.util.math.MathHelper;

public class FirearmController
{
	private float yawSpeed = 5.0F;
	private float pitchSpeed = 5.0F;
	private float yawDif;
	private float pitchDif;
	private float prevYaw;
	private float prevPitch;

	public void onUpdate(EntityArtillery entity)
	{
		if(this.yawDif != 0.0F)
		{
			float dif = MathHelper.wrapDegrees(this.prevYaw + this.yawDif - entity.getBarrelYaw());
			if(Math.abs(dif) > this.yawSpeed)
			{
				float f0 = this.yawSpeed * ((dif > 0.0F) ? 1.0F : -1.0F);
				entity.setBarrelYaw(entity.getBarrelYaw() + f0);
			}
			else
			{
				entity.setBarrelYaw(this.prevYaw + this.yawDif);
				this.yawDif = 0.0F;
			}
		}

		if(this.pitchDif != 0.0F)
		{
			float dif = MathHelper.wrapDegrees(this.prevPitch + this.pitchDif - entity.getBarrelPitch());
			if(Math.abs(dif) > this.pitchSpeed)
			{
				float f0 = this.pitchSpeed * ((dif > 0.0F) ? 1.0F : -1.0F);
				entity.setBarrelPitch(entity.getBarrelPitch() + f0);
			}
			else
			{
				entity.setBarrelPitch(this.prevPitch + this.pitchDif);
				this.pitchDif = 0.0F;
			}
		}
	}

	public void addYaw(EntityArtillery entity, float par1)
	{
		this.yawDif = par1;
		this.prevYaw = entity.getBarrelYaw();
	}

	public void addPitch(EntityArtillery entity, float par1)
	{
		this.pitchDif = par1;
		this.prevPitch = entity.getBarrelPitch();
	}
}