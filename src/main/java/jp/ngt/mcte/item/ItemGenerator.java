package jp.ngt.mcte.item;

import jp.ngt.mcte.MCTE;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemGenerator extends ItemCustom
{
	public ItemGenerator()
	{
		super();
		this.setMaxStackSize(1);
	}

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();

		if(world.isRemote)
		{
			player.openGui(MCTE.instance, MCTE.guiIdGenerator, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return holder.success();
    }
}