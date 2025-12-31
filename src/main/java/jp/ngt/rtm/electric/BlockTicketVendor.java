package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.BlockMachineBase;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockTicketVendor extends BlockMachineBase
{
	public BlockTicketVendor()
	{
		super(Material.ROCK);
		this.setLightOpacity(0);
	}

	@Override
	public TileEntity createNewTileEntity(World par1World, int par2)
    {
		return new TileEntityTicketVendor();
    }

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		World world = holder.getWorld();
    	BlockPos pos = holder.getBlockPos();
    	EntityPlayer player = holder.getPlayer();

		if(NGTUtil.isEquippedItem(player, RTMItem.crowbar))
    	{
    		;
    	}

    	if(!world.isRemote)
    	{
    		int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();
    		player.openGui(RTMCore.instance, RTMCore.instance.guiIdTicketVendor, world, x, y, z);
    	}
    	return true;
    }

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
		if(!world.isRemote)
        {
			spawnAsEntity(world, pos, new ItemStack(RTMItem.installedObject, 1, IstlObjType.TICKET_VENDOR.id));
        }
    }

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.TICKET_VENDOR.id);
    }
}