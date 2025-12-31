package jp.ngt.rtm.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public final class RTMCommand
{
	public static void init(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandNPC());
		event.registerServerCommand(new CommandRTM());
		event.registerServerCommand(new CommandMCtrl());
		event.registerServerCommand(new CommandTRec());
	}
}