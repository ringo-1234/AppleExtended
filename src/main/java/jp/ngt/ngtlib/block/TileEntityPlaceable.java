package jp.ngt.ngtlib.block;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityPlaceable extends TileEntityCustom
{
	private float rotation;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.setRotation(nbt.getFloat("Yaw"), false);
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("Yaw", this.rotation);
        return nbt;
    }

	public float getRotation()
	{
		return this.rotation;
	}

	public void setRotation(float par1, boolean synch)
	{
		this.rotation = par1 % 360.0F;
		if(synch)
		{
			this.sendPacket();
			this.markDirty();
		}
	}

	public void setRotation(EntityPlayer player, float rotationInterval, boolean synch)
	{
		int yaw = NGTMath.floor(NGTMath.normalizeAngle(-player.rotationYaw + 180.0D + (rotationInterval / 2.0D)) / (double)rotationInterval);
		this.setRotation((float)yaw * rotationInterval, synch);
	}
}