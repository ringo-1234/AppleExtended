package jp.ngt.rtm.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandNPC extends CommandBase
{
	@Override
	public String getName()
	{
		return "npc";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.npc.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayer player;
		try
		{
			player = getCommandSenderAsPlayer(sender);
		}
		catch (PlayerNotFoundException e)
		{
			e.printStackTrace();
			return;
		}

		if(args.length == 2)
		{
			;//npc <target> <order>
		}
	}
}