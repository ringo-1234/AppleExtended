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

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.item.ItemProtectionKey;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandProtection extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
    {
        return 3;//OP権限
    }

	@Override
	public String getName()
	{
		return "protect";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.protect.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
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

		if(args.length > 0)
		{
			ItemStack stack = ItemProtectionKey.getKey(args[0]);
			player.entityDropItem(stack, 0.5F);
			NGTLog.sendChatMessage(player, "Give Key [%s]", args[0]);
			return;
		}

		NGTLog.sendChatMessage(player, "Invalid command");
	}
}