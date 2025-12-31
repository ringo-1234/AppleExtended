package jp.ngt.rtm.item;

import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class ItemWire extends ItemWithModel
{
	public ItemWire()
	{
		super();
	}

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		return holder.pass();
    }

	@Override
	protected ResourceType getModelType(ItemStack itemStack)
	{
		return RTMResource.WIRE;
	}

	@Override
	public int getGuiId(ItemStack stack)
	{
		return RTMCore.guiIdSelectItemModel;
	}

	@Override
	protected ResourceState getNewState(ItemStack itemStack, ResourceType type)
	{
		return new ResourceState(type, null);
	}
}