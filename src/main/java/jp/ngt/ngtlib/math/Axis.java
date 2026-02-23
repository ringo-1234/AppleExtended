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

package jp.ngt.ngtlib.math;

import net.minecraft.util.EnumFacing;

public enum Axis
{
	POSITIVE_X(EnumFacing.EAST),
	NEGATIVE_X(EnumFacing.WEST),
	POSITIVE_Y(EnumFacing.UP),
	NEGATIVE_Y(EnumFacing.DOWN),
	POSITIVE_Z(EnumFacing.SOUTH),
	NEGATIVE_Z(EnumFacing.NORTH);

	public final EnumFacing face;

	private Axis(EnumFacing par1)
	{
		this.face = par1;
	}
}
