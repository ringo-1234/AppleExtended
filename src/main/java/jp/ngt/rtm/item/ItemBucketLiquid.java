package jp.ngt.rtm.item;

import java.util.List;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.entity.fluid.EntityFluid;
import jp.ngt.rtm.entity.fluid.FluidType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBucketLiquid extends ItemCustom
{
	private static final FluidType[] FLUID_LIST = {FluidType.PIG_IRON, FluidType.STEEL};
	public static final int MAX_COUNT = 16;

	public ItemBucketLiquid()
	{
		super();
		this.maxStackSize = 1;
        this.setMaxDamage(MAX_COUNT - 1);
		this.setHasSubtypes(true);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();

		RayTraceResult mop = this.rayTrace(world, player, false);
        if(mop == null)
        {
            return holder.success();
        }
        else
        {
        	FluidType type = getFluidType(holder.getItemStack());
        	if(type != null)
    		{
        		int amount = (MAX_COUNT - itemStack.getItemDamage());
    			holder.setItemStack(useBucket(world, player, mop, type, amount,
    					holder.getItemStack(), player.capabilities.isCreativeMode));
    		}
        	return holder.success();
        }
    }

	/**
	 * FillBucketEvent, もしくは右クリックしたとき呼ばれる
	 * @param world
	 * @param type 液体タイプ, nullならBlockAir
	 * @param amount 液量, 空なら0
	 * @param item 液体入りバケツ or null
	 */
	public static ItemStack useBucket(World world, EntityPlayer player, RayTraceResult target, FluidType type, int amount, ItemStack item, boolean isCreative)
	{
		if(target.typeOfHit == RayTraceResult.Type.BLOCK)
        {
			int x = target.getBlockPos().getX();
            int y = target.getBlockPos().getY();
            int z = target.getBlockPos().getZ();

            if(type != null)
            {
            	x += target.sideHit.getDirectionVec().getX();
            	y += target.sideHit.getDirectionVec().getY();
            	z += target.sideHit.getDirectionVec().getZ();
            }

            if(type != null)
            {
            	if(player.canPlayerEdit(new BlockPos(x, y, z), target.sideHit, item))
            	{
            		float temp = getTemperture(item);
            		if(setFluid(world, x, y, z, type, amount, temp))
                    {
            			return isCreative ? item : new ItemStack(Items.BUCKET);
                    }
            	}
            }
        }
		return item;
	}

	public static boolean setFluid(World world, int x, int y, int z, FluidType type, int amount, float tempreture)
	{
		return setFluid(world, x + 0.5D, y + 0.5D, z + 0.5D, type, amount, tempreture);
	}

	public static boolean setFluid(World world, double x, double y, double z, FluidType type, int amount, float temperture)
	{
		if(!world.isRemote)
		{
			double fluc = 2.0F * (0.5D - EntityFluid.R);//ブロックにめり込まない範囲で
			for(int i = 0; i < amount; ++i)
			{
				double d0 = (double)(i + 1) / (double)amount;
				EntityFluid fluid = new EntityFluid(world);
				fluid.setPosition(
						x + (world.rand.nextDouble() - 0.5D) * fluc * d0,
						y,
						z + (world.rand.nextDouble() - 0.5D) * fluc * d0);
				fluid.setTemperture(temperture);
				fluid.setFluidType(type);
				world.spawnEntity(fluid);
			}
		}
		return true;
	}

	public static boolean pickupFluid(EntityPlayer player, EntityFluid fluid)
	{
		int count = 0;
		if(NGTUtil.isEquippedItem(player, RTMItem.bucketLiquid))
		{
			count = (MAX_COUNT - player.getHeldItemMainhand().getItemDamage());
			if(count >= MAX_COUNT)
			{
				return false;//バケツが満杯
			}

			if(getFluidType(player.getHeldItemMainhand()) != fluid.getFluidType())
			{
				return false;//バケツと液体タイプが異なる
			}
		}
		int oldCount = count;

		if(!player.getEntityWorld().isRemote)
		{
			List<EntityFluid> list = player.getEntityWorld().getEntitiesWithinAABB(EntityFluid.class,
					fluid.getEntityBoundingBox().grow(EntityFluid.METABALL_RANGE, EntityFluid.METABALL_RANGE, EntityFluid.METABALL_RANGE));
			for(EntityFluid fluid2 : list)
			{
				if((fluid2.getFluidType() == fluid.getFluidType()) && (count < MAX_COUNT))
				{
					fluid2.setDead();
					++count;
				}
			}

			if(!player.isCreative() && count > oldCount)
			{
				ItemStack itemstack = getItem(fluid.getFluidType(), count - 1, fluid.getTemperature());
				player.setHeldItem(EnumHand.MAIN_HAND, itemstack);
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
		if(!this.isInCreativeTab(tab)){return;}

		for(FluidType type : FLUID_LIST)
		{
			list.add(getItem(type, MAX_COUNT - 1, type.meltingPoint));
		}
    }

	@Override
	protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag)
    {
		FluidType type = getFluidType(holder.getItemStack());
		if(type != null)
		{
			list.add(TextFormatting.GRAY + type.toString());
			int amount = MAX_COUNT - holder.getItemStack().getItemDamage();
			list.add(TextFormatting.GRAY + String.format("%d/16", amount));
		}
    }

	private static ItemStack getItem(FluidType type, int amount, float temperture)
	{
		ItemStack itemstack = new ItemStack(RTMItem.bucketLiquid, 1, (MAX_COUNT - amount - 1));
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", type.toString());
		nbt.setFloat("temperture", temperture);
		itemstack.setTagCompound(nbt);
		return itemstack;
	}

	@Nullable
	public static FluidType getFluidType(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			String type = stack.getTagCompound().getString("type");
			if(type != null && !type.isEmpty())
			{
				return FluidType.valueOf(type);
			}
		}
		return null;
	}

	public static float getTemperture(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			return stack.getTagCompound().getFloat("temperture");
		}
		return 0.0F;
	}
}