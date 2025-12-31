package jp.ngt.ngtlib.network;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.util.NGTCertificate;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNoticeHandlerClient implements IMessageHandler<PacketNotice, IMessage>
{
	@Override
    public IMessage onMessage(PacketNotice message, MessageContext ctx)
	{
		if(message.type == PacketNotice.Side_CLIENT)
		{
			if(message.notice.equals("regKey"))
			{
				NGTCertificate.writePlayerData(NGTCore.proxy.getUserName());
			}
		}
		return null;
	}
}