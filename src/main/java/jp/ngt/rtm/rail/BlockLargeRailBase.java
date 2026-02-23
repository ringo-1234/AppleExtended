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

package jp.ngt.rtm.rail;

import java.util.List;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMMaterial;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.util.RailMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLargeRailBase extends BlockContainerCustomWithMeta
{
	public static final float THICKNESS = 0.0625F;
	public static final float AABB_ADD_Y = 256.0F;

	public BlockLargeRailBase()
	{
		super(RTMMaterial.RAIL_BASE);
		this.setHardness(1.0F);
		this.setLightOpacity(0);
		this.setResistance(15.0F);
		this.setSoundType(SoundType.GROUND);
		this.setAABB(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, THICKNESS, 1.0F));
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityLargeRailBase();
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		boolean flag = this.preventMobMovement(world, pos) && entity instanceof EntityLiving;
		AxisAlignedBB aabb2 = this.getAABB(world, x, y, z, flag);
		if(entityBox.intersects(aabb2))
		{
			collidingBoxes.add(aabb2);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return this.getAABB(world, 0, 0, 0, this.preventMobMovement(world, pos));
	}

	private AxisAlignedBB getAABB(IBlockAccess world, int x, int y, int z, boolean par5)
	{
		double d0 = par5 ? AABB_ADD_Y : 0.0D;
		AxisAlignedBB aabb = this.getAABBWithState(world, new BlockPos(x, y, z)).offset(x, y, z);
		aabb = aabb.setMaxY(aabb.maxY + d0);
		return aabb;
	}

	public boolean preventMobMovement(IBlockAccess world, BlockPos pos)
	{
		TileEntityLargeRailCore core = this.getCore(world, pos);
		if(core != null)
		{
			ModelSetRail set = core.getResourceState().getResourceSet();
			return !set.getConfig().allowCrossing;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		return this.getAABBWithState(world, pos).offset(pos);
	}

	protected AxisAlignedBB getAABBWithState(IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null){return FULL_BLOCK_AABB;}

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		float[] fa = ((TileEntityLargeRailBase)tile).getBlockHeights(x, y, z, THICKNESS, false);
		float height2 = 0.0F;
		for(int i = 0; i < 4; ++i)
		{
			height2 += fa[i];
		}
		height2 *= 0.25F;
		return new AxisAlignedBB(0.0F, 0.0F, 0.0F,
				1.0F, (height2 < THICKNESS ? THICKNESS : height2), 1.0F);
	}

	@Override
	protected boolean removedByPlayer(BlockArgHolder holder, boolean willHarvest)
	{
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();
		if(world.isRemote)
		{
			return super.removedByPlayer(holder, willHarvest);
		}
		else
		{
			if(PermissionManager.INSTANCE.hasPermission(player, RTMCore.EDIT_RAIL))
			{
				if(!player.capabilities.isCreativeMode)
				{
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					TileEntityLargeRailCore tile1 = this.getCore(world, pos);
					if(tile1 != null)
					{
						this.dropRail(world, x, y, z, tile1.getResourceState());
					}
				}
				return super.removedByPlayer(holder, willHarvest);
			}
			return false;
		}
	}

	protected void dropRail(World world, int x, int y, int z, ResourceStateRail prop)
	{
		if(!world.isRemote)
		{
			spawnAsEntity(world, new BlockPos(x, y, z), ItemRail.getRailItem(prop));
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntityLargeRailCore core = this.getCore(world, pos);
		if(!world.isRemote && core != null && !core.breaking)
		{
			RailMap[] railmaps = core.getAllRailMaps();
			for(RailMap rm : railmaps)
			{
				rm.breakRail(world, core.getResourceState(), core);
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	protected boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
	{
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		if(!world.isRemote)
		{
			TileEntityLargeRailCore core = this.getCore(world, holder.getBlockPos());
			if(core != null && player.getHeldItemMainhand().getItem() == RTMItem.paddle)
			{
				core.getRailMap(null).showRailProp();
			}
		}
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if(world.isRemote)
		{
			TileEntityLargeRailCore rail = this.getCore(world, pos);
			if(rail == null || rail.getResourceState() == null){return;}
			Block base = rail.getResourceState().block;
			boolean isSnow = (base.getMaterial(base.getDefaultState()) == Material.SNOW);
			if(isSnow && entity instanceof EntityBogie)
			{
				EntityTrainBase train = ((EntityBogie)entity).getTrain();
				if(train != null && Math.abs(train.getSpeed()) > 0.0F)
				{
					double speed = (double)train.getSpeed() * 0.125D;
					for(int i = 0; i < 5; ++i)
					{
						double d0 = pos.getX() + (double)world.rand.nextFloat();
						double d1 = pos.getY() + (double)world.rand.nextFloat() * 0.25D;
						double d2 = pos.getZ() + (double)world.rand.nextFloat();
						double vx = (d0 - entity.posX) * speed;
						double vz = (d2 - entity.posZ) * speed;
						world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, d0, d1, d2, vx, 0.125D, vz);
					}
				}
			}
		}
	}

	public boolean isCore()
	{
		return false;
	}

	public static TileEntityLargeRailCore getCore(IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityLargeRailBase)
		{
			return ((TileEntityLargeRailBase)tile).getRailCore();
		}
		return null;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileEntityLargeRailCore core = this.getCore(world, pos);
		return core != null ? ItemRail.copyItemFromRail(core) : ItemStack.EMPTY;
	}

	@Override
	public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return this.preventMobMovement(world, pos) ? PathNodeType.BLOCKED : PathNodeType.RAIL;
	}
}