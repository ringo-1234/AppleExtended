package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.sound.SpeakerSounds;

public class TileEntitySpeaker extends TileEntityMachineBase implements IProvideElectricity
{
	@Override
	protected ResourceType getSubType()
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
