package jp.ngt.rtm.command;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTRec extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getName()
	{
		return "trec";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.trec.usage";
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
		RTMCore.NETWORK_WRAPPER.sendTo(new PacketNotice(PacketNotice.Side_CLIENT, "TRec"), player);
	}
}