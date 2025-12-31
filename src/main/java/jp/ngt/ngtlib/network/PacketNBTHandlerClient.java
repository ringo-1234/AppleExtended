package jp.ngt.ngtlib.network;

import jp.ngt.ngtlib.event.TickProcessEntry;
import jp.ngt.ngtlib.event.TickProcessQueue;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketNBTHandlerClient implements IMessageHandler<PacketNBT, IMessage>
{
	@Override
    public IMessage onMessage(final PacketNBT message, MessageContext ctx)
	{
		if(message.nbtData == null)
		{
			return null;
		}

		final World world = NGTUtil.getClientWorld();

		if(message.nbtData.getBoolean("ToClient"))
		{
			TickProcessQueue.getInstance(Side.CLIENT).add(new TickProcessEntry(){
				@Override
				public boolean process(World par1)
				{
					return message.onGetPacket(world);
				}
			}, 50, 5);
		}
		return null;
	}
}