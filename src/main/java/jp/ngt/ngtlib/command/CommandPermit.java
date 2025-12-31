package jp.ngt.ngtlib.command;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.PermissionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandPermit extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
    {
        return 3;//OP権限
    }

	@Override
	public String getName()
	{
		return "permit";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.permit.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length > 0)
		{
			if(args[0].equals("list"))
			{
				PermissionManager.INSTANCE.showPermissionList(sender);
				return;
			}
			else if(args[0].equals("myname"))
			{
				NGTLog.sendChatMessage(sender, "My name is " + sender.getName());
				return;
			}
			else if(args.length >= 3)
			{
				String playerName = args[1];
				String target = args[2];

				if(args[0].equals("add"))
				{
					PermissionManager.INSTANCE.addPermission(sender, playerName, target);
					return;
				}
				else if(args[0].equals("remove"))
				{
					PermissionManager.INSTANCE.removePermission(sender, playerName, target);
					return;
				}
			}
		}

		NGTLog.sendChatMessage(sender, "/permit add <player or -all> <category>");
		NGTLog.sendChatMessage(sender, "/permit remove <player or -all> <category>");
		NGTLog.sendChatMessage(sender, "/permit list");
		NGTLog.sendChatMessage(sender, "/permit myname");
	}
}