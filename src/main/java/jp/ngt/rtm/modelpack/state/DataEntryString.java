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

import net.minecraft.nbt.NBTTagCompound;

public final class DataEntryString extends DataEntry<String> {
    public DataEntryString(String value, int flag) {
        super(value, flag);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.data = nbt.getString("Data");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("Data", this.data);
        nbt.setString("Type", this.getType().key);
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}