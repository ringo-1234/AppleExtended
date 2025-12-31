package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.block.tileentity.TileEntityPlantOrnament;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPlant extends BlockContainerCustom
{
	public BlockPlant()
	{
		super(Material.PLANTS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
    {
		return new TileEntityPlantOrnament();
    }
}