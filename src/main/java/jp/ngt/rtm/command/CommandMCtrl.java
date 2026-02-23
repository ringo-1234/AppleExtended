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

package jp.ngt.rtm.command;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.io.NGTLog;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

public class CommandMCtrl extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getName()
	{
		return "mctrl";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.mctrl.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length == 0 || "help".equals(args[0]))
		{
			this.help(sender);
			return;
		}

		if(args.length < 3)
		{
			NGTLog.sendChatMessage(sender, "Invalid command.");
			return;
		}

		ModelCtrl ctrl = ModelCtrl.getCommand(args[1]);
		List list = this.getTargets(sender, args[0], ctrl);
		if(list.isEmpty())
		{
			NGTLog.sendChatMessage(sender, "Target not found.");
			return;
		}

		for(Object obj : list)
		{
			ctrl.executor.exec(obj, sender, args[1], args[2]);
		}
	}

	private List getTargets(ICommandSender player, String filter, ModelCtrl ctrl)
	{
		List list = new ArrayList<>();
		List<Entity> allEntities = player.getEntityWorld().loadedEntityList;//getterはsideonly
		double distanceSq = Double.MAX_VALUE;
		for(Entity entity : allEntities)
		{
			if(ctrl.filter.match(entity))
			{
				if(filter.equals("@a"))
				{
					list.add(entity);
				}
				else if(filter.equals("@n"))
				{
					double d1 = entity.getDistanceSq(player.getPosition());
					if(d1 < distanceSq)
					{
						list.clear();
						list.add(entity);
					}
				}
				else if(filter.startsWith("@r"))
				{
					int range = Integer.valueOf(filter.replace("@r:", ""));
					double d1 = entity.getDistanceSq(player.getPosition());
					if(d1 <= range * range)
					{
						list.add(entity);
					}
				}
				else if(filter.equals(entity.getName()))
				{
					list.add(entity);
				}
			}
		}
		return list;
	}

	private void help(ICommandSender player)
	{
		NGTLog.sendChatMessage(player, "Target filter -> @a...all, @n...nearest, @r:00...range, or name");
		for(ModelCtrl mc : ModelCtrl.values())
		{
			if(!mc.discription.isEmpty())
			{
				NGTLog.sendChatMessage(player, mc.discription);
			}
		}
	}
}