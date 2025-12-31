package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.electric.TileEntitySignal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSignal extends PacketCustom implements IMessageHandler<PacketSignal, IMessage>
{
	private int level;

	public PacketSignal(){}

	public PacketSignal(TileEntitySignal tileEntity, int par4)
	{
		super(tileEntity);
		this.level = par4;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(this.level);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		this.level = buffer.readInt();
	}

	@Override
    public IMessage onMessage(PacketSignal message, MessageContext ctx)
	{
		World world = NGTUtil.getClientWorld();
		TileEntity tile = message.getTileEntity(world);
		if(tile instanceof TileEntitySignal)
		{
			((TileEntitySignal)tile).setSignal(message.level);
		}
		return null;
	}
}