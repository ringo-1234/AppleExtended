package jp.ngt.ngtlib.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemColored;

public class ItemColoredBlock extends ItemColored
{
	public ItemColoredBlock(Block block)
	{
		super(block, true);
	}
}