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

package jp.ngt.rtm;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabRTM extends CreativeTabs {
    public static final CreativeTabs RAILWAY = new CreativeTabRTM("rtm_railway");
    public static final CreativeTabs INDUSTRY = new CreativeTabRTM("rtm_industry");
    public static final CreativeTabs TOOLS = new CreativeTabRTM("rtm_tools");
    @SuppressWarnings("unused")
    public static final CreativeTabs field_78040_i = TOOLS;

    public CreativeTabRTM(String label) {
        super(label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        if (this == RAILWAY) {
            return new ItemStack(RTMItem.itemtrain);
        } else if (this == INDUSTRY) {
            return new ItemStack(RTMItem.steel_ingot);
        } else if (this == TOOLS) {
            return new ItemStack(RTMItem.crowbar);
        } else {
            return new ItemStack(RTMItem.crowbar);
        }
    }
}