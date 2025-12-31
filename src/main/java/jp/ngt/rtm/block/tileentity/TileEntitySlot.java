package jp.ngt.rtm.block.tileentity;

import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.block.BlockSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntitySlot extends TileEntity implements ITickable
{
	private int count;

	@Override
	public void update()
    {
    	++count;
    	if(count > 4)
    	{
    		count = 0;
    	}

    	if(count == 0 && this.world.isBlockPowered(this.getPos()))
		{
    		if(!this.world.isRemote)
        	{
    			((BlockSlot)RTMBlock.slot).inhaleLiquid(this.world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        	}
		}
    }
}