package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase;
import jp.ngt.rtm.modelpack.cfg.MachineConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockMachineBase extends BlockContainerCustomWithMeta
{
	protected BlockMachineBase(Material mat)
	{
		super(mat);
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		this.clickMachine(holder.getWorld(), holder.getBlockPos().getX(), holder.getBlockPos().getY(), holder.getBlockPos().getZ(), holder.getPlayer());
		return true;
    }

	protected boolean clickMachine(World world, int x, int y, int z, EntityPlayer player)
	{
    	if(player.isSneaking())//NGTUtil.isEquippedItem(player, RTMItem.crowbar))
    	{
    		if(world.isRemote)
    		{
    			player.openGui(RTMCore.instance, RTMCore.guiIdSelectTileEntityModel, player.getEntityWorld(), x, y, z);
    		}
    		return true;
    	}
    	return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		TileEntityMachineBase tile = (TileEntityMachineBase)world.getTileEntity(pos);
		if(tile == null){return 0;}
		MachineConfig cfg = tile.getResourceState().getResourceSet().getConfig();
		return tile.isGettingPower ? cfg.brightness[1] : cfg.brightness[0] ;
    }
}