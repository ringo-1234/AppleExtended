package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockInsulator extends BlockConnector
{
	public BlockInsulator()
	{
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityInsulator();
	}

	@Override
	protected ItemStack getItem(int damage)
    {
		return new ItemStack(RTMItem.installedObject, 1, 3);
    }
}