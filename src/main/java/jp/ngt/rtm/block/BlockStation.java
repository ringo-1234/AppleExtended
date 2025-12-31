package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.tileentity.TileEntityStation;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStation extends BlockContainerCustom
{
	public BlockStation()
	{
		super(Material.ROCK);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	public TileEntity createNewTileEntity(World par1, int par2)
	{
		return new TileEntityStation();
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		if(holder.getWorld().isRemote)
		{
			int x = holder.getBlockPos().getX();
    		int y = holder.getBlockPos().getY();
    		int z = holder.getBlockPos().getZ();
    		holder.getPlayer().openGui(RTMCore.instance, RTMCore.guiIdStation, holder.getWorld(), x, y, z);
		}
        return true;
    }

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(world, pos, state);

		if(!world.isRemote)
		{
			int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();
			//StationManager.INSTANCE.stationCollection.add(x, y, z);
		}
	}

	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
    	super.breakBlock(world, pos, state);

    	if(!world.isRemote)
		{
    		int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();
    		//StationManager.INSTANCE.stationCollection.remove(x, y, z);
		}
    }
}