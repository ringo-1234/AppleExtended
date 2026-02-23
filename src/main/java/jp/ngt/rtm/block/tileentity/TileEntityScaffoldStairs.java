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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;

public class TileEntityScaffoldStairs extends TileEntityScaffold
{
	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.ORNAMENT_STAIR;
	}

	@Override
	protected Vec3 getVec(float par1)
    {
		double d0 = NGTMath.sin(45.0F);
    	return PooledVec3.create(0.0F, par1 * d0, par1 * d0);
    }
}