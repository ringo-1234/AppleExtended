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

import jp.ngt.ngtlib.math.Vec3;
import net.minecraft.nbt.NBTTagCompound;

public final class DataEntryVec extends DataEntry<Vec3>
{
	public DataEntryVec(Vec3 value, int flag)
	{
		super(value, flag);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.data = fromString(nbt.getString("Data"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("Data", this.toString());
		nbt.setString("Type", this.getType().key);
	}

	@Override
	public DataType getType()
	{
		return DataType.VEC;
	}

	@Override
	public String toString()
	{
		return "" + this.data.getX() + " " + this.data.getY() + " " + this.data.getZ();
	}

	public static Vec3 fromString(String par1)
	{
		if (par1.isEmpty()) return new Vec3(0, 0, 0);
		String[] sa = par1.split(" ");
		return new Vec3(Double.valueOf(sa[0]), Double.valueOf(sa[1]), Double.valueOf(sa[2]));
	}
}