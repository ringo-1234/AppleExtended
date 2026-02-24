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
import jp.ngt.ngtlib.block.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import jp.apple.log.AppleLogger;

public class PacketGenerator implements IMessage, IMessageHandler<PacketGenerator, IMessage>
{
	private int x;
	private int y;
	private int z;
	private String blockName;
	private byte metadata;

	public PacketGenerator(){}

	/**ブロックの情報を1つずつ送る*/
	public PacketGenerator(int par1, int par2, int par3, String par4, byte par5)
	{
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.blockName = par4;
		this.metadata = par5;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.z);
		ByteBufUtils.writeUTF8String(buffer, this.blockName);
		buffer.writeByte(this.metadata);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.blockName = ByteBufUtils.readUTF8String(buffer);
		this.metadata = buffer.readByte();
	}

	@Override
    public IMessage onMessage(PacketGenerator message, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().player;
		World world = ctx.getServerHandler().player.world;
		Block block = Block.getBlockFromName(message.blockName);
		if(block != null && world.isBlockLoaded(new BlockPos(message.x, message.y, message.z), false))
		{
			//ここからカラカラ
			AppleLogger.logBlockChange(
					player,
					new BlockPos(message.x, message.y, message.z),
					block.getStateFromMeta(message.metadata),
					"MCTE_GEN"
			);
			//終わりンゴンゴンゴ
			BlockUtil.setBlock(world, message.x, message.y, message.z, block, message.metadata, 2);
			return null;
		}
		return null;
	}
}