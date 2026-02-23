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

package jp.ngt.ngtlib.util;

public final class ObjectPool<T>
{
	private final T[][] pool;
	private int[] index = {0, 0};

	public ObjectPool(T[][] array)
	{
		this.pool = array;
	}

	public T get()
	{
		int i0 = NGTUtil.isServer() ? 0 : 1;
		int i1 = this.index[i0];
		this.index[i0] = (i1 + 1) % this.pool[i0].length;
		return this.pool[i0][i1];
	}
}
