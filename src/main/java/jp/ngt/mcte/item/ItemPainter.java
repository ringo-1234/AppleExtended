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

package jp.ngt.mcte.item;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPainter extends ItemCustom
{
	public ItemPainter()
	{
		super();
		this.setMaxStackSize(1);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		holder.getPlayer().setActiveHand(holder.getHand());
		//holder.getPlayer().setItemInUse(holder.getItemStack(), this.getMaxItemUseDuration(holder.getItemStack()));//1.8
		return holder.success();
    }

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();

		if(!world.isRemote)
		{
			if(player.isSneaking())//ブロックの詳細をチャットに表示
			{
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				int meta = block.getMetaFromState(state);
				ItemStack stack = new ItemStack(block, 1, meta);
				NGTLog.sendChatMessage(player, stack.getDisplayName() + "(" + Block.REGISTRY.getNameForObject(block) + ", " + meta + ")");
				if(block.hasTileEntity(state))
				{
					TileEntity tile = world.getTileEntity(pos);
					if(tile != null)
					{
						NGTLog.sendChatMessage(player, tile.getClass().toString());
					}
					else
					{
						NGTLog.sendChatMessage(player, "No TileEntity");
					}
				}
			}
			else
			{
				this.onUsingTick(itemStack, player, 0);
			}
		}
		return holder.success();
    }

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase living, int count)
    {
		if(!living.getEntityWorld().isRemote)
		{
			if(living instanceof EntityPlayer)
			{
				PainterSetting setting = PainterSetting.getPainterSettingFromItem(stack);
				RayTraceResult target = ItemEditor.getTarget((EntityPlayer)living, setting.drawMode == 0, false);
	    		if(target != null && setting != null)
	        	{
	    			BlockPos pos = target.getBlockPos();
	    			this.placeBlocks(setting, living.world, pos.getX(), pos.getY(), pos.getZ());
	        	}
			}
		}
    }

	private void placeBlocks(PainterSetting setting, World world, int x, int y, int z)
	{
		int size = setting.size - 1;
		for(int i = -size; i <= size; ++i)
		{
			for(int j = -size; j <= size; ++j)
			{
				for(int k = -size; k <= size; ++k)
				{
					int x0 = x + i;
					int y0 = y + j;
					int z0 = z + k;
					if((i * i + j * j + k * k) <= size * size)
					{
						Block targetBlock = BlockUtil.getBlock(world, x0, y0, z0);
						int targetMetadata = BlockUtil.getMetadata(world, x0, y0, z0);

						boolean flag1 = setting.fill && targetBlock == Blocks.AIR;
						boolean flag2 = setting.rewrite && targetBlock != Blocks.BEDROCK && (setting.rewriteBlock.block == Blocks.AIR || (setting.rewriteBlock.block == targetBlock && setting.rewriteBlock.metadata == targetMetadata));
						if(flag1 || flag2)
						{
							BlockUtil.setBlock(world, x0, y0, z0, setting.fillBlock.block, setting.fillBlock.metadata, 2);
						}
					}
				}
			}
		}
	}



	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 72000;
    }

	@Override
	public EnumAction getItemUseAction(ItemStack par1)
    {
		return EnumAction.NONE;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
}