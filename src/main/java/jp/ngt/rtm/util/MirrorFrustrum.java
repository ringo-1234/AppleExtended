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

package jp.ngt.rtm.util;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;

@Deprecated
public class MirrorFrustrum implements ICamera
{
	public MirrorFrustrum()
	{
		;
	}

	@Override
	public boolean isBoundingBoxInFrustum(AxisAlignedBB aabb)
	{
		return false;
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		;
	}
}