package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class TileEntityTicketVendor extends TileEntityMachineBase implements IInventory
{
	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return null;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int par1)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2)
	{
		;
	}

	@Override
	public String getName()
	{
		return "TicketVendor";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		;
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		;
	}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2)
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int getField(int id) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public int getFieldCount() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void clear() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.MACHINE_VENDOR;
	}

	@Override
	public boolean isEmpty() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}