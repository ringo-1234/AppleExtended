/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

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