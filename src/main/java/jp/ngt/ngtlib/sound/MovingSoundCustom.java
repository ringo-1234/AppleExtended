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

package jp.ngt.ngtlib.sound;

import jp.ngt.ngtlib.io.ResourceLocationCustom;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundCustom extends MovingSound
{
	private final String soundName;
	private float prevVolume = -1.0F;
	private float prevPitch = -1.0F;
	private boolean isMCSound;
	private SoundEventAccessor seAccessor;

	public MovingSoundCustom(String sound, boolean repeat)
	{
		super(getSoundEvent(sound), SoundCategory.MASTER);
		this.soundName = sound;
		this.repeat = repeat;
		this.repeatDelay = 0;
		this.attenuationType = AttenuationType.LINEAR;//減衰率, NONEで音量変わらず
		this.isMCSound = this.checkMCSound(sound);
	}

	private boolean checkMCSound(String sound)
	{
		return SoundEvent.REGISTRY.containsKey(new ResourceLocation(sound));
	}

	@Override
	public void update()
	{
		if(this.prevVolume >= 0.0F)
		{
			this.volume = this.prevVolume;
			this.prevVolume = -1.0F;
		}

		if(this.prevPitch >= 0.0F)
		{
			this.pitch = this.prevPitch;
			this.prevPitch = -1.0F;
		}
	}

	public void stop()
	{
		this.donePlaying = true;
	}

	public void setVolume(float par1)
	{
		this.prevVolume = par1 < 0.0F ? 0.0F : par1;
	}

	public void setPitch(float par1)
	{
		this.prevPitch = par1 < 0.0F ? 0.0F : par1;
	}

	@Override
	public SoundEventAccessor createAccessor(SoundHandler handler)
    {
		if(this.isMCSound){return super.createAccessor(handler);}

		this.seAccessor = super.createAccessor(handler);
		if(this.seAccessor == null)
		{
			this.seAccessor = new SoundEventAccessorCustom(new ResourceLocationCustom(this.soundName));
		}
		this.sound = this.seAccessor.cloneEntry();
        return this.seAccessor;
    }

	private static SoundEvent getSoundEvent(String sound)
	{
		SoundEvent se = SoundEvent.REGISTRY.getObject(new ResourceLocation("minecraft", sound));
		if(se == null)
		{
			return new SoundEventDummy(sound);
		}
		return se;
	}

	//getSoundName()返すだけ用
	private static class SoundEventDummy extends SoundEvent
	{
		public SoundEventDummy(String sound)
		{
			this(new ResourceLocationCustom(sound));
		}

		private SoundEventDummy(ResourceLocation soundNameIn)
		{
			super(soundNameIn);
		}
	}
}
