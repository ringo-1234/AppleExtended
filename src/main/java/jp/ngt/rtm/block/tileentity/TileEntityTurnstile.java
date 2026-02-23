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

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityTurnstile extends TileEntityMachineBase
{
	private int count = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.count = nbt.getInteger("Count");
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("Count", this.count);
        return nbt;
    }

	@Override
    public void update()
    {
    	super.update();

    	if(this.count > 0)
    	{
    		--this.count;
    	}
    }

	/**通り抜けられる*/
    public boolean canThrough()
    {
    	return this.count > 0;
    }

    public void setCount(int par1)
    {
    	this.count = par1;
    	if(!this.world.isRemote)
    	{
    		this.sendPacket();
    	}
    }

	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_TURNSTILE;
	}
}