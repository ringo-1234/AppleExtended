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
import jp.ngt.ngtlib.block.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**Entity, TileEntityへのPacket*/
public abstract class PacketCustom implements IMessage
{
	private NBTTagCompound targetData;

	public PacketCustom(){}

	public PacketCustom(Entity entity)
	{
		this.targetData = new NBTTagCompound();
		//World上に他Entityがいない場合、PlayerがID=0になる
		/*if(entity.getEntityId() <= 0)
		{
			throw new IllegalArgumentException("Entity ID is invalid");
		}*/
		this.targetData.setInteger("EntityId", entity.getEntityId());
	}

	public PacketCustom(TileEntity tileEntity)
	{
		this.targetData = new NBTTagCompound();
		BlockPos pos = tileEntity.getPos();
		if(pos.getY() <= 0)
		{
			throw new IllegalArgumentException("TileEntity's position is invalid");
		}
		this.targetData.setInteger("PosX", pos.getX());
		this.targetData.setInteger("PosY", pos.getY());
		this.targetData.setInteger("PosZ", pos.getZ());
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, this.targetData);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.targetData = ByteBufUtils.readTag(buffer);
	}

	//鯖側NBT読み出し時などではClientで
	public Entity getEntity(World world)
	{
		int id = this.targetData.getInteger("EntityId");
		Entity entity = world.getEntityByID(id);
		return entity;
	}

	public TileEntity getTileEntity(World world)
	{
		int x = this.targetData.getInteger("PosX");
		int y = this.targetData.getInteger("PosY");
		int z = this.targetData.getInteger("PosZ");
		TileEntity entity = BlockUtil.getTileEntity(world, x, y, z);
		return entity;
	}

	public boolean forEntity()
	{
		return this.targetData.hasKey("EntityId");
	}
}