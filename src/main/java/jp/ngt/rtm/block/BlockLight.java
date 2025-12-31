package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityLight;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLight extends BlockMachineBase
{
	public BlockLight()
	{
		super(Material.GLASS);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntityLight();
	}

	@Override
	protected ItemStack getItem(int meta)
	{
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.LIGHT.id);
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
}