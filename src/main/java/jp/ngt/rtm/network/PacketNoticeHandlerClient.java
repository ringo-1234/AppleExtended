package jp.ngt.rtm.network;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.decoration.DecorationStore;
import jp.ngt.rtm.block.tileentity.TileEntityMovingMachine;
import jp.ngt.rtm.entity.npc.macro.MacroRecorder;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.modelpack.state.DataMap;
import jp.ngt.rtm.rail.TileEntityTurnTableCore;
import jp.ngt.rtm.sound.SpeakerSounds;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNoticeHandlerClient implements IMessageHandler<PacketNotice, IMessage>
{
	@Override
    public IMessage onMessage(PacketNotice message, MessageContext ctx)
	{
		String msg = message.notice;

		if((message.type & 1) == PacketNotice.Side_CLIENT)
		{
			World world = NGTUtil.getClientWorld();

			if(msg.equals("setConnected"))
			{
				RTMCore.proxy.setConnectionState((byte)1);
			}
			else if(msg.startsWith("changeDisplayList"))
			{
				;
			}
			else if(msg.startsWith("fire"))
			{
				Entity entity = message.getEntity(world);
				if(entity instanceof EntityArtillery)
				{
					((EntityArtillery)entity).onFirePacket(msg);
				}
			}
			else if(msg.startsWith("MM"))
			{
				String[] sa0 = msg.split(",");
				int v = Integer.parseInt(sa0[1]);
				TileEntity tile = message.getTileEntity(world);
				if(tile instanceof TileEntityMovingMachine)
				{
					((TileEntityMovingMachine)tile).setMovement((byte)v);
				}
			}
			else if(msg.startsWith("TRec"))
			{
				if(MacroRecorder.INSTANCE.isRecording())
				{
					MacroRecorder.INSTANCE.stop(world);
				}
				else
				{
					MacroRecorder.INSTANCE.start(world);
				}
			}
			else if(msg.startsWith("DM"))
			{
				if(world == null){return null;}
				DataMap.receivePacket(msg, message, world, true);
			}
			else if(msg.startsWith("TT"))
			{
				TileEntity tile = message.getTileEntity(world);
				if(tile instanceof TileEntityTurnTableCore)
				{
					float f0 = Float.valueOf(msg.split(":")[1]);
					((TileEntityTurnTableCore)tile).setRotation(f0);
				}
			}
			else if(message.notice.startsWith("decoration"))
			{
				String json = message.notice.replace("decoration:", "");
				DecorationStore.INSTANCE.setModel(json);
			}
			else if(msg.startsWith("speaker"))
			{
				SpeakerSounds.getInstance(false).onGetPacket(msg, false);
			}
			/*else if(msg.startsWith("col"))
			{
				String[] sa = msg.split(",");
				float x = Float.parseFloat(sa[1]);
				float y = Float.parseFloat(sa[2]);
				float z = Float.parseFloat(sa[3]);
				Entity target = message.getEntity(world);
				if(target != null)
				{
					target.setPosition(target.posX + x, target.posY + y, target.posZ + z);
				}
			}*/
		}
		return null;
	}
}