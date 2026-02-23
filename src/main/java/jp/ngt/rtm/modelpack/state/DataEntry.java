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

import jp.ngt.ngtlib.math.Vec3;
import net.minecraft.nbt.NBTTagCompound;

public abstract class DataEntry<T>
{
	protected T data;
	public final int flag;

	public DataEntry(T value, int flag)
	{
		this.data = value;
		this.flag = flag;
	}

	public abstract void readFromNBT(NBTTagCompound nbt);

	public abstract void writeToNBT(NBTTagCompound nbt);

	public abstract DataType getType();

	public T get()
	{
		return this.data;
	}

	public static DataEntry getEntry(String type, String data, int flag)
	{
		DataType dType = DataType.getType(type);

		if(dType == DataType.INT)
		{
			int i = data.isEmpty() ? 0 : Integer.valueOf(data);
			return new DataEntryInt(i, flag);
		}
		else if(dType == DataType.DOUBLE)
		{
			double d = data.isEmpty() ? 0.0D : Double.valueOf(data);
			return new DataEntryDouble(d, flag);
		}
		else if(dType == DataType.BOOLEAN)
		{
			boolean f = data.isEmpty() ? false : Boolean.valueOf(data);
			return new DataEntryBoolean(f, flag);
		}
		else if(dType == DataType.STRING)
		{
			return new DataEntryString(data, flag);
		}
		else if(dType == DataType.VEC)
		{
			Vec3 vec = DataEntryVec.fromString(data);
			return new DataEntryVec(vec, flag);
		}
		else if(dType == DataType.HEX)
		{
			int i = data.isEmpty() ? 0 : Integer.decode(data);
			return new DataEntryHex(i, flag);
		}
		return null;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}