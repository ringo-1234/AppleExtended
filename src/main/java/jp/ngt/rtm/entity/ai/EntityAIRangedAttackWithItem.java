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

package jp.ngt.rtm.entity.ai;

import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.item.ItemGun;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.item.ItemStack;

public class EntityAIRangedAttackWithItem extends EntityAIAttackRanged
{
	private final EntityNPC npc;

	public EntityAIRangedAttackWithItem(EntityNPC par1, double speed, int par3, int maxTime, float range)
	{
		super(par1, speed, par3, maxTime, range);
		this.npc = par1;
	}

	@Override
	public boolean shouldExecute()
    {
		ItemStack item = this.npc.getHeldItem();
		if(item != null && item.getItem() instanceof ItemGun)
		{
			return super.shouldExecute();
		}
		return false;
    }
}