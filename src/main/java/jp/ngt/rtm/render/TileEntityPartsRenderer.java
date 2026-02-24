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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TileEntityPartsRenderer<MS extends ModelSetBase> extends PartsRenderer<TileEntity, MS>
{
	public TileEntityPartsRenderer(String... par1)
	{
		super(par1);
	}

	public int getMetadata(TileEntity par1)
	{
		return par1 == null ? 0 : ((TileEntity)par1).getBlockMetadata();
	}

	@Override
	public World getWorld(TileEntity entity)
	{
		return entity == null ? null : entity.getWorld();
	}

	public int getX(TileEntity entity)
	{
		return entity == null ? 0 : entity.getPos().getX();
	}

	public int getY(TileEntity entity)
	{
		return entity == null ? 0 : entity.getPos().getY();
	}

	public int getZ(TileEntity entity)
	{
		return entity == null ? 0 : entity.getPos().getZ();
	}
}