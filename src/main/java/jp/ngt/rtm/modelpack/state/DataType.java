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

package jp.ngt.rtm.modelpack.state;

public enum DataType
{
	BOOLEAN("Boolean"),
	DOUBLE("Double"),
	INT("Int"),
	STRING("String"),
	VEC("Vec"),
	HEX("Hex"),
	;

	public final String key;

	private DataType(String par1)
	{
		this.key = par1;
	}

	public static DataType getType(String s)
	{
		for(DataType type : DataType.values())
		{
			if(type.key.equals(s))
			{
				return type;
			}
		}
		return null;
	}

	/*public interface DataEntryConverter
	{
		DataEntry toEntry(String type, String data, int flag);
	}*/
}