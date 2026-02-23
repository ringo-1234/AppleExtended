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

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import jp.ngt.rtm.sound.SoundPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCrossingGate extends TileEntityMachineBase
{
	/**棒の動き*/
	public int barMoveCount = 0;
	/**ライト点滅*/
	public int lightCount = -1;
	private int tickCountOnActive;

	private SoundPlayer soundPlayer = SoundPlayer.create();//NoClassナントカ対策

	@Override
	public void update()
	{
		super.update();

		if(this.world.isBlockIndirectlyGettingPowered(this.getPos()) > 0)
		{
			if(this.barMoveCount < 90)
			{
				++this.barMoveCount;
			}

			this.tickCountOnActive = (this.tickCountOnActive + 1) % 360;

			if(this.lightCount < 0)
			{
				this.lightCount = 0;
			}
			else if(this.tickCountOnActive % 10 == 0)
			{
				this.lightCount = (this.lightCount + 1) % 2;
			}

			if(this.world.isRemote)
			{
				ModelSetMachine set = this.getResourceState().getResourceSet();
				if(!this.soundPlayer.isPlaying() && set.getConfig().sound_Running != null)
				{
					this.soundPlayer.playSound(this, set.getConfig().sound_Running, true);
				}
			}
		}
		else
		{
			if(this.barMoveCount > 0)
			{
				--this.barMoveCount;
			}

			this.tickCountOnActive = 0;
			this.lightCount = -1;

			if(this.world.isRemote)
			{
				if(this.soundPlayer.isPlaying())
				{
					this.soundPlayer.stopSound();
				}
			}
		}
	}

	@Override
	public void onActivate(){}

	@Override
	public void updateResourceState()
	{
		super.updateResourceState();

		if(this.world != null && this.world.isRemote)
		{
			if(this.soundPlayer.isPlaying())
			{
				this.soundPlayer.stopSound();
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		float[] box = this.getResourceState().getResourceSet().getConfig().renderAABB;
		BlockPos pos = this.getPos();
		AxisAlignedBB aabb = new AxisAlignedBB(
				pos.getX() + box[0], pos.getY() + box[1], pos.getZ() + box[2],
				pos.getX() + box[3], pos.getY() + box[4], pos.getZ() + box[5]);
		return aabb.offset(getOffsetX(), getOffsetY(), getOffsetZ());
	}

	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_GATE;
	}
}