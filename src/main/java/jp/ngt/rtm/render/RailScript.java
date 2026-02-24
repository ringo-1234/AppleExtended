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

import jp.ngt.rtm.rail.TileEntityLargeRailCore;

public interface RailScript
{
	void renderRailStatic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8, int pass);

	void renderRailDynamic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8, int pass);

	boolean shouldRenderObject(TileEntityLargeRailCore tileEntity, String objName, int len, int pos);
}