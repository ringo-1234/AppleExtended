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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.rtm.block.decoration.DecorationModel;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityDecoration extends TileEntityCustom {
    private String modelName = DecorationModel.DEFAULT_MODEL.name;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.modelName = nbt.getString("ModelName");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("ModelName", this.modelName);
        return nbt;
    }

    public void setModelName(String par1) {
        this.modelName = par1;
        this.sendPacket();
    }

    public String getModelName() {
        return this.modelName;
    }
}