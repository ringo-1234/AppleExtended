package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityPipe extends TileEntityOrnament
{
	/**0:Non, 1:Block, 2:Pipe 3, Slot*/
	public byte[] connection = new byte[6];

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        //this.direction = nbt.getByte("dir");
        this.connection = nbt.getByteArray("connection");
        if(this.connection.length < 6)
        {
        	this.connection = new byte[6];
        }
        this.searchConnection();
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByteArray("connection", this.connection);
        return nbt;
    }

	/**接続を再設定*/
	public void refresh()
	{
		this.searchConnection();
		this.sendPacket();
		this.markDirty();
	}

	public void searchConnection()
	{
		if(this.getWorld() == null){return;}

		for(int i = 0; i < 6; ++i)
		{
			int x0 = this.getPos().getX() + BlockUtil.facing[i][0];
			int y0 = this.getPos().getY() + BlockUtil.facing[i][1];
			int z0 = this.getPos().getZ() + BlockUtil.facing[i][2];
			IBlockState state = BlockUtil.getBlockState(this.getWorld(), x0, y0, z0);
			Block block = state.getBlock();
			if(block == RTMBlock.slot)
			{
				this.connection[i] = 3;
			}
			else if(block == RTMBlock.pipe)
			{
				this.connection[i] = 2;
			}
			else if(state.isOpaqueCube())
			{
				this.connection[i] = 1;
			}
			else
			{
				this.connection[i] = 0;
			}
		}
	}

	public boolean isConnected(byte side)
	{
		return this.connection[side] == 2 || this.connection[side] == 3;
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.ORNAMENT_PIPE;
	}
}