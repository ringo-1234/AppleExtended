package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockElectricalWiring extends BlockContainerCustomWithMeta implements IBlockConnective
{
	protected BlockElectricalWiring(Material material)
	{
		super(material);
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		if(holder.getWorld().isRemote)
    	{
    		return true;
    	}
    	else
    	{
    		TileEntityElectricalWiring tile = (TileEntityElectricalWiring)holder.getWorld().getTileEntity(holder.getBlockPos());
    		tile.onRightClick(holder.getPlayer());
    		return true;
    	}
    }

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntityElectricalWiring tile = (TileEntityElectricalWiring)world.getTileEntity(pos);
		tile.onBlockBreaked();
		super.breakBlock(world, pos, state);//removeTileEntity
	}
}