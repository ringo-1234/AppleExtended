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

import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.decoration.DecorationModel;
import jp.ngt.rtm.block.tileentity.TileEntityDecoration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemDecoration extends ItemCustom
{
	public ItemDecoration()
	{
		super();
		this.setMaxStackSize(1);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		if(world.isRemote)
		{
			player.openGui(RTMCore.instance, RTMCore.guiIdDecoration, world, player.getEntityId(), 0, 0);
		}
        return holder.success();
    }

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		if(!holder.getWorld().isRemote)
		{
			BlockPos pos = holder.getBlockPos().add(holder.getFacing().getDirectionVec());
			BlockUtil.setBlock(holder.getWorld(), pos, RTMBlock.decoration, 0, 3);
			TileEntityDecoration tile = (TileEntityDecoration)holder.getWorld().getTileEntity(pos);
			tile.setModelName(getModelName(holder.getItemStack()));
		}
		return holder.success();
    }

	@Override
	protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag)
    {
		super.addInformation(holder, list, flag);
		list.add(TextFormatting.GRAY + "Model:" + getModelName(holder.getItemStack()));
    }

	public static void setModel(ItemStack stack, String modelName)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null)
		{
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		nbt.setString("ModelName", modelName);
	}

	public static String getModelName(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			return stack.getTagCompound().getString("ModelName");
		}
		return DecorationModel.DEFAULT_MODEL.name;
	}
}