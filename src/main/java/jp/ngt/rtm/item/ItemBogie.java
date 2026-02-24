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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBogie extends Item
{
	public ItemBogie()
	{
		super();
		this.setHasSubtypes(true);
		this.maxStackSize = 16;
	}

	/*@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        RailMap rm0 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, x, y, z);
    	if(rm0 != null)
    	{
    		int i0 = rm0.getNearlestPoint(128, x, z);
	    	float f0 = rm0.getRailRotation(128, i0);

	    	double x0 = rm0.getRailPos(128, i0)[1];
			double y0 = y + 1.0D;
			double z0 = rm0.getRailPos(128, i0)[0];

			EntityBogie bogie = new EntityBogie(world);
			bogie.setPositionAndRotation(x0, y0, z0, f0, 0.0F);
            if (!world.isRemote)
            {
                world.spawnEntityInWorld(bogie);
            }
            --itemStack.stackSize;
			return true;
    	}
        else
        {
            return false;
        }
    }*/

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
    {
        return super.getUnlocalizedName() + "." + itemStack.getItemDamage();
    }
}