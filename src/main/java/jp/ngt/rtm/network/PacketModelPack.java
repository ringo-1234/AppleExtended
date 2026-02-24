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

package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.init.ModelPackWriter;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketModelPack implements IMessage, IMessageHandler<PacketModelPack, IMessage>
{
	public static final ModelPackWriter MP_WRITER = new ModelPackWriter();

	private String name;
	private long size;
	private byte[] bytes;

	public PacketModelPack(){}

	public PacketModelPack(String par1, long par2, ByteBuffer par3)
	{
		this.name = par1;
		this.size = par2;
		this.bytes = par3.array();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.name);
		buffer.writeLong(this.size);
		buffer.writeBytes(this.bytes);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.name = ByteBufUtils.readUTF8String(buffer);
		this.size = buffer.readLong();
		this.readBytesFromBuffer(buffer);
	}

	private void readBytesFromBuffer(ByteBuf buffer)
	{
		int index = RTMCore.PACKET_SIZE;
		int i0 = buffer.writerIndex() - buffer.readerIndex();
		if(index > i0)
		{
			index = i0;
		}
		this.bytes = new byte[index];
		buffer.readBytes(this.bytes);
	}

	@Override
	public IMessage onMessage(PacketModelPack message, MessageContext ctx)
	{
		MP_WRITER.onPacket(message.name, message.size, message.bytes);
		return null;
	}
}