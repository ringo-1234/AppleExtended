package jp.ngt.ngtlib.block;

import jp.ngt.ngtlib.event.TickProcessEntry;
import jp.ngt.ngtlib.event.TickProcessQueue;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCustom extends TileEntity
{
	public void setPos(int x, int y, int z, int prevX, int prevY, int prevZ)
	{
		this.setPos(new BlockPos(x, y, z));
	}

	public int getX()
	{
		return this.getPos().getX();
	}

	public int getY()
	{
		return this.getPos().getY();
	}

	public int getZ()
	{
		return this.getPos().getZ();
	}

	//このタイミングだとパケットが届かない
	/*@Override
	public void onLoad()
    {
		super.onLoad();
		this.sendPacket();
    }*/

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
    {
		final World world = this.getWorld();
		TickProcessQueue.getInstance(Side.SERVER).add(new TickProcessEntry(){
			@Override
			public boolean process(World world)
			{
				TileEntityCustom.this.sendPacket();
				return true;
			}
		}, 40);//遅延させると届く、下のはレールで使えないため
		return new SPacketUpdateTileEntity(this.pos, -1, this.getUpdateTag());//null返すと届かない？
    }

	@Override
	public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
		this.readFromNBT(pkt.getNbtCompound());
    }

	protected void sendPacket()
	{
		if(this.world == null || !this.world.isRemote)
		{
			PacketNBT.sendToClient(this);
    	}
	}

    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared()
    {
    	return NGTUtil.getChunkLoadDistanceSq();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
    	return INFINITE_EXTENT_AABB;
    }
}