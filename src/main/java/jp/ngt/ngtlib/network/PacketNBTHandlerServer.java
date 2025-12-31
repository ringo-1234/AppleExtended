package jp.ngt.ngtlib.network;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNBTHandlerServer implements IMessageHandler<PacketNBT, IMessage>
{
	@Override
    public IMessage onMessage(PacketNBT message, MessageContext ctx)
	{
		if(!message.nbtData.getBoolean("ToClient"))
		{
			World world = ctx.getServerHandler().player.world;
			message.onGetPacket(world);
		}
		return null;
	}
}