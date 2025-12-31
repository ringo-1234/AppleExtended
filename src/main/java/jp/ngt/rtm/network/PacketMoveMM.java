package jp.ngt.rtm.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.entity.EntityMMBoundingBox;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMoveMM implements IMessage, IMessageHandler<PacketMoveMM, IMessage>
{
	private int[] entityIds;
	private double moveX;
	private double moveY;
	private double moveZ;

	public PacketMoveMM(){}

	public PacketMoveMM(int[] p1, double p2, double p3, double p4)
	{
		this.entityIds = p1;
		this.moveX = p2;
		this.moveY = p3;
		this.moveZ = p4;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(this.entityIds.length);
		for(int i : this.entityIds)
		{
			buffer.writeInt(i);
		}
		buffer.writeDouble(this.moveX);
		buffer.writeDouble(this.moveY);
		buffer.writeDouble(this.moveZ);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		int size = buffer.readInt();
		this.entityIds = new int[size];
		for(int i = 0; i < size; ++i)
		{
			this.entityIds[i] = buffer.readInt();
		}
		this.moveX = buffer.readDouble();
		this.moveY = buffer.readDouble();
		this.moveZ = buffer.readDouble();
	}

	@Override
    public IMessage onMessage(PacketMoveMM message, MessageContext ctx)
	{
		World world = NGTUtil.getClientWorld();
		if(world != null)
		{
			EntityMMBoundingBox.handleMMMovement(world, message.entityIds, message.moveX, message.moveY, message.moveZ);
		}
		return null;
	}
}