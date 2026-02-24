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
import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundCustom extends Sound
{
	private final ResourceLocation location;

	@Deprecated
	public SoundCustom(Sound oldSound, ResourceLocation par2)
	{
		super(oldSound.getSoundLocation().toString(), oldSound.getVolume(), oldSound.getPitch(), oldSound.getWeight(), oldSound.getType(), oldSound.isStreaming());
		this.location = par2;
	}

	public SoundCustom(ResourceLocation src)
	{
		super(src.toString(), 1.0F, 1.0F, 1, Type.FILE, false);//weight:ランダム再生に使う?
		this.location = src;
	}

	@Override
	public ResourceLocation getSoundLocation()
    {
        return this.location;
    }

	@Override
    public ResourceLocation getSoundAsOggLocation()
    {
		if(this.location.getResourcePath().contains("ogg"))
		{
			return this.location;
		}
		else
		{
			String path = this.location.getResourcePath().replace('.', '/');
	        return new ResourceLocationCustom(this.location.getResourceDomain(), "sounds/" + path + ".ogg");
		}
    }
}