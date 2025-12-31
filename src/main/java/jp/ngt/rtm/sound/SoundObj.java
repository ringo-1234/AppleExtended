package jp.ngt.rtm.sound;

import jp.ngt.ngtlib.io.ResourceLocationCustom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Deprecated
public class SoundObj
{
	private final String name;
	private final ResourceLocation location;
	private SoundEvent sound;

	/**
	 * RTMSoundManager.register()からのみ生成
	 * @param par1 "domain:name"
	 */
	public SoundObj(String par1)
	{
		this.name = par1;
		String[] sa = par1.split(":");
		this.location = new ResourceLocationCustom(sa[0], sa[1]);
		this.sound = new SoundEvent(this.location);
	}

	/**SoundEventの登録*/
	public void init()
	{
		this.sound.setRegistryName(this.location);
		ForgeRegistries.SOUND_EVENTS.register(this.sound);
	}

	public SoundEvent getSound()
	{
		return this.sound;
	}

	public String getName()
	{
		return this.name;
	}

	public ResourceLocation getResourceLocation()
	{
		return this.location;
	}
}