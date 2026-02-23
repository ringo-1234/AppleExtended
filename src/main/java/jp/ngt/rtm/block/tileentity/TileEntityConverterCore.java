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

package jp.ngt.rtm.block.tileentity;

import java.util.Random;

import jp.ngt.ngtlib.block.BlockLiquidBase;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMFluid;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityConverterCore extends TileEntityConverter
{
	private static final int Max_Capacity = 720;
	private byte direction;
	private int capacity;
	/**0:empty, 1:pigIron, 2:burning, 3:finish*/
	private int mode = 0;
	private int count = 0;

	private int prevMode = 0;
	private float pitch = 0.0F;

	public boolean powered = false;
	private boolean prevPowered = false;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.direction = nbt.getByte("dir");
        this.capacity = nbt.getInteger("capacity");
        this.mode = nbt.getInteger("mode");
        this.count = nbt.getInteger("count");
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("dir", this.direction);
        nbt.setInteger("capacity", this.capacity);
        nbt.setInteger("mode", this.mode);
        nbt.setInteger("count", this.count);
        return nbt;
    }

    @Override
	public void update()
    {
    	int x = this.getX();
		int y = this.getY();
		int z = this.getZ();

    	this.prevPowered = this.powered;
    	this.powered = false;

    	this.prevMode = this.mode;

    	if(!this.prevPowered && this.pitch == 0.0F)
		{
			if(BlockUtil.getBlock(this.getWorld(), x, y + 1, z) == RTMFluid.liquefiedPigIron)
			{
				if(this.capacity < Max_Capacity)
				{
					int meta = BlockUtil.getMetadata(this.getWorld(), x, y + 1, z);
					this.capacity += meta + 1;
					if(this.capacity > Max_Capacity)
    				{
						this.capacity = Max_Capacity;
    				}
					BlockUtil.setAir(this.world, x, y + 1, z);

					if(this.capacity > (int)((float)Max_Capacity * 0.75F))
					{
						this.mode = 1;
					}
				}
			}
			else if(this.capacity > 0)
			{
				if(this.mode == 1)
				{
					/*if(this.worldObj.getBlock(this.xCoord, this.yCoord - 4, this.zCoord) == RTMBlock.slot)
					{
						this.mode = 2;
					}*/
					this.mode = 2;
				}
				else if(this.mode == 2)
				{
					++this.count;

					if(this.count > this.capacity * 16)//反応時間
					{
						this.mode = 3;
						this.count = 0;
					}
				}
			}
		}

    	if(this.mode == 0 || this.mode == 3)
    	{
    		if(this.prevPowered)
        	{
        		this.pitch += 0.5F;
        	}
        	else
        	{
        		this.pitch -= 0.5F;
        	}

        	if(this.pitch > 90.0F)
        	{
        		this.pitch = 90.0F;
        	}

        	if(this.pitch < 0.0F)
        	{
        		this.pitch = 0.0F;
        	}

        	if(this.prevPowered && this.pitch > 27.0F)
    		{
    			Vec3 vec3 = PooledVec3.create(0.0D, 2.04805D, 2.9275D);
    			vec3 = vec3.rotateAroundX(-this.pitch);
    			vec3 = vec3.rotateAroundY((float)-this.direction * 90.0F);

    			if(this.mode == 3)
				{
    				if(this.capacity > 0)
    				{
    					--this.capacity;
    				}
    				else
    				{
    					this.mode = 0;
    				}
				}

    			if(this.world.isRemote)
            	{
    				if(this.mode == 3 && this.capacity > 0)
    				{
    					for(int i = 0; i < 10; ++i)
            			{
    						Random random = this.world.rand;
            				double x0 = x + vec3.getX() + 0.5D - 0.25D + random.nextFloat() * 0.5F;
                			double y0 = y + vec3.getY() - 0.25D + random.nextFloat() * 0.5F;
                			double z0 = z + vec3.getZ() + 0.5D - 0.25D + random.nextFloat() * 0.5F;
                			RTMCore.proxy.spawnModParticle(this.world, x0, y0, z0, 0.0D, -0.125D, 0.0D);
            			}
    				}
            	}
    			else
    			{
    				if(this.mode == 3 && this.capacity > 0)
    				{
    					int x1 = NGTMath.floor(x + vec3.getX() + 0.5D);
    					int y1 = NGTMath.floor(y + vec3.getY());
    					int z1 = NGTMath.floor(z + vec3.getZ() + 0.5D);
    					while(BlockUtil.getBlock(this.getWorld(), x1, y1, z1) == Blocks.AIR || BlockUtil.getBlock(this.getWorld(), x1, y1, z1) == RTMFluid.liquefiedSteel)
    					{
    						--y;
    					}
    					BlockLiquidBase.addLiquid(this.world, x1, y1 + 1, z1, RTMFluid.liquefiedSteel, 1, true);
    				}
    			}
    		}
    	}

    	if(!this.world.isRemote && this.mode != this.prevMode)
    	{
    		this.sendPacket();
    		NGTLog.sendChatMessageToAll("message.converter.mode" + this.mode, new Object[]{});
    	}
    }

    public float getPitch()
    {
    	return this.pitch;
    }

    public int getMode()
    {
    	return this.mode;
    }

    public byte getDirection()
    {
    	return this.direction;
    }

    public void setDirection(byte par1)
    {
    	this.direction = par1;
    	this.sendPacket();
    }

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
    {
		int x = this.getX();
		int y = this.getY();
		int z = this.getZ();
    	AxisAlignedBB bb = new AxisAlignedBB(x - 3, y - 3, z - 3, x + 4, y + 4, z + 4);
    	return bb;
    }
}