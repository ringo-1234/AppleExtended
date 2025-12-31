package jp.ngt.rtm.rail;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailCore extends BlockLargeRailBase
{
	public BlockLargeRailCore()
	{
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityLargeRailNormalCore();
	}

	@Override
	public boolean isCore()
	{
		return true;
	}
}