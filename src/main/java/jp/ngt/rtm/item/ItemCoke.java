package jp.ngt.rtm.item;

import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.rtm.entity.fluid.FluidType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class ItemCoke extends ItemCustom
{
	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		if(!holder.getWorld().isRemote)
		{
			ItemBucketLiquid.setFluid(holder.getWorld(),
					holder.getBlockPos().getX() + hitX, holder.getBlockPos().getY() + hitY, holder.getBlockPos().getZ() + hitZ,
					FluidType.COKE, 1, 0.0F);
			holder.getItemStack().shrink(1);
		}
		return holder.success();
    }
}
