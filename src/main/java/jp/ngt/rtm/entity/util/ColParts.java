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

package jp.ngt.rtm.entity.util;

import java.util.ArrayList;
import java.util.List;

public final class ColParts
{
	public final String name;
	public final List<ColFace> faces = new ArrayList<>();

	public ColParts(String par1)
	{
		this.name = par1;
	}
}
