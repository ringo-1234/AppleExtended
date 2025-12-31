package jp.ngt.rtm.item;

import java.util.List;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemWithModel<T extends ResourceSet> extends ItemCustom implements IResourceSelector
{
	/**モデル選択時の読み書きに使用*/
	//@SideOnly(Side.CLIENT)
	private ItemStack selectedItem;
	//@SideOnly(Side.CLIENT)
	private EntityPlayer selectedPlayer;

	public ItemWithModel()
	{
		super();
		this.setHasSubtypes(true);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		if(holder.getWorld().isRemote)
		{
			if(this.getModelType(holder.getItemStack()) != null)
			{
				this.selectedItem = holder.getItemStack();
				this.selectedPlayer = holder.getPlayer();
				holder.getPlayer().openGui(RTMCore.instance, this.getGuiId(holder.getItemStack()), holder.getWorld(), 0, -1, 0);
			}
			else
			{
				NGTLog.debug("No Type");
			}
		}
		return holder.success();
    }

	public abstract int getGuiId(ItemStack stack);

	@SideOnly(Side.CLIENT)
	@Override
	protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag)
    {
		if(ModelPackManager.INSTANCE.modelLoaded)//モデルロード前に呼ばれることの対策
		{
			ResourceState state = this.getModelState(holder.getItemStack());
			if(state != null)
			{
				list.add(TextFormatting.GRAY + state.getResourceName());
			}
		}
    }

	/**@return モデルパックの種類(null可)*/
	protected abstract ResourceType getModelType(ItemStack itemStack);

	public ResourceState<T> getModelState(ItemStack itemStack)
	{
		ResourceType type = this.getModelType(itemStack);
		if(type != null)
		{
			ResourceState<T> state = this.getNewState(itemStack, type);
			if(itemStack.hasTagCompound())
			{
				state.readFromNBT(itemStack.getTagCompound().getCompoundTag("State"));
			}
			else
			{
				state.setResourceName(type.defaultName);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setTag("State", state.writeToNBT());
				itemStack.setTagCompound(nbt);
			}
			return state;
		}
		return null;
	}

	protected abstract ResourceState<T> getNewState(ItemStack itemStack, ResourceType type);

	public void setModelState(ItemStack itemStack, ResourceState<T> state)
	{
		if(!itemStack.hasTagCompound())
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		itemStack.getTagCompound().setTag("State", state.writeToNBT());

		if(this.selectedPlayer != null)
		{
			PacketNBT.sendToServer(this.selectedPlayer, this.selectedItem);
		}
	}

	@Override
	public ResourceState<T> getResourceState()
	{
		return this.getModelState(this.selectedItem);
	}

	@Override
	public void updateResourceState(){}

	@Override
	public int[] getSelectorPos()
	{
		return new int[3];
	}

	@Override
	public boolean closeGui(ResourceState par1)
	{
		//ResourceState<T> state = this.getResourceState();
		//state.setResourceName(par1);
		this.setModelState(this.selectedItem, par1);
		return true;
	}
}