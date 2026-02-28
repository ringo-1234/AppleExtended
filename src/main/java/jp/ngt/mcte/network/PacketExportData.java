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

package jp.ngt.mcte.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.event.TickProcessEntry;
import jp.ngt.ngtlib.event.TickProcessQueue;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

/**
 * NGTObjectをClientのファイルへ出力
 */
public class PacketExportData implements IMessage, IMessageHandler<PacketExportData, IMessage> {
    private String fileName;
    private NGTObject blocksData;

    public PacketExportData() {
    }

    public PacketExportData(String par1, NGTObject par2) {
        this.fileName = par1;
        this.blocksData = par2;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.fileName);
        ByteBufUtils.writeTag(buffer, this.blocksData.writeToNBT());
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.fileName = ByteBufUtils.readUTF8String(buffer);
        this.blocksData = NGTObject.readFromNBT(ByteBufUtils.readTag(buffer));
    }

    @Override
    public IMessage onMessage(PacketExportData message, MessageContext ctx) {
        TickProcessQueue.getInstance(Side.CLIENT).add(new TickProcessEntry() {
            @Override
            public boolean process(World world) {
                File file = new File(message.fileName);
                message.blocksData.exportToFile(file);
                return true;
            }
        });
        return null;
    }
}