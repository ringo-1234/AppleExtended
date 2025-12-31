package jp.ngt.rtm.rail;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTurntableCore extends BlockLargeRailBase
{
	public BlockTurntableCore()
	{
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityTurnTableCore();
	}

	@Override
	public boolean isCore()
	{
		return true;
	}
}