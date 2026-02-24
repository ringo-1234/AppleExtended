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

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.sound.SpeakerSounds;

public class TileEntitySpeaker extends TileEntityMachineBase implements IProvideElectricity
{
	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_SPEAKER;
	}

	@Override
	public int getElectricity()
	{
		return 0;
	}

	@Override
	public void setElectricity(int x, int y, int z, int level)
	{
		if(!this.world.isRemote)
		{
			if(level > 0 && level <= SpeakerSounds.MAX_SOUND_ID)
			{
				String sound = SpeakerSounds.getInstance(!this.world.isRemote).getSound(level);
				if(sound != null && !sound.equals("null"))
				{
					RTMCore.proxy.playSound(this, sound, 1.0F, 1.0F);
				}
			}
		}
	}
}
