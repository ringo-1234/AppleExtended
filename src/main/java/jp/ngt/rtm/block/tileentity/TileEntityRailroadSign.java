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

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityPlaceable;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.modelset.TextureSetRRS;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityRailroadSign extends TileEntityPlaceable implements IResourceSelector
{
	private ResourceState<TextureSetRRS> state = new ResourceState<>(RTMResource.RRS, this);

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.state.readFromNBT(nbt.getCompoundTag("State"));

		if(this.state.version < 1)
		{
			this.getResourceState().setResourceName(nbt.getString("textureName"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setTag("State", this.state.writeToNBT());
		return nbt;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(this.getPos(), this.getPos().add(1, 2, 1)).offset(getOffsetX(), getOffsetY(), getOffsetZ());
	}

	@Override
	public void updateResourceState()
	{
		if(this.world == null || !this.world.isRemote)
		{
			this.sendPacket();
			this.markDirty();
			BlockUtil.markBlockForUpdate(this.getWorld(), this.getPos());
		}
	}

	@Override
	public int[] getSelectorPos()
	{
		return new int[]{this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()};
	}

	@Override
	public ResourceState<TextureSetRRS> getResourceState()
	{
		return this.state;
	}

	@Override
	public boolean closeGui(ResourceState par1)
	{
		return true;
	}
}