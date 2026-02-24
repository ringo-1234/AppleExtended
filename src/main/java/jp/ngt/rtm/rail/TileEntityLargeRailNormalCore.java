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

package jp.ngt.rtm.rail;

import jp.ngt.rtm.rail.util.RailMap;

public class TileEntityLargeRailNormalCore extends TileEntityLargeRailCore
{
	@Override
	public String getRailShapeName()
	{
		RailMap map = this.getRailMap(null);
		StringBuilder sb = new StringBuilder();
		sb.append("Type:Normal, ");
		sb.append("X:").append(map.getEndRP().blockX - map.getStartRP().blockX).append(", ");
		sb.append("Y:").append(map.getEndRP().blockY - map.getStartRP().blockY).append(", ");
		sb.append("Z:").append(map.getEndRP().blockZ - map.getStartRP().blockZ);
		return sb.toString();
	}
}