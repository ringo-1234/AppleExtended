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

import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BasicRailPartsRenderer extends RailPartsRendererBase
{
	@Override
	public void init(ModelSetRail par1, ModelObject par2)
	{
		super.init(par1, par2);
	}

	@Override
	protected void renderRailStatic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8)
	{
		this.renderStaticParts(tileEntity, x, y, z);
	}

	@Override
	protected void renderRailDynamic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8)
	{
		;
	}

	@Override
	protected boolean shouldRenderObject(TileEntityLargeRailCore tileEntity, String objName, int len, int pos)
	{
		return true;
	}
}