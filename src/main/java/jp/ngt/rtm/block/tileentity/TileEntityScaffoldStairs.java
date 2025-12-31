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