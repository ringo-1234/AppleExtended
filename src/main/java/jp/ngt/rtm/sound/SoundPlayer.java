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

package jp.ngt.rtm.sound;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.ClientProxy;
import net.minecraft.tileentity.TileEntity;

/**SoundUpdaterのTileEntity版*/
public class SoundPlayer
{
	private SoundPlayer(){}

	public void playSound(TileEntity tile, String sound, boolean repeat){}

	public void stopSound(){}

	public boolean isPlaying()
	{
		return false;
	}

	public static SoundPlayer create()
	{
		return NGTUtil.isServer() ? new SoundPlayer() : new SoundPlayerClient();
	}

	private static class SoundPlayerClient extends SoundPlayer
	{
		private MovingSoundTileEntity sound;

		@Override
		public void playSound(TileEntity tile, String sound, boolean repeat)
		{
			if(this.sound != null)
			{
				this.stopSound();
			}

			this.sound = MovingSoundMaker.create(tile, sound, repeat);
			if(this.sound != null)
			{
				this.sound.setVolume(4.0F);
				ClientProxy.playSound(this.sound, 10.0F, 1.0F);
			}
		}

		@Override
		public void stopSound()
		{
			if(this.sound != null)
			{
				this.sound.stop();
				this.sound = null;
			}
		}

		@Override
		public boolean isPlaying()
		{
			return this.sound != null;
		}
	}
}