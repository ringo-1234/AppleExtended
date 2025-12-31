package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.rtm.block.tileentity.TileEntityDecoration;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDecoration extends BlockContainerCustom
{
	public BlockDecoration()
	{
		super(Material.ROCK);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityDecoration();
	}
}