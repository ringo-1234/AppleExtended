package jp.ngt.ngtlib.math;

public class Mat3
{
	public static final Mat3 IDENTITY = new Mat3(
			new Vec3(1.0D, 0.0D, 0.0D), new Vec3(0.0D, 1.0D, 0.0D), new Vec3(0.0D, 0.0D, 1.0D));

	private final Vec3 x, y, z;

	public Mat3(Vec3 px, Vec3 py, Vec3 pz)
	{
		this.x = px;
		this.y = py;
		this.z = pz;
	}

	public Vec3 getX()
	{
		return this.x;
	}

	public Vec3 getY()
	{
		return this.y;
	}

	public Vec3 getZ()
	{
		return this.z;
	}

	public Mat3 rotateAroundX(float rotation)
	{
		return this.rotateAroundVec(this.getX(), rotation);
	}

	public Mat3 rotateAroundY(float rotation)
	{
		return this.rotateAroundVec(this.getY(), rotation);
	}

	public Mat3 rotateAroundZ(float rotation)
	{
		return this.rotateAroundVec(this.getZ(), rotation);
	}

	public Mat3 rotateAroundVec(Vec3 axis, float rotation)
	{
		Vec3 vx = this.getX().rotateAroundVec(axis, rotation);
		Vec3 vy = this.getY().rotateAroundVec(axis, rotation);
		Vec3 vz = this.getZ().rotateAroundVec(axis, rotation);
		return new Mat3(vx, vy, vz);
	}
}
