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

import jp.ngt.rtm.item.ItemGun.GunType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMagazine extends Item {
    public final GunType magazineType;

    public ItemMagazine(GunType par1) {
        super();
        this.magazineType = par1;
        this.maxStackSize = 1;
        this.setMaxDamage(par1.maxSize);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        int max = stack.getMaxDamage();
        tooltip.add(TextFormatting.GRAY + "Bullet:" + String.valueOf(max - stack.getItemDamage()) + "/" + String.valueOf(max));
    }
}