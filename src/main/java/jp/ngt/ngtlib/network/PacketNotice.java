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

package jp.ngt.ngtlib.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketNotice implements IMessage {
    public static final byte Side_SERVER = 0;
    public static final byte Side_CLIENT = 1;
    public byte type;
    public String notice;

    public PacketNotice() {
    }

    public PacketNotice(byte par1, String par2) {
        this.type = par1;
        this.notice = par2;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(this.type);
        ByteBufUtils.writeUTF8String(buffer, this.notice);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.type = buffer.readByte();
        this.notice = ByteBufUtils.readUTF8String(buffer);
    }
}