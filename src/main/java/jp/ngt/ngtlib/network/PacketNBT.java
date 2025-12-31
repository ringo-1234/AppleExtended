package jp.ngt.ngtlib.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.ngtlib.NGTCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketNBT extends PacketCustom
{
	private static final byte Type_Entity = 0;
	private static final byte Type_TileEntity = 1;
	private static final byte Type_PlayerItem = 2;
	public NBTTagCompound nbtData;

	public PacketNBT(){}

	private PacketNBT(Entity entity, boolean toClient)
	{
		super(entity);
		NBTTagCompound nbt = new NBTTagCompound();
		entity.writeToNBT(nbt);
		this.initPacket(entity, nbt, toClient);
	}

	private PacketNBT(TileEntity tileEntity, NBTTagCompound nbt, boolean toClient)
	{
		super(tileEntity);
		this.initPacket(tileEntity, nbt, toClient);
	}

	private PacketNBT(TileEntity tileEntity, boolean toClient)
	{
		super(tileEntity);
		NBTTagCompound nbt = new NBTTagCompound();
		tileEntity.writeToNBT(nbt);
		this.initPacket(tileEntity, nbt, toClient);
	}

	/**Playerの所持アイテムにNBT書き込む(Server行きのみ)*/
	private PacketNBT(EntityPlayer player, ItemStack stack)
	{
		super(player);
		this.nbtData = new NBTTagCompound();
		this.nbtData.setTag("TagData", stack.getTagCompound());
		this.nbtData.setBoolean("ToClient", false);
		this.nbtData.setByte("DataType", Type_PlayerItem);
	}

	private void initPacket(Entity entity, NBTTagCompound nbt, boolean toClient)
	{
		this.nbtData = nbt;
		this.nbtData.setBoolean("ToClient", toClient);
		this.nbtData.setByte("DataType", Type_Entity);
	}

	private void initPacket(TileEntity tileEntity, NBTTagCompound nbt, boolean toClient)
	{
		this.nbtData = nbt;
		this.nbtData.setBoolean("ToClient", toClient);
		this.nbtData.setByte("DataType", Type_TileEntity);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		ByteBufUtils.writeTag(buffer, this.nbtData);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		this.nbtData = ByteBufUtils.readTag(buffer);
	}

	/**Client側のみTPQ内から呼び出し(リトライ有り)*/
	protected boolean onGetPacket(World world)
	{
		if(world == null)
		{
			//NGTLog.debug("World is null (PacketNBT)");
			return false;
		}

		byte type = this.nbtData.getByte("DataType");
		if(type == Type_Entity)
		{
			Entity entity = this.getEntity(world);
			if(entity == null)
			{
				//NGTLog.debug("Entity not found (PacketNBT)");
			}
			else
			{
				entity.readFromNBT(this.nbtData);
				return true;
			}
		}
		else if(type == Type_TileEntity)
		{
			TileEntity tileEntity = this.getTileEntity(world);
			if(tileEntity == null)
			{
				//NGTLog.debug("TileEntity not found (PacketNBT)");
			}
			else
			{
				tileEntity.readFromNBT(this.nbtData);
				if(!world.isRemote)
				{
					tileEntity.markDirty();//セーブする
				}
				return true;
			}
		}
		else if(type == Type_PlayerItem)
		{
			Entity entity = this.getEntity(world);
			if(entity instanceof EntityPlayer)
			{
				ItemStack stack = ((EntityPlayer)entity).inventory.getCurrentItem();
				if(stack != null)
				{
					int index = ((EntityPlayer)entity).inventory.currentItem;
					NBTTagCompound data = this.nbtData.getCompoundTag("TagData");
					stack.setTagCompound(data);
					if(entity instanceof EntityPlayerMP)
					{
						this.updateCurrentItem((EntityPlayerMP)entity);
					}
					return true;
				}
			}
		}
		return false;
	}

	/**手持ちアイテムの同期*/
	private void updateCurrentItem(EntityPlayerMP player)//NetHandlerPlayerServer.processPlayerBlockPlacement()
	{
		Slot slot = player.inventoryContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
		player.connection.sendPacket(new SPacketSetSlot(player.inventoryContainer.windowId, slot.slotNumber, player.inventory.getCurrentItem()));
	}

	public static void sendToServer(Entity entity)
	{
		NGTCore.NETWORK_WRAPPER.sendToServer(new PacketNBT(entity, false));
	}

	public static void sendToServer(EntityPlayer player, ItemStack item)
	{
		NGTCore.NETWORK_WRAPPER.sendToServer(new PacketNBT(player, item));
	}

	public static void sendToServer(TileEntity entity)
	{
		NGTCore.NETWORK_WRAPPER.sendToServer(new PacketNBT(entity, false));
	}

	public static void sendToServer(TileEntity entity, NBTTagCompound nbt)
	{
		NGTCore.NETWORK_WRAPPER.sendToServer(new PacketNBT(entity, nbt, false));
	}

	public static void sendToClient(final Entity entity)
	{
		NGTCore.NETWORK_WRAPPER.sendToAll(new PacketNBT(entity, true));
		//TickProcessQueue.getInstance(Side.SERVER).add(entry);
	}

	public static void sendToClient(final TileEntity entity)
	{
		NGTCore.NETWORK_WRAPPER.sendToAll(new PacketNBT(entity, true));
	}
}