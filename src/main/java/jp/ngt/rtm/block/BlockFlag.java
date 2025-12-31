package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.tileentity.TileEntityFlag;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockFlag extends BlockContainerCustom
{
	public BlockFlag()
	{
		super(Material.IRON);
		this.setLightOpacity(0);
		this.setAABB(new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityFlag();
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
}