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

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNoticeHandlerServer implements IMessageHandler<PacketNotice, IMessage>
{
	@Override
    public IMessage onMessage(PacketNotice message, MessageContext ctx)
	{
		if(message.type == PacketNotice.Side_SERVER)
		{
			if(message.notice.equals("isConnected"))
			{
				;
			}
		}
		return null;
	}
}