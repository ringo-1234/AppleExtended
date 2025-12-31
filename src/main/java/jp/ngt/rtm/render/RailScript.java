package jp.ngt.rtm.render;

import jp.ngt.rtm.rail.TileEntityLargeRailCore;

public interface RailScript
{
	void renderRailStatic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8, int pass);

	void renderRailDynamic(TileEntityLargeRailCore tileEntity, double x, double y, double z, float par8, int pass);

	boolean shouldRenderObject(TileEntityLargeRailCore tileEntity, String objName, int len, int pos);
}