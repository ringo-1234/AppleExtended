package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityMechanism;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMechanism extends BlockContainerCustom
{
	public BlockMechanism()
	{
		super(Material.CIRCUITS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityMechanism();
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		Item item = holder.getPlayer().getHeldItemMainhand().getItem();
		if(item != RTMItem.installedObject && item != RTMItem.itemVehicle)
		{
			TileEntity tileEntity = holder.getWorld().getTileEntity(holder.getBlockPos());
			if(tileEntity instanceof TileEntityMechanism)
			{
				((TileEntityMechanism)tileEntity).onRightClick(holder.getPlayer());
			}
			return true;
		}
		return false;
    }
}
