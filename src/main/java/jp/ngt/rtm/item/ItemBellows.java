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

package jp.ngt.rtm.item;

import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.entity.fluid.EntityFluid;
import jp.ngt.rtm.entity.fluid.FluidType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemBellows extends ItemCustom
{
	public ItemBellows()
	{
		super();
		this.setMaxStackSize(1);
	}

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		if(!holder.getWorld().isRemote)
		{
			if(BlockUtil.getBlock(holder.getWorld(), holder.getBlockPos()) == RTMBlock.slot)
			{
				int[] ia = BlockUtil.facing[holder.getFacing().getIndex()];
				int x = holder.getBlockPos().getX();
				int y = holder.getBlockPos().getY();
				int z = holder.getBlockPos().getZ();
				int sx = x - ia[0];
				int sy = x - ia[1];
				int sz = x - ia[2];
				this.blow(holder.getWorld(), sx, sy, sz, sx, sy, sz);
			}
		}
		return holder.success();
    }

	private void blow(World world, int startX, int startY, int startZ, int endX, int endY, int endZ)
	{
		List<EntityFluid> list = world.getEntitiesWithinAABB(EntityFluid.class,
				new AxisAlignedBB(startX, startY, startZ, endX + 1, endY + 3, endZ + 1));
		for(EntityFluid fluid : list)
		{
			if(fluid.getFluidType() == FluidType.COKE && fluid.countAir() <= 2)
			{
				float temp = fluid.getTemperature();
				if(temp > 300.0F && temp < 2000.0F)
				{
					fluid.setTemperture(temp + 50.0F);
				}
			}
		}
	}
}