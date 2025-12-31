package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.rtm.block.decoration.DecorationModel;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityDecoration extends TileEntityCustom
{
	private String modelName = DecorationModel.DEFAULT_MODEL.name;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.modelName = nbt.getString("ModelName");
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("ModelName", this.modelName);
        return nbt;
    }

	public void setModelName(String par1)
	{
		this.modelName = par1;
		this.sendPacket();
	}

	public String getModelName()
	{
		return this.modelName;
	}
}