/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

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
