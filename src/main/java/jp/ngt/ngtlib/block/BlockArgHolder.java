package jp.ngt.ngtlib.block;

import jp.ngt.ngtlib.item.ItemArgHolderBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;

public class BlockArgHolder extends ItemArgHolderBase<BlockArgHolder>
{
	private IBlockState state;
	private IBlockAccess access;

	public IBlockState getBlockState()
	{
		return this.state;
	}

	public BlockArgHolder setBlockState(IBlockState par1)
	{
		this.state = par1;
		return this;
	}

	public IBlockAccess getBlockAccess()
	{
		return this.access;
	}

	public BlockArgHolder setBlockAccess(IBlockAccess par1)
	{
		this.access = par1;
		return this;
	}
}