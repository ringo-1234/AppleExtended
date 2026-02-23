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

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityMeltedMetalFX extends Particle
{
	public EntityMeltedMetalFX(World world, double x, double y, double z, double mX, double mY, double mZ)
	{
		super(world, x, y, z, mX, mY, mZ);
		this.motionX = mX + (Math.random() * 0.01D) - 0.005D;
        this.motionY = 0.0D;
        this.motionZ = mZ + (Math.random() * 0.01D) - 0.005D;
		this.particleMaxAge = (int)(15.0D / (Math.random() * 0.8D + 0.2D));
		this.particleScale = 0.8F;
        //this.noClip = false;//1.8以前
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_)
    {
		int i = (int)(255.0F * (1.0F - ((float)this.particleAge / (float)this.particleMaxAge)));
		return (i << 16) + (i << 8) + i;
    }

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}

		this.motionY -= 0.05D;

		if(this.onGround)
		{
			this.motionY *= -(10.0D + Math.random() * 4.0D * (1.0F - ((float)this.particleAge / (float)this.particleMaxAge)));
			this.motionX += (Math.random() * 0.08D) - 0.04D;
			this.motionZ += (Math.random() * 0.08D) - 0.04D;
		}

		this.move(this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public int getFXLayer()
	{
		return 2;
	}
}