package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSignal extends BlockContainerCustomWithMeta implements IBlockConnective
{
	public BlockSignal()
	{
		super(Material.ROCK);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
    {
		return new TileEntitySignal();
    }

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		if(holder.getWorld().isRemote)
		{
			int x = holder.getBlockPos().getX();
			int y = holder.getBlockPos().getY();
			int z = holder.getBlockPos().getZ();
			holder.getPlayer().openGui(RTMCore.instance, RTMCore.guiIdSelectTileEntityModel, holder.getWorld(), x, y, z);
		}
		return true;
    }

	@Override
	public boolean removedByPlayer(BlockArgHolder holder, boolean willHarvest)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		if(!world.isRemote)
		{
			TileEntitySignal tile = (TileEntitySignal)world.getTileEntity(pos);
			tile.setOrigBlock();
			if(!holder.getPlayer().capabilities.isCreativeMode)
			{
				this.dropBlockAsItemWithChance(world, pos, null, 0.0F, 0);
			}
		}
		return true;
    }

	@Override
	protected ItemStack getItem(int damage)
    {
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.SIGNAL.id);
    }

	@Override
	public boolean canConnect(World world, int x, int y, int z)
	{
		return true;
	}
}