package jp.ngt.ngtlib.sound;

import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundEventAccessorCustom extends SoundEventAccessor
{
	private final ResourceLocation location;
	private final Sound sound;

	public SoundEventAccessorCustom(ResourceLocation locationIn)
	{
		super(locationIn, null);
		this.location = locationIn;
		this.sound = new SoundCustom(locationIn);
	}

	@Override
	public Sound cloneEntry()
    {
		return this.sound;
    }
}
