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
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.electric.TileEntityElectricalWiring;
import jp.ngt.rtm.entity.EntityElectricalWiring;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketWire extends PacketCustom implements IMessageHandler<PacketWire, IMessage>
{
	public static final int ARRAY_SIZE = 4;

	private boolean isBlock, isActivated;
	private NBTTagCompound nbtData;

	public PacketWire(){}

	public PacketWire(EntityElectricalWiring entity, TileEntityElectricalWiring tileEntity)
	{
		super(entity);
		this.isBlock = false;
		this.isActivated = tileEntity.isActivated;
		this.nbtData = new NBTTagCompound();
		tileEntity.writeToNBT(this.nbtData);
	}

	public PacketWire(TileEntityElectricalWiring tileEntity)
	{
		super(tileEntity);
		this.isBlock = true;
		this.isActivated = tileEntity.isActivated;
		this.nbtData = new NBTTagCompound();
		tileEntity.writeToNBT(this.nbtData);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeBoolean(this.isBlock);
		buffer.writeBoolean(this.isActivated);
		ByteBufUtils.writeTag(buffer, this.nbtData);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		this.isBlock = buffer.readBoolean();
		this.isActivated = buffer.readBoolean();
		this.nbtData = ByteBufUtils.readTag(buffer);
	}

	@Override
    public IMessage onMessage(final PacketWire message, MessageContext ctx)
	{
		TickProcessQueue.getInstance(Side.CLIENT).add(new TickProcessEntry()
		{
			@Override
			public boolean process(World world)
			{
				return PacketWire.this.processPacket(message);
			}
		}, 20, 5);
		return null;
	}

	protected boolean processPacket(PacketWire message)
	{
		World world = NGTUtil.getClientWorld();
		TileEntityElectricalWiring tile = null;
		if(message.isBlock)
		{
			TileEntity tile1 = message.getTileEntity(world);
			if(tile1 instanceof TileEntityElectricalWiring)
			{
				tile = (TileEntityElectricalWiring)tile1;
			}
		}
		else
		{
			Entity entity = message.getEntity(world);
			if(entity instanceof EntityElectricalWiring)
			{
				tile = ((EntityElectricalWiring)entity).tileEW;
			}
		}

		if(tile != null)
		{
			tile.isActivated = message.isActivated;
			tile.readFromNBT(message.nbtData);
			return true;
		}

		return false;
	}
}