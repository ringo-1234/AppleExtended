package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncItem implements IMessage, IMessageHandler<PacketSyncItem, IMessage>
{
	private String playerName;
	private ItemStack item;
	public int slotNumber;

	public PacketSyncItem(){}

	public PacketSyncItem(EntityPlayer par1, ItemStack par2, int par3)
	{
		this.playerName = par1.getName();
		this.item = par2;
		this.slotNumber = par3;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.playerName);
		ByteBufUtils.writeItemStack(buffer, this.item);
		buffer.writeInt(this.slotNumber);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.playerName = ByteBufUtils.readUTF8String(buffer);
		this.item = ByteBufUtils.readItemStack(buffer);
		this.slotNumber = buffer.readInt();
	}

	@Override
    public IMessage onMessage(PacketSyncItem message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.world;
		EntityPlayer player = world.getPlayerEntityByName(message.playerName);
		if(player != null)
		{
			player.openContainer.getSlot(message.slotNumber).putStack(message.item);
			player.openContainer.detectAndSendChanges();
		}
		return null;
	}
}
