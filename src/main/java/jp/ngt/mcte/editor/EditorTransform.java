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

package jp.ngt.mcte.editor;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

public enum EditorTransform
{
	Transform_RotateX(EnumFacing.EAST, null),
	Transform_RotateY(EnumFacing.UP, null),
	Transform_RotateZ(EnumFacing.SOUTH, null),
	Transform_MirrorX(EnumFacing.UP, EnumFacing.UP),
	Transform_MirrorY(EnumFacing.SOUTH, EnumFacing.SOUTH),
	Transform_MirrorZ(EnumFacing.UP, EnumFacing.UP);

	public final EnumFacing axis1;
	@Nullable
	public final EnumFacing axis2;

	private EditorTransform(EnumFacing par1, @Nullable EnumFacing par2)
	{
		this.axis1 = par1;
		this.axis2 = par2;
	}
}