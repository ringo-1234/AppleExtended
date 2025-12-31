package jp.ngt.ngtlib.item;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.util.Usage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCustom extends Item
{
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		return this.onItemRightClick((new ItemArgHolder()).setWorld(world).setPlayer(player).setHand(hand));
    }

	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		return new ActionResult(EnumActionResult.PASS, holder.getItemStack());
    }

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		return this.onItemUse((new ItemArgHolder()).setPlayer(player).setWorld(world).setBlockPos(pos).setHand(hand).setFacing(facing), hitX, hitY, hitZ).getType();
    }

	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		return new ActionResult(EnumActionResult.PASS, holder.getItemStack());
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
		this.addInformation(new ItemArgHolder().setItemStack(stack).setWorld(world), tooltip, flag);
		super.addInformation(stack, world, tooltip, flag);

		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			Usage.INSTANCE.addTooltip(this, stack.getItemDamage(), tooltip);
		}
		else
		{
			tooltip.add(TextFormatting.YELLOW + "= Display Usage with L_Shift =");
		}
    }

	@SideOnly(Side.CLIENT)
	protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag){}
}