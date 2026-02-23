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

package jp.ngt.ngtlib.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**ブロックをダメージ別名付けする*/
public class ItemBlockCustom extends ItemBlock
{
	public ItemBlockCustom(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
    {
        return damage;
    }

	@Override
	public String getUnlocalizedName(ItemStack par1)
	{
		return super.getUnlocalizedName() + "." + par1.getItemDamage();
	}
}