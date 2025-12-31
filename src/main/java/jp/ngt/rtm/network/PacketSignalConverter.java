package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.rtm.electric.TileEntitySignalConverter;
import jp.ngt.rtm.electric.TileEntitySignalConverter.ComparatorType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSignalConverter extends PacketCustom implements IMessageHandler<PacketSignalConverter, IMessage>
{
	private int comparatorIndex;
	private int signal1, signal2;

	public PacketSignalConverter(){}

	public PacketSignalConverter(TileEntitySignalConverter par1, int par2, int par3, int par4)
	{
		super(par1);
		this.comparatorIndex = par2;
		this.signal1 = par3;
		this.signal2 = par4;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(this.comparatorIndex);
		buffer.writeInt(this.signal1);
		buffer.writeInt(this.signal2);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		this.comparatorIndex = buffer.readInt();
		this.signal1 = buffer.readInt();
		this.signal2 = buffer.readInt();
	}

	@Override
    public IMessage onMessage(PacketSignalConverter message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.world;
		TileEntitySignalConverter tile = (TileEntitySignalConverter)message.getTileEntity(world);
		tile.setSignalProp(message.signal1, message.signal2, ComparatorType.getType(message.comparatorIndex));
		return null;
	}
}