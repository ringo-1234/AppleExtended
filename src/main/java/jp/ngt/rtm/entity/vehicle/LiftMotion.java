package jp.ngt.rtm.entity.vehicle;

import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.block.tileentity.TileEntityMechanism;

public final class LiftMotion
{
	public final Vec3 pos;
	public final float yaw;
	public final float pitch;
	public final float move;
	public final TileEntityMechanism mecha;

	public LiftMotion(Vec3 p1, float p2, float p3, float p4, TileEntityMechanism p5)
	{
		this.pos = p1;
		this.yaw = p2;
		this.pitch = p3;
		this.move = p4;
		this.mecha = p5;
	}
}
