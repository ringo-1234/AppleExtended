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

package jp.ngt.rtm.render;

import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class EntityPartsRenderer<MS extends ModelSetBase> extends PartsRenderer<Entity, MS>
{
	public EntityPartsRenderer(String... par1)
	{
		super(par1);
	}

	public int getTick(Entity entity)
	{
		return entity == null ? 0 : entity.ticksExisted;
	}

	@Override
	public World getWorld(Entity entity)
	{
		return entity.world;
	}

	public float getYaw(Entity entity)
	{
		return entity != null ? entity.rotationYaw : 0.0F;
	}

	public double getX(Entity entity)
	{
		return entity == null ? 0 : entity.posX;
	}

	public double getY(Entity entity)
	{
		return entity == null ? 0 : entity.posY;
	}

	public double getZ(Entity entity)
	{
		return entity == null ? 0 : entity.posZ;
	}

	public boolean onGround(Entity entity)
	{
		return entity == null ? true : entity.onGround;
	}
}