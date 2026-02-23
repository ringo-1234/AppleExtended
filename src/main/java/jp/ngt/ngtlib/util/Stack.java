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

import java.util.LinkedList;
import java.util.List;

public class Stack<E>
{
	private List<E> list = new LinkedList<E>();
	private int maxSize;

	public Stack(int size)
	{
		this.maxSize = size;
	}

	public void push(E element)
	{
		while(this.list.size() > this.maxSize)
		{
			this.list.remove(this.list.size() - 1);
		}
		this.list.add(0, element);
	}

	public E pop()
	{
		if(!this.list.isEmpty())
		{
			E element = this.list.get(0);
			this.list.remove(0);
			return element;
		}
		return null;
	}
}