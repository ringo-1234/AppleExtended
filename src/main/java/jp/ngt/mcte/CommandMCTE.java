package jp.ngt.mcte;

import jp.ngt.mcte.block.RenderMiniature;
import jp.ngt.mcte.editor.Editor;
import jp.ngt.mcte.editor.EditorManager;
import jp.ngt.mcte.world.MCTETeleporter;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandMCTE extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getName()
	{
		return "mcte";
	}

	@Override
	public String getUsage(ICommandSender par1)
	{
		return "commands.mcte.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] strings) throws CommandException
	{
		if(strings.length >= 1)
		{
			if(strings[0].equals("clear"))
			{
				EditorManager.INSTANCE.removeAll();
				return;
			}
			else if(strings[0].equals("minesweeper"))
			{
				this.createMinesweeper(sender, strings);
				return;
			}
			else if(strings[0].equals("tp"))
			{
				this.teleport(sender, strings);
	    		return;
			}
			else if(strings[0].equals("clr_m"))
			{
				if(!NGTUtil.isSMP())
				{
					RenderMiniature.INSTANCE.clear();
				}
			}
		}

		this.help(sender);
	}

	private void help(ICommandSender sender)
	{
		NGTLog.sendChatMessage(sender, "/mcte clear : Clear all editors");
		NGTLog.sendChatMessage(sender, "/mcte minesweeper [difficulty(1~9)]");
		NGTLog.sendChatMessage(sender, "/mcte tp");
	}

	private void createMinesweeper(ICommandSender sender, String[] strings)
	{
		EntityPlayerMP player;
		try
		{
			player = getCommandSenderAsPlayer(sender);
		}
		catch(PlayerNotFoundException e1)
		{
			e1.printStackTrace();
			return;
		}

		Editor editor = EditorManager.INSTANCE.getEditor(player);
		if(editor == null)
		{
			NGTLog.sendChatMessage(player, "Please Select Range");
			return;
		}

		int difficulty = 1;
		if(strings.length == 2)
		{
			try{difficulty = Integer.parseInt(strings[1]);}catch(NumberFormatException e){}

			if(difficulty <= 0)
			{
				NGTLog.sendChatMessage(player, "Illegal number");
				difficulty = 1;
			}
		}
		else
		{
			switch(player.getEntityWorld().getDifficulty())
			{
			case PEACEFUL: difficulty = 9;
			case EASY: difficulty = 8;
			case NORMAL: difficulty = 7;
			case HARD: difficulty = 6;
			}
		}

		editor.editBlocks(Editor.EditType_Minesweeper, difficulty);
	}

	private void teleport(ICommandSender sender, String[] strings)
	{
		try
		{
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			player.timeUntilPortal = 5;
			int dimId = MCTE.MEGA_CITY.getId();
			if(player.world.provider.getDimensionType().getId() == dimId)
			{
				dimId = 0;
			}
			player.mcServer.getPlayerList().transferPlayerToDimension(player, dimId,
					new MCTETeleporter(player.mcServer.getWorld(dimId)));
		}
		catch(PlayerNotFoundException e1)
		{
			e1.printStackTrace();
			return;
		}
	}
}