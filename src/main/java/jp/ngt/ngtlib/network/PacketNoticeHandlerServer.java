package jp.ngt.ngtlib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNoticeHandlerServer implements IMessageHandler<PacketNotice, IMessage>
{
	@Override
    public IMessage onMessage(PacketNotice message, MessageContext ctx)
	{
		if(message.type == PacketNotice.Side_SERVER)
		{
			if(message.notice.equals("isConnected"))
			{
				;
			}
		}
		return null;
	}
}