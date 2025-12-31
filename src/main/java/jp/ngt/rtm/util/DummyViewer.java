package jp.ngt.rtm.util;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

public class DummyViewer extends EntityLivingBase
{
	public DummyViewer(World world, double x, double y, double z, float yaw, float pitch)
	{
		super(world);
		this.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {
		super.setLocationAndAngles(x, y, z, yaw, pitch);
		this.chunkCoordX = NGTMath.floor(x);// >> 4
		this.chunkCoordY = NGTMath.floor(y);
		this.chunkCoordZ = NGTMath.floor(z);
    }

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public EnumHandSide getPrimaryHand() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}