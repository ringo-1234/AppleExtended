package jp.ngt.ngtlib.util;

import java.util.List;
import java.util.Random;

import jp.ngt.ngtlib.block.BlockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**バニラの要素にスクリプトからアクセスするためのラッパー*/
public final class MCWrapper
{
	public static Random getRandom(World world)
	{
		return world.rand;
	}

	/*World*******************************************************************************/

	public static World getWorld(Entity entity)
	{
		return entity.getEntityWorld();
	}

	public World getWorld(TileEntity entity)
	{
		return entity.getWorld();
	}

	/*Block*******************************************************************************/

	public static void setBlockToAir(World world, double x, double y, double z)
	{
		int ix = MathHelper.floor(x);
		int iy = MathHelper.floor(y);
		int iz = MathHelper.floor(z);
		BlockUtil.setAir(world, ix, iy, iz);
	}

	/**ブロック破壊(ドロップ有り、クリエイティブでもOK)*/
	public static void breakBlock(Entity entity, double x, double y, double z)
	{
		if(!entity.getPassengers().isEmpty())
		{
			Entity passenger = entity.getPassengers().get(0);
			if(passenger instanceof EntityPlayer)
			{
				World world = entity.getEntityWorld();
				EntityPlayer player = (EntityPlayer)passenger;
				BlockPos pos = new BlockPos(x, y, z);
				IBlockState state = world.getBlockState(pos);
				if(state.getBlock() != Blocks.AIR)
				{
					TileEntity tileentity = world.getTileEntity(pos);
					state.getBlock().dropBlockAsItem(world, pos, state, 0);
					state.getBlock().removedByPlayer(state, world, pos, player, false);
				}
			}
		}
	}

	//ItemInWorldManager
	/*public static void breakBlock(Entity entity, double x, double y, double z)
	{
		if(entity.riddenByEntity instanceof EntityPlayer)
		{
			World world = entity.getEntityWorld();
			EntityPlayer player = (EntityPlayer)entity.riddenByEntity;
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = world.getBlockState(pos);
			boolean flag1 = state.getBlock().canHarvestBlock(world, pos, player);
			//boolean flag2 = removeBlock(world, pos, flag1,player);
			if(flag1)//(flag2 && flag1)
			{
				TileEntity tileentity = world.getTileEntity(pos);
				state.getBlock().harvestBlock(world, player, pos, state, tileentity);
			}
		}
	}

	private static boolean removeBlock(World world, BlockPos pos, boolean canHarvest, EntityPlayer player)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        iblockstate.getBlock().onBlockHarvested(world, pos, iblockstate, player);
        boolean flag = iblockstate.getBlock().removedByPlayer(world, pos, player, canHarvest);
        if(flag)
        {
            iblockstate.getBlock().onBlockDestroyedByPlayer(world, pos, iblockstate);
        }
        return flag;
    }*/

	/*Entity*******************************************************************************/

	//nullチェック入れるべし?
	public static int getPosX(TileEntity entity)
	{
		return entity.getPos().getX();
	}

	public static int getPosY(TileEntity entity)
	{
		return entity.getPos().getY();
	}

	public static int getPosZ(TileEntity entity)
	{
		return entity.getPos().getZ();
	}

	public static double getPosX(Entity entity)
	{
		return entity.posX;
	}

	public static double getPosY(Entity entity)
	{
		return entity.posY;
	}

	public static double getPosZ(Entity entity)
	{
		return entity.posZ;
	}

	public static double getYaw(Entity entity)
	{
		return entity.rotationYaw;
	}

	public static double getPitch(Entity entity)
	{
		return entity.rotationPitch;
	}

	public static int getEntityId(Entity entity)
	{
		return entity.getEntityId();
	}

	public static Entity getEntityById(World world, int id)
	{
		return world.getEntityByID(id);
	}

	public static List<Entity> getEntities(World world, double x1, double y1, double z1, double x2, double y2, double z2)
	{
		return world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
	}

	public static double getDistanceSq(Entity entity1, Entity entity2)
	{
		return entity1.getDistanceSq(entity2);
	}
}