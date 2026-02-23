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

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityConverter extends TileEntityCustom implements ITickable
{
	private int[] corePos = {0, 0, 0};
	private TileEntityConverterCore core;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.corePos = nbt.getIntArray("core");
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setIntArray("core", this.corePos);
        return nbt;
    }

    @Override
	public void update()
    {
    	if(this.getCore() != null)
    	{
    		if(this.world.isBlockIndirectlyGettingPowered(this.getPos()) > 0)
    		{
        		this.core.powered = true;
    		}
    	}
    }

    /**null有り*/
    public TileEntityConverterCore getCore()
    {
    	if(this.core == null)
    	{
    		TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), this.corePos[0], this.corePos[1], this.corePos[2]);
    		if(tile instanceof TileEntityConverterCore)
    		{
    			this.core = (TileEntityConverterCore)tile;
    		}
    	}
    	return this.core;
    }

    public void setCorePos(int x, int y, int z)
    {
    	this.corePos = new int[]{x, y, z};
    }

    @Override
    public void setPos(int x, int y, int z, int prevX, int prevY, int prevZ)
	{
    	super.setPos(x, y, z, prevX, prevY, prevZ);
    	this.corePos[0] += prevX - x;
    	this.corePos[1] += prevY - y;
    	this.corePos[2] += prevZ - z;
	}
}