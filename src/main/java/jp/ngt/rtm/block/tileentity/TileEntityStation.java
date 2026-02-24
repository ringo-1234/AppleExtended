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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.rtm.msims.MSIMS;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityStation extends TileEntityCustom
{
	private String stationName;
	public int maxHeight;

	public TileEntityStation()
	{
		this.stationName = String.format("default_%d", System.currentTimeMillis());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.setName(nbt.getString("station_name"));

        this.sendPacket();
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("station_name", this.stationName);
        return nbt;
    }

	public String getName()
	{
		return this.stationName;
	}

	public void setName(String par1)
	{
		this.stationName = par1;
		this.markDirty();
		MSIMS.INSTANCE.add(this);
	}

	public void checkHeight()
	{
		BlockPos pos = this.getPos();
		this.maxHeight = this.getWorld().getHeight(pos).getY() - pos.getY();
	}

	@Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
		return Double.POSITIVE_INFINITY;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		return INFINITE_EXTENT_AABB;
    }
}