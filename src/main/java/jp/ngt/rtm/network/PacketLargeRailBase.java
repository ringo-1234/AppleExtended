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

package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.event.TickProcessEntry;
import jp.ngt.ngtlib.event.TickProcessQueue;
import jp.ngt.ngtlib.network.PacketCustom;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketLargeRailBase extends PacketCustom implements IMessageHandler<PacketLargeRailBase, IMessage> {
    public static final byte RETRY = 50;
    public static final byte INTERVAL = 5;

    private int sX, sY, sZ;

    public PacketLargeRailBase() {
    }

    public PacketLargeRailBase(TileEntityLargeRailBase tileEntity) {
        super(tileEntity);
        this.sX = tileEntity.getStartPoint()[0];
        this.sY = tileEntity.getStartPoint()[1];
        this.sZ = tileEntity.getStartPoint()[2];
		/*if(this.sY <= 0)
		{
			throw new IllegalArgumentException("Rail's position is invalid");
		}*/
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        super.toBytes(buffer);
        buffer.writeInt(this.sX);
        buffer.writeInt(this.sY);
        buffer.writeInt(this.sZ);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        super.fromBytes(buffer);
        this.sX = buffer.readInt();
        this.sY = buffer.readInt();
        this.sZ = buffer.readInt();
    }

    @Override
    public IMessage onMessage(PacketLargeRailBase message, MessageContext ctx) {
        this.receivePacket(message);
        return null;
    }

    private void receivePacket(final PacketLargeRailBase message) {
        TickProcessQueue.getInstance(Side.CLIENT).add(new TickProcessEntry() {
            @Override
            public boolean process(World world) {
                TileEntity tile = message.getTileEntity(world);
                if (tile instanceof TileEntityLargeRailBase) {
                    ((TileEntityLargeRailBase) tile).setStartPoint(message.sX, message.sY, message.sZ);
                    return true;
                }
                return false;
            }
        }, RETRY, INTERVAL);
    }
}