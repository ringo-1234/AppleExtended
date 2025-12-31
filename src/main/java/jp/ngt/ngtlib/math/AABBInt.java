package jp.ngt.ngtlib.math;

public final class AABBInt
{
	public int minX, minY, minZ;
	public int maxX, maxY, maxZ;

	public AABBInt(int p1, int p2, int p3)
	{
		this(0, 0, 0, p1, p2, p3);
	}

	public AABBInt(int p1, int p2, int p3, int p4, int p5, int p6)
	{
		this.minX = p1;
		this.minY = p2;
		this.minZ = p3;
		this.maxX = p4;
		this.maxY = p5;
		this.maxZ = p6;
	}

	public boolean isCollided(AABBInt aabb)
	{
		return this.minX < aabb.maxX && this.maxX > aabb.minX
				&& this.minY < aabb.maxY && this.maxY > aabb.minY
				&& this.minZ < aabb.maxZ && this.maxZ > aabb.minZ;
	}

	public int sizeX()
	{
		return this.maxX - this.minX;
	}

	public int sizeY()
	{
		return this.maxY - this.minY;
	}

	public int sizeZ()
	{
		return this.maxZ - this.minZ;
	}

	public void repeat(RepeatFunc func)
	{
		int count = 0;
		for(int x = this.minX; x < this.maxX; ++x)
		{
			for(int y = this.minY; y < this.maxY; ++y)
			{
				for(int z = this.minZ; z < this.maxZ; ++z)
				{
					func.processing(x, y, z, count);
					++count;
				}
			}
		}
	}

	public static interface RepeatFunc
	{
		void processing(int x, int y, int z, int count);
	}
}