package jp.ngt.rtm.block.tileentity;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileEntityFluorescent extends TileEntityOrnament implements ITickable
{
	private int count = 0;
	public byte dirF;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.dirF = nbt.getByte("dir");
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("dir", this.dirF);
        return nbt;
    }

    public byte getDir()
    {
       return this.dirF;
    }

    public void setDir(byte byte0)
    {
    	this.dirF = byte0;

    }

    @Override
    public void update()
    {
    	if(this.getBlockMetadata() == 2)
    	{
    		++this.count;
    		if(this.count == 3)
    		{
    			//明るさ更新
    			this.world.checkLight(this.getPos());
        		this.count = 0;
    		}
    	}
    }

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.ORNAMENT_LAMP;
	}
}