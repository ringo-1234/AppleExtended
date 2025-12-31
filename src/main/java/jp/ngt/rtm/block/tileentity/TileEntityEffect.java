package jp.ngt.rtm.block.tileentity;

import java.util.Iterator;
import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityEffect extends TileEntityCustom implements ITickable
{
	public static final int Phase1 = 40;
	public static final int Phase2 = 400;
	public static final int Phase3 = 800;

	public static final float Scale = 20.0F;
	public static final float Slope1 = 0.03125F;
	public static final float Slope2 = 0.015625F;
	public static final float Radius = 2.0F;
	public static final float MaxDistance = 128.0F;

	public static final DamageSource nuclearDamage = (new DamageSource("nuclear")).setFireDamage();

	public int tickCount;

	public TileEntityEffect()
	{
		super();
		//this.MaxDistance = (float)(16 * MinecraftServer.getServer().getConfigurationManager().getViewDistance());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.tickCount = nbt.getInteger("count");
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("count", this.tickCount);
        return nbt;
    }

	@Override
    public void update()
    {
		if(this.shouldUpdateAsAtomicBomb())
		{
			if(!this.world.isRemote)
			{
				if(this.tickCount > Phase3)
				{
					this.world.setBlockToAir(this.getPos());
				}
				else if(this.tickCount > Phase1)
				{
					if(this.tickCount % 3 == 0)
					{
						float f0 = (float)(this.tickCount - Phase1);
						double flameSize = this.getSigmoid(f0, Slope1) * Scale * Radius;
						double blastSize = this.getLinear(f0);
						this.doExplosion(flameSize, blastSize);
					}
				}
			}

			++this.tickCount;
		}
    }

	public boolean shouldUpdateAsAtomicBomb()
	{
		return this.getBlockMetadata() == RTMCore.ATOMIC_BOM_META;
	}

	private void doExplosion(double flameSize, double blastSize)
	{
		int x = this.getPos().getX();
		int y = this.getPos().getY();
		int z = this.getPos().getZ();
		double tileX = (double)x + 0.5D;
		double tileY = (double)y + 0.5D;
		double tileZ = (double)z + 0.5D;

		if(blastSize > MaxDistance)
		{
			blastSize = MaxDistance;
		}

		double d0 = flameSize * flameSize;
		double d1 = blastSize * blastSize;

		//ブロック破壊
		if(this.tickCount < Phase2 && this.world.getGameRules().getBoolean("mobGriefing"))
		{
			int i0 = (int)(blastSize);
			int i1 = (int)(blastSize * 2.0F);
			int i2 = (int)d0;
			int i3 = (int)d1;
			int i0y = i0;
			int i1y = i1;
			if(y - i0y < 0)
			{
				i0y = y;
			}

			if(y - i0y + i1y >= 256)
			{
				i1y = 256 - y + i0y;
			}

			for(int i = 0; i < i1; ++i)
	        {
	            for(int j = 0; j < i1y; ++j)
	            {
	                for(int k = 0; k < i1; ++k)
	                {
	                	int x0 = i - i0;
	                	int y0 = j - i0y;
	                	int z0 = k - i0;
	                	int i4 = x0 * x0 + y0 * y0 + z0 * z0;
	                	int flag = 0;
	                	if(i4 <= i2)
	                	{
	                		flag = 1;
	                	}
	                	else if(i4 <= i3)
	                	{
	                		flag = 2;
	                	}

	                	if(flag > 0)
	                	{
	                		int x1 = x0 + x;
	                		int y1 = y0 + y;
	                		int z1 = z0 + z;
	                		if(this.isChunksExist(x1, y1, z1))
	                		{
	                			IBlockState state = BlockUtil.getBlockState(this.getWorld(), x1, y1, z1);
	                			Block block = state.getBlock();
	                			if(block == Blocks.AIR)
	                			{
	                				continue;
	                			}

	                    		float hardness = state.getBlockHardness(this.world, this.getPos());
	                    		if(flag == 1)
	                			{
	                    			if(hardness >= 0.0F && hardness < 500.0F)
	                    			{
	                    				this.setBlock(x1, y1, z1, Blocks.AIR);
	                    			}
	                			}
	                    		else
	                			{
	                    			if(hardness >= 0.0F && hardness < 0.5F)
	                				{
	                					if(block != Blocks.FIRE)
	                    				{
	                						this.setBlock(x1, y1, z1, Blocks.AIR);
	                    					if(state.getMaterial() == Material.PLANTS || state.getMaterial() == Material.LEAVES)
	                            			{
	                            				this.setBlock(x1, y1, z1, Blocks.FIRE);
	                            			}
	                    				}
	                				}
	                    			else if(block == Blocks.GRASS || block == Blocks.FARMLAND || block == Blocks.MYCELIUM)
	                    			{
	                    				this.setBlock(x1, y1, z1, Blocks.DIRT);
	                    			}
	                			}
	                		}
	                	}
	                }
	            }
	        }
		}

		//Entityへの爆風処理
		double time = (double)(Phase3 - this.tickCount) / (double)(Phase3 - Phase1);//0.0~1.0
		List list = this.world.getEntitiesWithinAABBExcludingEntity(null, this.getAABB(tileX, tileY, tileZ, blastSize));
		//List list = this.worldObj.loadedEntityList;//ConcurrentModificationException
		Iterator iterator = list.iterator();
		while(iterator.hasNext())
		{
			Entity entity = (Entity)iterator.next();
			double distanceSq = entity.getDistanceSq(tileX, tileY, tileZ);
			if(distanceSq < d1)
			{
				double dx = entity.posX - tileX;
				double dy = entity.posY + (double)entity.getEyeHeight() - tileY;
				double dz = entity.posZ - tileZ;
				Vec3 vec3 = PooledVec3.create(dx, dy, dz);
				double density = NGTMath.pow(0.5D, this.getBlockDensity(tileX, tileY, tileZ, vec3));
				double distance = Math.sqrt(distanceSq);
				double d4 = 1.0D - (distance / (double)MaxDistance);
				double d5 = time * density * d4;
				double acceleration = d5 / distance * 2.5D;//max1.0 * a
				float damage = (float)d5 * 10.0F;

				if(damage >= 0.25F)
				{
					if(distanceSq < d0)//ブロック破壊範囲内
					{
						entity.attackEntityFrom(DamageSource.IN_FIRE, damage * 2.0F);
						entity.setFire(5);
					}
					else
					{
						entity.attackEntityFrom(nuclearDamage, damage);
					}
				}

				if(acceleration > 0.0D)
				{
					if(entity instanceof EntityPlayer)
					{
						if(((EntityPlayer)entity).capabilities.isFlying)
						{
							continue;
						}
					}

					entity.velocityChanged = true;
					entity.motionX += dx * acceleration;
					entity.motionY += dy * acceleration * 1.2F;
					entity.motionZ += dz * acceleration;
				}
			}
		}
	}

	public double getSigmoid(float x, float a)//(1/(1+e^-x*0.5))-0.5
	{
		return ((1.0D / (1.0D + Math.pow(Math.E, -x * a))) - 0.5D) * 2.0D;
	}

	public double getLinear(float x)
	{
		return x * Slope2 * Scale * Radius;
	}

	private AxisAlignedBB getAABB(double x, double y, double z, double size)
	{
		double minY = y - size;
		double maxY = y + size;

		if(minY < 0.0D)
		{
			minY = 0.0D;
		}

		if(maxY > 256.0D)
		{
			maxY = 256.0D;
		}

		return new AxisAlignedBB(x - size, minY, z - size, x + size, maxY, z + size);
	}

	private boolean isChunksExist(int x, int y, int z)
    {
        if(y >= 0 && y < 256)
        {
        	//1.8以前
        	/*IChunkProvider chunkProvider = this.worldObj.getChunkProvider();
        	par1 >>= 4;
        	par3 >>= 4;
            return chunkProvider.chunkExists(par1, par3);*/
            return this.getWorld().isBlockLoaded(new BlockPos(x, y, z));
        }
        else
        {
            return false;
        }
    }

	private int getBlockDensity(double posX, double posY, double posZ , Vec3 par2)
    {
		Vec3 vec3 = par2.normalize();
		double x0 = vec3.getX();
		double y0 = vec3.getY();
		double z0 = vec3.getZ();
		int i0 = 0;

		while(vec3.length() < par2.length())
		{
			int x = NGTMath.floor(posX + vec3.getX());
			int y = NGTMath.floor(posY + vec3.getY());
			int z = NGTMath.floor(posZ + vec3.getZ());
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = this.world.getBlockState(pos);
			Block block = state.getBlock();
			if((block.getBoundingBox(state, this.getWorld(), pos) != null) && block.canCollideCheck(state, false))
            {
				++i0;
            }

			vec3 = vec3.add(x0, y0, z0);
		}

		return i0;
    }

	private boolean setBlock(int x, int y, int z, Block block)
	{
		//Client描画アプデのみ
		return BlockUtil.setBlock(this.getWorld(), x, y, z, block, 0, 2);
	}

	@SideOnly(Side.CLIENT)
	@Override
    public double getMaxRenderDistanceSquared()
    {
		return this.shouldUpdateAsAtomicBomb() ? Double.POSITIVE_INFINITY : 32.0D * 32.0D;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		return INFINITE_EXTENT_AABB;
    }
}