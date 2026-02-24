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

package jp.ngt.rtm.block;

import java.util.List;
import java.util.Random;

import jp.ngt.ngtlib.block.BlockLiquidBase;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMFluid;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFurnaceFire extends BlockLiquidBase
{
	private static final Item itemIronOre = Item.getItemFromBlock(Blocks.IRON_ORE);

	public BlockFurnaceFire(Fluid fluid, boolean light)
	{
		super(fluid, RTMMaterial.melted);
		this.setLightLevel(light ? 1.0F : 0.0F);
	}

	@SideOnly(Side.CLIENT)
	@Override
    public BlockRenderLayer getBlockLayer()
    {
		return BlockRenderLayer.TRANSLUCENT;
    }

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
		super.updateTick(world, pos, state, random);

		if(!world.isRemote)
		{
			int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();
    		int meta = BlockUtil.getMetadata(world, pos);

			if(this == RTMFluid.furnaceFire)
			{
				if(meta == 0)
				{
					IBlockState state2 = BlockUtil.getBlockState(world, x, y - 1, z);
					Block block = state2.getBlock();
					if(block != RTMBlock.fireBrick && block != RTMBlock.hotStoveBrick && state2.isNormalCube())
					{
						BlockUtil.setBlock(world, x, y, z, Blocks.FIRE, 0, 2);
					}
				}
			}
			else if(this == RTMFluid.exhaustGas)
			{
				if(BlockUtil.getBlock(world, x, y + 1, z) == RTMFluid.furnaceFire)
				{
					int m1 = BlockUtil.getMetadata(world, x, y + 1, z);
					BlockUtil.setBlock(world, x, y, z, RTMFluid.furnaceFire, m1, 2);
					BlockUtil.setBlock(world, x, y + 1, z, RTMFluid.exhaustGas, meta, 2);
					world.scheduleBlockUpdate(new BlockPos(x, y + 1, z), this, this.tickRate(world), 0);
				}
				else if(BlockUtil.isAir(world, x, y + 1, z))
				{
					if(random.nextInt(10) == 0)
					{
						if(meta > 0)
						{
							BlockUtil.setBlock(world, x, y, z, RTMFluid.exhaustGas, meta - 1, 2);
						}
						else
						{
							BlockUtil.setBlock(world, x, y, z, Blocks.AIR, 0, 2);
						}
					}
				}
			}
		}
    }

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
		boolean flag1 = true;//焼却
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(entity instanceof EntityItem)
		{
			ItemStack itemstack0 = ((EntityItem)entity).getItem();
			int sizeCoke = 0;
			int sizeIron = 0;
			boolean isIron = false;

			if(itemstack0.getItem() == RTMItem.coke)
			{
				sizeCoke = itemstack0.getCount() / 2;
				entity.setInWeb();
				flag1 = false;
			}
			else if(itemstack0.getItem() == Items.COAL && itemstack0.getItemDamage() == 1)
			{
				sizeCoke = itemstack0.getCount() / 8;
				entity.setInWeb();
				flag1 = false;
			}
			else if(itemstack0.getItem() == itemIronOre)
			{
				sizeIron = itemstack0.getCount();
				isIron = true;
				entity.setInWeb();
				flag1 = false;
			}

			if(sizeCoke > 0 || sizeIron > 0)
			{
				if(!world.isRemote)
				{
					List list = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB((double)x, (double)y, (double)z, (double)x + 1.0D, (double)y + 1.0D, (double)z + 1.0D));
	                if(list != null && !list.isEmpty())
	                {
	                	int i2 = 0;
	                    for(int i = 0; i < list.size(); ++i)
	                    {
	                    	EntityItem entityItem = (EntityItem)list.get(i);
	                    	ItemStack itemstack1 = entityItem.getItem();
	                    	if(itemstack1.getItem() == RTMItem.coke)
	                    	{
	                    		i2 = itemstack1.getCount() / 2;
	                    	}
	                    	else if(itemstack1.getItem() == Items.COAL && itemstack1.getItemDamage() == 1)
	                    	{
	                    		i2 = itemstack1.getCount() / 8;
	                    	}
	                    	else
	                    	{
	                    		i2 = 0;
	                    	}

	                    	if(i2 > 0)
	                    	{
	                    		if(sizeCoke + i2 > sizeIron)
		                    	{
	                    			itemstack1.setCount(sizeCoke + i2 - sizeIron);
		                    		entityItem.setItem(itemstack1);
		                    		sizeCoke = sizeIron;
		                    	}
		                    	else
		                    	{
		                    		sizeCoke += i2;
		                    		entityItem.setDead();
		                    	}
	                    	}
	                    }
	                }

	                if(isIron)
	                {
	                	if(sizeCoke == 0)
	                	{
	                		return;
	                	}
	                	else if(sizeCoke < sizeIron)
	                	{
	                		itemstack0.shrink(sizeCoke);
		                	((EntityItem)entity).setItem(itemstack0);
		                	sizeIron = sizeCoke;
	                	}
	                	else
	                	{
	                		entity.setDead();
	                	}
	                }
	                else
	                {
	                	if(sizeIron == 0)
	                	{
	                		return;
	                	}
	                	else if(sizeCoke > sizeIron)
	                	{
	                		itemstack0.shrink(sizeIron);
		                	((EntityItem)entity).setItem(itemstack0);
		                	sizeCoke = sizeIron;
	                	}
	                	else
	                	{
	                		entity.setDead();
	                	}
	                }

					this.onCollidedIronOre(world, x, y, z, sizeIron);
				}
			}

			if(flag1)
			{
				entity.motionY = 0.20000000298023224D;
				entity.motionX = (double)((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
				entity.motionZ = (double)((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
				entity.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.4F, 2.0F + world.rand.nextFloat() * 0.4F);
			}
		}

		if(flag1)
		{
			entity.attackEntityFrom(DamageSource.LAVA, 1.0F);
			entity.setFire(5);
		}
    }

	/**材料が投入された時*/
	protected void onCollidedIronOre(World world, int x, int y, int z, int amount)
	{
		if(BlockUtil.getBlock(world, x, y - 1, z) instanceof BlockFurnaceFire)
		{
			if(BlockUtil.getBlock(world, x, y, z) == RTMFluid.furnaceFire && world.rand.nextInt(10) == 0)
			{
				int meta = BlockUtil.getMetadata(world, x, y, z);
				amount += world.rand.nextInt(meta + 1);
				if(world.rand.nextInt(5) == 0)
				{
					BlockUtil.setBlock(world, x, y, z, RTMFluid.exhaustGas, 15, 2);
				}
			}
			this.onCollidedIronOre(world, x, y - 1, z, amount);//下のガスブロックへ送る
		}
		else
		{
			while(amount > 0)
			{
				if(!(BlockUtil.getBlock(world, x, y, z) instanceof BlockFurnaceFire))
				{
					break;
				}
				BlockUtil.setAir(world, x, y, z);
				amount = BlockLiquidBase.addLiquid(world, x, y, z, RTMFluid.liquefiedPigIron, amount, true);
				++y;
			}
		}
	}

	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
		if(this == RTMFluid.furnaceFire)
		{
			IBlockState state = BlockUtil.getBlockState(world, x, y + 1, z);
	        if(state.getMaterial() == Material.AIR && !state.isOpaqueCube())
	        {
	        	double d5 = (double)((float)x + random.nextFloat());
	        	double d6 = (double)y;// + this.maxY;
	        	double d7 = (double)((float)z + random.nextFloat());
	        	world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, d5, d6, d7, 0.0D, 0.0D, 0.0D);

	            /*if(random.nextInt(100) == 0)
	            {
	                d5 = (double)((float)x + random.nextFloat());
	                d6 = (double)y + this.maxY;
	                d7 = (double)((float)z + random.nextFloat());
	                world.spawnParticle("lava", d5, d6, d7, 0.0D, 0.0D, 0.0D);
	                world.playSound(d5, d6, d7, "liquid.lavapop", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
	            }*/

	            if(random.nextInt(200) == 0)
	            {
	                world.playSound((double)x, (double)y, (double)z, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
	            }
	        }

	        //雫の処理(1.8)
	        //doesBlockHaveSolidTopSurfaceが無くなってるから無し
	        /*if(random.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, new BlockPos(x, y - 1, z)) && !world.getBlockState(new BlockPos(x, y - 2, z)).getMaterial().blocksMovement())
	        {
	        	double d5 = (double)((float)x + random.nextFloat());
	        	double d6 = (double)y - 1.05D;
	        	double d7 = (double)((float)z + random.nextFloat());
	            world.spawnParticle(EnumParticleTypes.DRIP_LAVA, d5, d6, d7, 0.0D, 0.0D, 0.0D);
	        }*/
		}
    }

	@Override
	protected void meltBlock(World world, int x, int y, int z)
	{
		if(this == RTMFluid.furnaceFire)
		{
			super.meltBlock(world, x, y, z);
		}
	}
}