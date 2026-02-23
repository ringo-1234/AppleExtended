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
import java.util.List;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
public class CommandRTM extends CommandBase {
	@Override
	public String getName() {
		return "rtm";
	}

	@Override
	public String getUsage(ICommandSender commandSender) {
		return "commands.rtm.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayer player;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
			return;
		}

		if (args.length > 0) {
			if (args[0].equals("delAllTrain")) {
				int count = 0;
				List list = player.world.loadedEntityList;
				for (Object object : list) {
					Entity entity = null;
					if (object instanceof EntityTrainBase) {
						entity = (EntityTrainBase) object;
						++count;
					} else if (object instanceof EntityBogie) {
						entity = (EntityBogie) object;
					}

					if (entity != null && !entity.isDead) {
						entity.setDead();
					}
				}

				NGTLog.sendChatMessage(player, "Delete " + count + "trains.");
			} else if (args[0].equals("twitter_tag")) {
			} else if (args[0].equals("dismount")) {
				player.dismountRidingEntity();
			}

			return;
		}

		NGTLog.sendChatMessage(sender, "/rtm delAllTrain : Delete all train");
		NGTLog.sendChatMessage(sender, "/rtm dismount : Dismount player from vehicle");
	}
}