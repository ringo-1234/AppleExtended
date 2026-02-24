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

package jp.ngt.rtm.item;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemMultiIcon;
import jp.ngt.ngtlib.item.ItemUtil;
import jp.ngt.ngtlib.item.SerializableItemType;
import jp.ngt.rtm.RTMBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMirror extends ItemMultiIcon
{
	public ItemMirror(SerializableItemType[] par1)
	{
		super(par1);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1)
    {
		int meta = par1.getItemDamage();
		switch(meta)
		{
		case 0:return super.getUnlocalizedName() + ".plate";
		default:return super.getUnlocalizedName() + ".block." + (meta - 20);
		}
    }

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();
		EnumFacing side = holder.getFacing();

		if(!world.isRemote)
		{
			int meta = itemStack.getItemDamage();
			BlockPos newPos = ItemUtil.getPlacePos(pos, side);
			int x = newPos.getX();
			int y = newPos.getY();
			int z = newPos.getZ();
			Block block = null;

			if(!world.isAirBlock(newPos) || !player.canPlayerEdit(newPos, side, itemStack)){return holder.success();}

			if(meta == 0)
			{
				BlockUtil.setBlock(world, x, y, z, RTMBlock.mirror, side.getIndex(), 3);
				block = RTMBlock.mirror;
			}
			else
			{
				BlockUtil.setBlock(world, x, y, z, RTMBlock.mirrorCube, meta - 20, 3);
				block = RTMBlock.mirror;
			}

			if(block != null)
			{
				world.playSound((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, SoundEvents.BLOCK_GLASS_PLACE,
        				SoundCategory.BLOCKS, 1.0F, 1.0F, true);
				itemStack.shrink(1);
			}
		}

		return holder.success();
    }
}