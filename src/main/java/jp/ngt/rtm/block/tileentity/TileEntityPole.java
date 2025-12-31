package jp.ngt.rtm.block.tileentity;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;

public class TileEntityPole extends TileEntityOrnament
{
	//linepole-0,1,2,3
	//framework

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.ORNAMENT_POLE;
	}
}