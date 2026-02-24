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

package jp.ngt.ngtlib.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.protection.ProtectionManager;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketProtection implements IMessage, IMessageHandler<PacketProtection, IMessage>
{
	private String name;
	private NBTTagCompound data;

	public PacketProtection(){}

	public PacketProtection(String par1, NBTTagCompound par2)
	{
		this.name = par1;
		this.data = par2;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.name);
		ByteBufUtils.writeTag(buffer, this.data);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.name = ByteBufUtils.readUTF8String(buffer);
		this.data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public IMessage onMessage(PacketProtection message, MessageContext ctx)
	{
		World world = NGTUtil.getClientWorld();
		if(world == null){return null;}

		ProtectionManager.INSTANCE.receivePacket(message.name, message.data);
		return null;
	}
}