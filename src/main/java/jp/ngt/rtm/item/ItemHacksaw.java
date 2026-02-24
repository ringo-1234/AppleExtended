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

import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHacksaw extends ItemCustom
{
    public ItemHacksaw()
    {
        this.maxStackSize = 1;
        this.setMaxDamage(ToolMaterial.IRON.getMaxUses());
    }

    @Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();

    	if(!player.canPlayerEdit(pos, holder.getFacing(), itemStack))
        {
            return holder.fail();
        }
        else
        {
        	IBlockState state = world.getBlockState(pos);
        	if(state.getBlock() == RTMBlock.steelSlab && state.getBlock().getMetaFromState(state) == 0)
        	{
        		if(world.isRemote)
                {
                    return holder.success();
                }
                else
                {
                	player.entityDropItem(new ItemStack(RTMItem.steel_ingot, 1, 0), 0.5F);
                    world.setBlockToAir(pos);
                    itemStack.damageItem(1, player);
                    //player.addStat(RTMAchievement.getSteel, 1);
                    return holder.success();
                }
        	}
        	else
        	{
        		return holder.fail();
        	}
        }
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
    	return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockState block)
    {
        return block.getBlock() == RTMBlock.steelSlab;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
}