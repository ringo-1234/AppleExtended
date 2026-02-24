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

import jp.ngt.ngtlib.event.TickProcessQueue;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;

public class NGTSound
{
	public static void playSound(final ISound sound)
	{
		TickProcessQueue.getInstance(Side.CLIENT).add((world)->{
			//Packet受診タイミングで鳴らすとConcurrentModificationExceptionになる？のを回避
			NGTUtilClient.getMinecraft().getSoundHandler().playSound(sound);
			return true;
		});
	}
}
