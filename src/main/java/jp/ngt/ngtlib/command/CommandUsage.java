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

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.util.Usage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandUsage extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "usage";
    }

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands.usage.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player;
        try {
            player = getCommandSenderAsPlayer(sender);
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Usage.INSTANCE.requestUsage(player);
    }

    public static void showUsage() {
        ITextComponent component = new TextComponentTranslation(TextFormatting.RED + NGTUtil.translate("message.usage"));
        NGTUtilClient.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
    }
}