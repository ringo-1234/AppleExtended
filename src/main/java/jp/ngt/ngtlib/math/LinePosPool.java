package jp.ngt.ngtlib.math;

import jp.ngt.ngtlib.util.ObjectPool;

public final class LinePosPool
{
	private static ObjectPool<double[]> POOL;

	static
	{
		final int size = 512;
		double[][] array1 = new double[size][];
		double[][] array2 = new double[size][];
		for(int i = 0; i < size; ++i)
		{
			array1[i] = new double[2];
			array2[i] = new double[2];
		}
		POOL = new ObjectPool<>(new double[][][]{array1, array2});
	}

	public static double[] get(double x, double y)
	{
		/*double[] array = POOL.get();
		array[0] = x;
		array[1] = y;
		return array;*/
		return new double[]{x, y};
	}
}
