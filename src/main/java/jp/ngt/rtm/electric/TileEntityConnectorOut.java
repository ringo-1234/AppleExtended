package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;

public class TileEntityConnectorOut extends TileEntityConnector
{
	@Override
	public ResourceType getSubType()
	{
		return RTMResource.CONNECTOR_OUTPUT;
	}
}