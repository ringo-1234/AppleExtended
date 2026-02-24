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

package jp.ngt.mcte.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.mcte.editor.filter.FilterManager;
import jp.ngt.ngtlib.event.TickProcessQueue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketFilter implements IMessage, IMessageHandler<PacketFilter, IMessage>
{
	private String playerName;
	private String filterName;
	private String cfgData;
	private String script;

	public PacketFilter(){}

	public PacketFilter(EntityPlayer player, String name, String data, String script)
	{
		this.playerName = player.getName();
		this.filterName = name;
		this.cfgData = data;
		this.script = script;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.playerName);
		ByteBufUtils.writeUTF8String(buffer, this.filterName);
		ByteBufUtils.writeUTF8String(buffer, this.cfgData);
		ByteBufUtils.writeUTF8String(buffer, this.script);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.playerName = ByteBufUtils.readUTF8String(buffer);
		this.filterName = ByteBufUtils.readUTF8String(buffer);
		this.cfgData = ByteBufUtils.readUTF8String(buffer);
		this.script = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
    public IMessage onMessage(PacketFilter message, MessageContext ctx)
	{
		World w2 = ctx.getServerHandler().player.world;
		EntityPlayer player = w2.getPlayerEntityByName(message.playerName);
		TickProcessQueue.getInstance(Side.SERVER).add((world)->{
			FilterManager.INSTANCE.execFilter(player, message.filterName, message.cfgData, message.script);
			return true;});
		return null;
	}
}