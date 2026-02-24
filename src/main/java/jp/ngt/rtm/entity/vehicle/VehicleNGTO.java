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

package jp.ngt.rtm.entity.vehicle;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.renderer.GLObject;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class VehicleNGTO
{
	/**TileEntityMMでの一時保存時はnull*/
	@Nullable
	public final NGTObject ngto;
	public final float scale;
	public final float offsetX, offsetY, offsetZ;
	public float riderPosX, riderPosY, riderPosZ;
	public int type;

	@SideOnly(Side.CLIENT)
	public GLObject[] glLists;

	public VehicleNGTO(@Nullable NGTObject par1, float par2, float par3, float par4, float par5)
	{
		this.ngto = par1;
		this.offsetX = par2;
		this.offsetY = par3;
		this.offsetZ = par4;
		this.scale = par5;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("OffsetX", this.offsetX);
		nbt.setFloat("OffsetY", this.offsetY);
		nbt.setFloat("OffsetZ", this.offsetZ);
		nbt.setFloat("RiderPosX", this.riderPosX);
		nbt.setFloat("RiderPosY", this.riderPosY);
		nbt.setFloat("RiderPosZ", this.riderPosZ);
		nbt.setFloat("Scale", this.scale);
		nbt.setInteger("Type", this.type);
		if(this.ngto != null)
		{
			nbt.setTag("NGTO", this.ngto.writeToNBT());
		}
		return nbt;
	}

	public static VehicleNGTO readFromNBT(NBTTagCompound nbt, boolean allowNullNGTO)
	{
		float ox = nbt.getFloat("OffsetX");
		float oy = nbt.getFloat("OffsetY");
		float oz = nbt.getFloat("OffsetZ");
		float sc = nbt.getFloat("Scale");
		NGTObject ngto = null;
		if(nbt.hasKey("NGTO"))
		{
			ngto = NGTObject.readFromNBT(nbt.getCompoundTag("NGTO"));
		}

		VehicleNGTO obj = new VehicleNGTO(ngto, ox, oy, oz, sc);
		obj.riderPosX = nbt.getFloat("RiderPosX");
		obj.riderPosY = nbt.getFloat("RiderPosY");
		obj.riderPosZ = nbt.getFloat("RiderPosZ");
		obj.type = nbt.getInteger("Type");
		return (allowNullNGTO || ngto != null) ? obj : null;
	}
}