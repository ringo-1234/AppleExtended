package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityRailroadSign;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailroadSign extends BlockContainerCustomWithMeta
{
	public BlockRailroadSign()
	{
		super(Material.CIRCUITS);
		float f0 = 0.0625F;
		this.setAABB(new AxisAlignedBB(f0*7.0F, 0.0F, f0*7.0F, f0*9.0F, 1.5F, f0*9.0F));
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntityRailroadSign();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		if(BlockUtil.getBlock(world, pos.up()) != Blocks.AIR)
		{
			return new AxisAlignedBB(0.0625F*7.0F, -0.5F, 0.0625F*7.0F, 0.0625F*9.0F, 1.0F, 0.0625F*9.0F);
		}
		else
		{
			return new AxisAlignedBB(0.0625F*7.0F, 0.0F, 0.0625F*7.0F, 0.0625F*9.0F, 1.5F, 0.0625F*9.0F);
		}
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
        if(holder.getWorld().isRemote)
        {
        	int x = holder.getBlockPos().getX();
    		int y = holder.getBlockPos().getY();
    		int z = holder.getBlockPos().getZ();
    		holder.getPlayer().openGui(RTMCore.instance, RTMCore.instance.guiIdSelectTileEntityTexture, holder.getWorld(), x, y, z);
        }
        return true;
    }

	@Override
	protected ItemStack getItem(int damage)
    {
		return new ItemStack(RTMItem.installedObject, 1, IstlObjType.RAILLOAD_SIGN.id);
    }
}