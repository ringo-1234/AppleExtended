package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntitySignBoard;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSignBoard extends BlockContainerCustomWithMeta
{
	public BlockSignBoard()
	{
		super(Material.CIRCUITS);
		this.setLightOpacity(0);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntitySignBoard();
	}

	@Override
	protected ItemStack getItem(int damage)
    {
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.SIGNBOARD.id);
    }

	@Override
	public boolean removedByPlayer(BlockArgHolder holder, boolean willHarvest)
	{
		if(holder.getWorld().isRemote)
		{
			return super.removedByPlayer(holder, willHarvest);
		}
		else
		{
			if(PermissionManager.INSTANCE.hasPermission(holder.getPlayer(), RTMCore.EDIT_ORNAMENT))
			{
				return super.removedByPlayer(holder, willHarvest);
			}
			return false;
		}
	}

	/*@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
		TileEntitySignBoard tile = (TileEntitySignBoard)world.getTileEntity(x, y, z);
		float h = tile.currentProperty.height;
		float w = tile.currentProperty.width;
		float d = tile.currentProperty.depth;
		return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
    }*/

	/*@Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
		TileEntitySignBoard tile = (TileEntitySignBoard)world.getTileEntity(x, y, z);
		byte dir = tile.getDirection();
		int meta = world.getBlockMetadata(x, y, z);
		float cx = 0.0F;
		float cy = 0.0F;
		float cz = 0.0F;
		float h = tile.currentProperty.height / 2.0F;
		float w = tile.currentProperty.width / 2.0F;
		float d = tile.currentProperty.depth / 2.0F;

		if(meta == 0)
		{
			cy = 0.5F - h;
		}
		else if(meta == 1)
		{
			cy = h - 0.5F;
		}
		else
		{
			if(dir == 1 && meta == 4)
			{
				cx = 0.5F - d;
			}
			else if(dir == 3 && meta == 5)
			{
				cx = d - 0.5F;
			}
			else if(dir == 0 && meta == 3)
			{
				cz = d - 0.5F;
			}
			else if(dir == 2 && meta == 2)
			{
				cz = 0.5F - d;
			}
			else if((dir == 0 && meta == 4) || (dir == 2 && meta == 5))
			{
				cx = 0.5F - w;
			}
			else if((dir == 1 && meta == 3) || (dir == 3 && meta == 2))
			{
				cz = 0.5F - w;
			}
			else if(dir == 0 || dir == 2)
			{
				cx = w - 0.5F;
			}
			else
			{
				cz = w - 0.5F;
			}
		}

		if(dir == 1 || dir == 3)
		{
			float f0 = w;
			w = d;
			d = f0;
		}

		this.setBlockBounds(0.5F + cx - w , 0.5F + cy - h, 0.5F + cz - d, 0.5F + cx + w, 0.5F + cy + h, 0.5F + cz + d);
    }*/

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
        if(holder.getWorld().isRemote)
        {
        	int x = holder.getBlockPos().getX();
    		int y = holder.getBlockPos().getY();
    		int z = holder.getBlockPos().getZ();
    		holder.getPlayer().openGui(RTMCore.instance, RTMCore.instance.guiIdSignboard, holder.getWorld(), x, y, z);
        }
        return true;
    }

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		TileEntitySignBoard tile = (TileEntitySignBoard)world.getTileEntity(pos);
		if(tile != null)
		{
			int value = tile.getResourceState().getResourceSet().getConfig().lightValue;
			if(value >= 0)
			{
				return value;
			}
			else if(value == -16)
			{
				return NGTMath.RANDOM.nextInt(6) * 3;
			}
			else if(tile.isGettingPower)
			{
				return -value;
			}
		}
		return 0;
    }
}