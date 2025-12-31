package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.BlockMachineBase;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSpeaker extends BlockMachineBase implements IBlockConnective
{
	public BlockSpeaker()
	{
		super(Material.GLASS);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntitySpeaker();
	}

	@Override
	protected ItemStack getItem(int meta)
	{
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.SPEAKER.id);
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(holder.getPlayer().isSneaking())
    	{
			super.onBlockActivated(holder, hitX, hitY, hitZ);
    	}
		else
		{
			if(world.isRemote)
			{
				holder.getPlayer().openGui(RTMCore.instance, RTMCore.instance.guiIdSpeaker, world, x, y, z);
			}
		}
		return true;
    }

	@Override
	public boolean canConnect(World world, int x, int y, int z)
	{
		return true;
	}
}
