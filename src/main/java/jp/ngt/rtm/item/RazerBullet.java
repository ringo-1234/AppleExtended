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

package jp.ngt.rtm.item;

import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.event.TickProcessEntry;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.item.ItemGun.GunType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RazerBullet implements TickProcessEntry
{
	protected static final int RANGE = 4;
	protected static final float SPEED = 2.0F;
	protected static final int MAX_AGE = (int)((float)256 / SPEED);

	protected final EntityLivingBase shooter;
	protected double posX, posY, posZ;
	protected double motionX, motionY, motionZ;
	protected int age;

	public RazerBullet(EntityPlayer shooter)
	{
		this.shooter = shooter;
		this.posX = shooter.posX;
		this.posY = shooter.posY + (double)shooter.getEyeHeight();
		this.posZ = shooter.posZ;
        float yawRad = NGTMath.toRadians(shooter.rotationYaw);
        float pitchRad = NGTMath.toRadians(shooter.rotationPitch);
        this.motionX = (double)(-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad)) * SPEED;
        this.motionZ = (double)(MathHelper.cos(yawRad) * MathHelper.cos(pitchRad)) * SPEED;
        this.motionY = (double)(-MathHelper.sin(pitchRad)) * SPEED;

        double recoilCoe = 0.01D;
        recoilCoe *= GunType.razer_gun.speed / SPEED;
    	this.shooter.motionX -= this.motionX * recoilCoe;
    	this.shooter.motionY -= this.motionY * recoilCoe;
    	this.shooter.motionZ -= this.motionZ * recoilCoe;
    	this.shooter.velocityChanged = true;
	}

	@Override
	public boolean process(World world)
	{
		/*this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		this.age++;
		this.doEffect(world);
		return this.age > MAX_AGE || this.posY < 0.0D || this.posY > 256.0D;*/

		//1tickで全処理する方式
		for(int i = 0; i < MAX_AGE; ++i)
		{
			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			this.age++;
			this.deleteBlocks(this.shooter.getEntityWorld());
			this.deleteEntities(this.shooter.getEntityWorld());
			if(this.posY < 0.0D || this.posY > 256.0D)
			{
				return true;
			}
		}
		return true;
	}

	protected void deleteBlocks(World world)
	{
		int blockX = NGTMath.floor(this.posX);
		int blockY = NGTMath.floor(this.posY);
		int blockZ = NGTMath.floor(this.posZ);
		for(int i = -RANGE; i < RANGE; ++i)
		{
			for(int j = -RANGE; j < RANGE; ++j)
			{
				for(int k = -RANGE; k < RANGE; ++k)
				{
					int len2 = i * i + j * j + k * k;
					int rng2 = RANGE * RANGE;
					if(len2 <= rng2)
					{
						BlockPos pos = new BlockPos(blockX + i, blockY + j, blockZ + k);
						Block block = BlockUtil.getBlock(world, pos);
						if(block != Blocks.AIR && block != Blocks.BEDROCK)
						{
							if(len2 >= rng2 - 6)
							{
								BlockUtil.setBlock(world, pos, Blocks.MAGMA, 0, 3);
							}
							else
							{
								BlockUtil.setBlock(world, pos, Blocks.AIR, 0, 3);
							}
						}
					}
				}
			}
		}
	}

	protected void deleteEntities(World world)
	{
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this.shooter,
				new AxisAlignedBB(this.posX - RANGE, this.posY - RANGE, this.posZ - RANGE, this.posX + RANGE, this.posY + RANGE, this.posZ + RANGE));
		double rng2 = RANGE * RANGE;
		for(Entity entity : list)
		{
			if(entity.getDistanceSq(this.posX, this.posY, this.posZ) <= rng2)
			{
				entity.attackEntityFrom(DamageSource.MAGIC, 10000);
			}
		}
	}
}