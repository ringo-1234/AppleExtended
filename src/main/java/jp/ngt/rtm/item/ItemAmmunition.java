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

import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**弾薬*/
public class ItemAmmunition extends Item
{
	public ItemAmmunition()
	{
		super();
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1)
    {
        int i = par1.getItemDamage() / 4;
        return super.getUnlocalizedName() + "." + i;
    }

	@Override
	public String getItemStackDisplayName(ItemStack par1)
    {
		int i0 = par1.getItemDamage() % 4;
		String s0 = i0 == 0 ? "item.ammo.name" : (i0 == 1) ? "item.bullet.name" : "item.case.name";
		String s1 = NGTUtil.translate(this.getUnlocalizedName(par1) + ".name");
		String s2 = NGTUtil.translate(s0);
        return (s1 + s2).trim();
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
		if(!this.isInCreativeTab(tab)){return;}

		for(BulletType type : BulletType.values())
        {
			if(type == BulletType.rifle_5_56mm){continue;}
			int i0 = type.id * 4;
			list.add(new ItemStack(this, 1, i0));//弾薬
			list.add(new ItemStack(this, 1, i0 + 1));//弾
			list.add(new ItemStack(this, 1, i0 + 2));//薬莢
        }
    }

	public enum BulletIconType
	{
		CANNON,
		HANDGUN,
		RIFLE,
		ROCKET;
	}

	public enum BulletType
	{
		cannon_40cm(0, 40.0F, false, BulletIconType.CANNON),
		handgun_9mm(1, 8.0F, true, BulletIconType.HANDGUN),
		rifle_5_56mm(2, 12.0F, true, BulletIconType.RIFLE),
		rifle_7_62mm(3, 12.0F, true, BulletIconType.RIFLE),
		rifle_12_7mm(4, 24.0F, true, BulletIconType.RIFLE),
		cannon_Atomic(5, 100.0F, false, BulletIconType.CANNON),
		rocket(6, 50.0F, false, BulletIconType.ROCKET)
		;

		public final byte id;
		public final float damage;
		public final boolean muzzleFlash;
		public final BulletIconType icon;

		private BulletType(int par1, float par2, boolean par3, BulletIconType par4)
		{
			this.id = (byte)par1;
			this.damage = par2;
			this.muzzleFlash = par3;
			this.icon = par4;
		}

		public static BulletType getBulletType(int id)
		{
			for(BulletType type : values())
			{
				if(type.id == id)
				{
					return type;
				}
			}
			return handgun_9mm;
		}

		public static BulletType getBulletType(String name)
		{
			return BulletType.valueOf(name);
		}
	}
}