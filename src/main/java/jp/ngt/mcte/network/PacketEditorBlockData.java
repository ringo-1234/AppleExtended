package jp.ngt.mcte.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.mcte.editor.EntityEditor;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**Editorのブロックデータのやりとり専用*/
public class PacketEditorBlockData implements IMessage, IMessageHandler<PacketEditorBlockData, IMessage>
{
	public NBTTagCompound nbtData;

	public PacketEditorBlockData(){}

	public PacketEditorBlockData(EntityEditor entity, NBTTagCompound nbt)
	{
		this.nbtData = nbt;
		this.nbtData.setInteger("EntityId", entity.getEntityId());
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, this.nbtData);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.nbtData = ByteBufUtils.readTag(buffer);
	}

	public void onGetPacket(World world)
	{
		int id = this.nbtData.getInteger("EntityId");
		Entity entity = world.getEntityByID(id);
		if(entity instanceof EntityEditor)
		{
			((EntityEditor)entity).importBlocksFromNBT(this.nbtData);
		}
	}

	@Override
    public IMessage onMessage(PacketEditorBlockData message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.world;
		message.onGetPacket(world);
		return null;
	}
}