package jp.ngt.rtm.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.rail.TileEntityMarker;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMarker extends PacketCustom implements IMessageHandler<PacketMarker, IMessage>
{
	private List<BlockPos> list;

	public PacketMarker(){}

	public PacketMarker(TileEntityMarker marker, List<BlockPos> par4)
	{
		super(marker);
		this.list = par4;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);

		buffer.writeInt(this.list.size());
		for(BlockPos pos : this.list)
		{
			buffer.writeInt(pos.getX());
			buffer.writeInt(pos.getY());
			buffer.writeInt(pos.getZ());
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);

		int size = buffer.readInt();
		this.list = new ArrayList<>();
		for(int i = 0; i < size; ++i)
		{
			int i0 = buffer.readInt();
			int i1 = buffer.readInt();
			int i2 = buffer.readInt();
			this.list.add(new BlockPos(i0, i1, i2));
		}
	}

	@Override
    public IMessage onMessage(PacketMarker message, MessageContext ctx)
	{
		World world = NGTUtil.getClientWorld();
		TileEntity tile = message.getTileEntity(world);
		if(tile instanceof TileEntityMarker)
		{
			((TileEntityMarker)tile).setMarkersPos(message.list);
		}
		return null;
	}
}