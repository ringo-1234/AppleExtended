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

package jp.apple.arse.network;

import io.netty.buffer.ByteBuf;
import jp.apple.arse.tileentity.TileEntitySounder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketArseSetSound implements IMessage {
    private BlockPos pos;
    private String sound1;
    private String sound2;
    private boolean loop;
    private boolean useRSIF;

    public PacketArseSetSound() {
    }

    public PacketArseSetSound(BlockPos pos, String sound1, String sound2, boolean loop, boolean useRSIF) {
        this.pos = pos;
        this.sound1 = sound1;
        this.sound2 = sound2;
        this.loop = loop;
        this.useRSIF = useRSIF;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos.toLong());
        writeString(buffer, sound1);
        writeString(buffer, sound2);
        buffer.writeBoolean(loop);
        buffer.writeBoolean(useRSIF);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.pos = BlockPos.fromLong(buffer.readLong());
        this.sound1 = readString(buffer);
        this.sound2 = readString(buffer);
        this.loop = buffer.readBoolean();
        this.useRSIF = buffer.readBoolean();
    }

    private void writeString(ByteBuf buffer, String s) {
        byte[] bytes = s.getBytes();
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    private String readString(ByteBuf buffer) {
        int len = buffer.readInt();
        byte[] bytes = new byte[len];
        buffer.readBytes(bytes);
        return new String(bytes);
    }

    public static class Handler implements IMessageHandler<PacketArseSetSound, IMessage> {
        @Override
        public IMessage onMessage(PacketArseSetSound message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                TileEntity te = ctx.getServerHandler().player.world.getTileEntity(message.pos);
                if (te instanceof TileEntitySounder) {
                    TileEntitySounder s = (TileEntitySounder) te;
                    s.setSelectedSound(message.sound1);
                    s.setSelectedSound2(message.sound2);
                    s.setLoop(message.loop);
                    s.setUseRSIF(message.useRSIF);
                }
            });
            return null;
        }
    }
}
