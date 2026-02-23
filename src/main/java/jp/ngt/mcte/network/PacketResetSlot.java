/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.mcte.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.mcte.editor.EntityEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketResetSlot implements IMessage, IMessageHandler<PacketResetSlot, IMessage>
{
	public String playerName;
	public int slotNumber;
	public int xPosition;
	public int yPosition;

	public PacketResetSlot(){}

	public PacketResetSlot(EntityEditor par1, Slot par2)
	{
		this.playerName = par1.getPlayer().getName();
		this.slotNumber = par2.slotNumber;
		this.xPosition = par2.xPos;
		this.yPosition = par2.yPos;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.playerName);
		buffer.writeInt(this.slotNumber);
		buffer.writeInt(this.xPosition);
		buffer.writeInt(this.yPosition);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.playerName = ByteBufUtils.readUTF8String(buffer);
		this.slotNumber = buffer.readInt();
		this.xPosition = buffer.readInt();
		this.yPosition = buffer.readInt();
	}

	@Override
    public IMessage onMessage(PacketResetSlot message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.world;
		EntityPlayer player = world.getPlayerEntityByName(message.playerName);
		if(player != null)
		{
			Slot slot = player.openContainer.getSlot(message.slotNumber);
			if(slot != null)
			{
				slot.xPos = message.xPosition;
				slot.yPos = message.yPosition;
				//NGTLog.debug("[RTM](Server) Reset Slot Position : " + message.slotNumber);
			}
		}
		return null;
	}
}