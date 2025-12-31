package jp.ngt.ngtlib.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class NGTLog
{
	private static final Logger logger = LogManager.getLogger("NGT");

	public static void debug(String par1)
	{
		debug(par1, new Object[0]);
	}

	public static void debug(String par1, Object... par2)
	{
		try
		{
			String message = par1;
			if(par2 != null && par2.length > 0)
			{
				message = String.format(par1, par2);
			}
			logger.log(Level.INFO, message);
		}
		catch(Exception e)
		{
			;//MultiServerで出る?
		}
	}

	/*
	player.sendChatToPlayer(
		ChatMessageComponent.createFromTranslationWithSubstitutions(
			"commands.message.display.outgoing",
			new Object[]{player.getCommandSenderName(), message}
		).setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true))
	);
	*/

	/**フォーマットはこちらで行う*/
	public static void sendChatMessage(ICommandSender player, String message, Object... objects)//ServerCommandManager
	{
		player.sendMessage(new TextComponentTranslation(message, objects));
	}

	/**フォーマットはこちらで行う*/
	public static void sendChatMessageToAll(String message, Object... objects)
	{
		if(NGTUtil.getServer() == null)
		{
			debug("[NGTLog] Can't send message. This is client.");
		}
		else
		{
			NGTUtil.getServer().getPlayerList().sendMessage(new TextComponentTranslation(message, objects));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void showChatMessage(ITextComponent component)
	{
		NGTUtilClient.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
	}

	@SideOnly(Side.CLIENT)
	public static void showChatMessage(String message, Object... objects)
	{
		showChatMessage(new TextComponentString(String.format(message, objects)));
	}

	private static final List<Long> startTimes = new ArrayList();

	public static void startTimer()
	{
		startTimes.add(System.currentTimeMillis());
	}

	public static void resetTimer(String msg)
	{
		stopTimer(msg);
		startTimer();
	}

	/**出力形式 "msg:0000ms"*/
	public static void stopTimer(String msg)
	{
		if(startTimes.isEmpty())
		{
			NGTLog.debug("Timer is not started");
		}
		else
		{
			long start = startTimes.remove(startTimes.size() - 1);
			long time = System.currentTimeMillis() - start;
			NGTLog.debug(msg + ":" + time + "ms");
		}
	}
}