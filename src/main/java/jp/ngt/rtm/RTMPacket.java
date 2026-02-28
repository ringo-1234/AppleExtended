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

package jp.ngt.rtm;

import jp.ngt.rtm.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

public final class RTMPacket {
    private static short packetId;

    public static void init() {
        registerPacket(PacketLargeRailBase.class, PacketLargeRailBase.class, Side.CLIENT);
        registerPacket(PacketModelSet.class, PacketModelSet.class, Side.CLIENT);
        registerPacket(PacketPlaySound.class, PacketPlaySound.class, Side.CLIENT);
        registerPacket(PacketLargeRailCore.class, PacketLargeRailCore.class, Side.CLIENT);
        registerPacket(PacketNoticeHandlerClient.class, PacketNotice.class, Side.CLIENT);
        registerPacket(PacketNoticeHandlerServer.class, PacketNotice.class, Side.SERVER);
        registerPacket(PacketRTMKey.class, PacketRTMKey.class, Side.SERVER);
        registerPacket(PacketSelectResource.class, PacketSelectResource.class, Side.SERVER);
        registerPacket(PacketSignal.class, PacketSignal.class, Side.CLIENT);
        registerPacket(PacketWire.class, PacketWire.class, Side.CLIENT);
        registerPacket(PacketSetTrainState.class, PacketSetTrainState.class, Side.SERVER);
        registerPacket(PacketModelPack.class, PacketModelPack.class, Side.CLIENT);
        registerPacket(PacketVehicleMovement.class, PacketVehicleMovement.class, Side.CLIENT);
        registerPacket(PacketMarker.class, PacketMarker.class, Side.CLIENT);
        registerPacket(PacketMarkerRPClient.class, PacketMarkerRPClient.class, Side.SERVER);
        registerPacket(PacketFormation.class, PacketFormation.class, Side.CLIENT);
        registerPacket(PacketSignalConverter.class, PacketSignalConverter.class, Side.SERVER);
        registerPacket(PacketMovingMachine.class, PacketMovingMachine.class, Side.SERVER);
        registerPacket(PacketMoveMM.class, PacketMoveMM.class, Side.CLIENT);
        registerPacket(PacketCollisionObj.class, PacketCollisionObj.class, Side.SERVER);
        registerPacket(PacketSyncItem.class, PacketSyncItem.class, Side.SERVER);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        RTMCore.NETWORK_WRAPPER.registerMessage(messageHandler, requestMessageType, packetId++, side);
    }
}