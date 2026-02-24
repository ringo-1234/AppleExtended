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

package jp.ngt.ngtlib.command;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.network.PacketNotice;
import jp.ngt.ngtlib.renderer.media.MediaBase;
import jp.ngt.ngtlib.util.NGTCertificate;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandNGT extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getName()
	{
		return "ngt";
	}

	@Override
	public String getUsage(ICommandSender icommandsender)
	{
		return "commands.ngt.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length > 0)
		{
			if(args[0].equals("clearMedia") && !NGTUtil.isSMP())
			{
				MediaBase.clear();
				return;
			}
		}

		EntityPlayerMP player;
		try
		{
			player = getCommandSenderAsPlayer(sender);
		}
		catch (PlayerNotFoundException e)
		{
			e.printStackTrace();
			return;
		}

		if(args.length == 1)
		{
			if(NGTCertificate.registerKey(player, args[0]))
			{
				NGTCore.NETWORK_WRAPPER.sendTo(new PacketNotice(PacketNotice.Side_CLIENT, "regKey"), player);
			}
		}
		else
		{
			NGTLog.sendChatMessage(player, "commands.ngt.invalid_command");
		}
	}
}