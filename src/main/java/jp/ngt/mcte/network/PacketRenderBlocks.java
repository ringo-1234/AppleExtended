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
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRenderBlocks implements IMessage, IMessageHandler<PacketRenderBlocks, IMessage>
{
	private int entityId;
	private byte flag;
	private NGTObject blocksData;

	public PacketRenderBlocks(){}

	public PacketRenderBlocks(EntityEditor par1, NGTObject ngto)
	{
		this.entityId = par1.getEntityId();

		if(ngto == null)
		{
			this.flag = 0;
		}
		else
		{
			this.blocksData = ngto;
			this.flag = 1;
		}
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(this.entityId);
		buffer.writeByte(this.flag);
		if(this.flag == 1)
		{
			ByteBufUtils.writeTag(buffer, this.blocksData.writeToNBT());
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.entityId = buffer.readInt();
		this.flag = buffer.readByte();
		if(this.flag == 1)
		{
			//サイズ制限:NBTSizeTracker(2097152L=2MB)
			this.blocksData = NGTObject.readFromNBT(ByteBufUtils.readTag(buffer));
		}
	}

	@Override
    public IMessage onMessage(PacketRenderBlocks message, MessageContext ctx)
	{
		World world = NGTUtil.getClientWorld();
		Entity entity = world.getEntityByID(message.entityId);
		if(entity instanceof EntityEditor)
		{
			EntityEditor editor = (EntityEditor)entity;
			if(message.flag == 1)
			{
				editor.blocksForRenderer = message.blocksData;
				editor.setUpdate(true);
			}
			else
			{
				editor.blocksForRenderer = null;
			}
		}
		return null;
	}
}