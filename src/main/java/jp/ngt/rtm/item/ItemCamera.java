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

import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.rtm.RTMCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class ItemCamera extends ItemCustom
{
	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();

		if(world.isRemote)
		{
			player.openGui(RTMCore.instance, RTMCore.guiIdCamera, world, player.getEntityId(), 0, 0);
		}

		return holder.success();
    }
}