package jp.ngt.rtm.rail;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLargeRailSwitchCore extends BlockLargeRailBase
{
	public BlockLargeRailSwitchCore()
	{
		super();
		this.setTickRandomly(true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityLargeRailSwitchCore();
	}

	@Override
	public boolean isCore()
	{
		return true;
	}
}