package jp.ngt.rtm.rail;

import jp.ngt.rtm.rail.util.RailMap;

public class TileEntityLargeRailNormalCore extends TileEntityLargeRailCore
{
	@Override
	public String getRailShapeName()
	{
		RailMap map = this.getRailMap(null);
		StringBuilder sb = new StringBuilder();
		sb.append("Type:Normal, ");
		sb.append("X:").append(map.getEndRP().blockX - map.getStartRP().blockX).append(", ");
		sb.append("Y:").append(map.getEndRP().blockY - map.getStartRP().blockY).append(", ");
		sb.append("Z:").append(map.getEndRP().blockZ - map.getStartRP().blockZ);
		return sb.toString();
	}
}