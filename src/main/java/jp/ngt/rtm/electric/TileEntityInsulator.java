package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;

public class TileEntityInsulator extends TileEntityConnectorBase
{
    @Override
	public ResourceType getSubType()
	{
    	return RTMResource.CONNECTOR_RELAY;
	}
}