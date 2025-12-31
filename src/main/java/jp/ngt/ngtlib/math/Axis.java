package jp.ngt.ngtlib.math;

import net.minecraft.util.EnumFacing;

public enum Axis
{
	POSITIVE_X(EnumFacing.EAST),
	NEGATIVE_X(EnumFacing.WEST),
	POSITIVE_Y(EnumFacing.UP),
	NEGATIVE_Y(EnumFacing.DOWN),
	POSITIVE_Z(EnumFacing.SOUTH),
	NEGATIVE_Z(EnumFacing.NORTH);

	public final EnumFacing face;

	private Axis(EnumFacing par1)
	{
		this.face = par1;
	}
}
