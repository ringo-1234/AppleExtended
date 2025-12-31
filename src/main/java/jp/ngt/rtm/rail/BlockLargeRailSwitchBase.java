package jp.ngt.rtm.rail;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailSwitchBase extends BlockLargeRailBase
{
	public BlockLargeRailSwitchBase()
	{
		super();
		this.setTickRandomly(true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityLargeRailSwitchBase();
	}

	@Override
	public boolean isCore()
	{
		return false;
	}
}