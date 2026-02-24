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

package jp.ngt.rtm.electric;

public enum SignalConverterType
{
	RSIn(0),
	RSOut(1),
	Increment(2),
	Decrement(3),
	Wireless(4);

	public final byte id;

	private SignalConverterType(int p1)
	{
		this.id = (byte)p1;
	}

	public static SignalConverterType getType(int p1)
	{
		if(p1 < 0 || p1 > SignalConverterType.values().length)
		{
			return SignalConverterType.RSIn;
		}
		return SignalConverterType.values()[p1];
	}
}