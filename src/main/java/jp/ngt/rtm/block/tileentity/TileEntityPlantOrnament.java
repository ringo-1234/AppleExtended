package jp.ngt.rtm.block.tileentity;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;

public class TileEntityPlantOrnament extends TileEntityOrnament
{
	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.ORNAMENT_PLANT;
	}
}