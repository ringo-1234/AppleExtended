package jp.ngt.rtm.item;

import java.util.List;

import jp.ngt.ngtlib.event.TickProcessQueue;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMSound;
import jp.ngt.rtm.entity.EntityBullet;
import jp.ngt.rtm.entity.npc.EntityDummyPlayer;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGun extends ItemCustom
{
	public static final int INTERVAL = 2;

	public final GunType gunType;

	public ItemGun(GunType par1)
	{
		super();
		this.gunType = par1;
		this.maxStackSize = 1;
		this.setMaxDamage(par1.maxSize);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		ItemStack itemstack = holder.getItemStack();
		EntityPlayer player = holder.getPlayer();
		if(itemstack.getItemDamage() < itemstack.getMaxDamage())//0→Max
		{
			player.setActiveHand(holder.getHand());
		}
		else//inv.hasItem()判定削除、というかメソッドがない
		{
			if(!player.capabilities.isCreativeMode && !holder.getWorld().isRemote)
			{
				int i0 = this.setMagazine(player);
				if(i0 < itemstack.getMaxDamage())
				{
					itemstack.setItemDamage(i0);
					player.entityDropItem(new ItemStack(this.getMagazineFromGunType(), 1, itemstack.getMaxDamage()), 1.0F);
				}
			}
		}

		return holder.success();
    }

	private int setMagazine(EntityPlayer player)
	{
		Item magazineItem = this.getMagazineFromGunType();
		for(int i = 0; i < player.inventory.mainInventory.size(); ++i)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() == magazineItem)
			{
				if(stack.getItemDamage() < magazineItem.getMaxDamage())
				{
					player.inventory.setInventorySlotContents(i, null);
					return stack.getItemDamage();
				}
			}
		}
		return 1024;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
		;
    }

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase living, int count)
    {
		if(!(living instanceof EntityPlayer)){}
		EntityPlayer player = (EntityPlayer)living;

		if(stack.getItemDamage() == stack.getMaxDamage())
		{
			//player.stopUsingItem();
		}
		else if(this.onUsingGun(stack, player.world, player, count))
		{
			if(!player.capabilities.isCreativeMode)
			{
				if(stack.isItemStackDamageable())
	            {
					stack.setItemDamage(stack.getItemDamage() + 1);
	            }
			}
		}
    }

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return this.gunType.useDuration;
    }

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
    {
        return EnumAction.BOW;
    }

	@Override
	public int getItemEnchantability()
    {
        return 1;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

	/**アイテムを使用している間、Tickごとに呼ばれる*/
	protected boolean onUsingGun(ItemStack itemstack, World world, EntityPlayer player, int count)
	{
		if(count % INTERVAL > 0){return false;}
		if(!this.gunType.rapidFire && count < this.gunType.useDuration){return false;}

		if(!world.isRemote)
		{
			if(this.gunType == GunType.razer_gun)
			{
				if(PermissionManager.INSTANCE.hasPermission(player, RTMCore.USE_RAZER))
				{
					RazerBullet bullet = new RazerBullet(player);
					TickProcessQueue.getInstance(Side.SERVER).add(bullet);
					return true;
				}
			}
			else
			{
				if(PermissionManager.INSTANCE.hasPermission(player, RTMCore.USE_GUN))
				{
					EntityBullet bullet;
					if(player instanceof EntityDummyPlayer)
					{
						bullet = ((EntityDummyPlayer)player).npc.getBullet(this.gunType);
					}
					else
					{
						bullet = new EntityBullet(world, player, this.gunType.speed, this.gunType.bulletType);
					}
					world.spawnEntity(bullet);
					RTMCore.proxy.playSound(player, RTMSound.GUN, RTMCore.gunSoundVol, 1.0F);

					if(!player.capabilities.isCreativeMode)
					{
						int damage = this.gunType.bulletType.id * 4 + 2;
						player.entityDropItem(new ItemStack(RTMItem.bullet, 1, damage), 0.5F);//薬莢ドロップ
					}
				}
			}
		}

		return false;
	}

	@Override
	protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag)
    {
		int max = holder.getItemStack().getMaxDamage();
		list.add(TextFormatting.GRAY + "Bullet:" + String.valueOf(max - holder.getItemStack().getItemDamage()) + "/" + String.valueOf(max));
    }

	public Item getMagazineFromGunType()
	{
		switch(this.gunType)
		{
		case handgun:           return RTMItem.magazine_handgun;
		case rifle:             return RTMItem.magazine_rifle;
		case autoloading_rifle: return RTMItem.magazine_alr;
		case sniper_rifle:      return RTMItem.magazine_sr;
		case smg:               return RTMItem.magazine_smg;
		case amr:               return RTMItem.magazine_amr;
		case razer_gun:         return RTMItem.razer_gun;
		default:
			break;
		}
		return RTMItem.magazine_handgun;
	}

	public enum GunType
	{
		handgun(BulletType.handgun_9mm,            10, 16, 4.5F, false),
		rifle(BulletType.rifle_7_62mm,              5, 16, 7.5F, false),
		autoloading_rifle(BulletType.rifle_7_62mm, 30,  6, 7.5F, true),
		sniper_rifle(BulletType.rifle_7_62mm,      10, 20, 7.5F, false),
		smg(BulletType.handgun_9mm,                30,  6, 4.5F, true),
		amr(BulletType.rifle_12_7mm,               10, 24, 9.0F, false),
		razer_gun(BulletType.rifle_12_7mm,         10, 60, 150.0F, false);;

		public final BulletType bulletType;
		public final int maxSize;
		public final int useDuration;
		public final float speed;
		public boolean rapidFire;

		private GunType(BulletType par1, int par2, int par3, float par4, boolean par5)
		{
			this.bulletType = par1;
			this.maxSize = par2;
			this.useDuration = par3;
			this.speed = par4;
			this.rapidFire = par5;
		}
	}
}