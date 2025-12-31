package jp.ngt.mcte.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.mcte.MCTEKeyHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMCTEKey implements IMessage, IMessageHandler<PacketMCTEKey, IMessage>
{
	private String playerName;
	private byte keyId;

	public PacketMCTEKey(){}

	public PacketMCTEKey(EntityPlayer par1Entity, byte par2Key)
	{
		this.playerName = par1Entity.getName();
		this.keyId = par2Key;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.playerName);
		buffer.writeByte(this.keyId);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.playerName = ByteBufUtils.readUTF8String(buffer);
		this.keyId = buffer.readByte();
	}

	@Override
    public IMessage onMessage(PacketMCTEKey message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.world;
		EntityPlayer player = world.getPlayerEntityByName(message.playerName);
		MCTEKeyHandlerServer.INSTANCE.onKeyDown(player, message.keyId);
		return null;
	}
}