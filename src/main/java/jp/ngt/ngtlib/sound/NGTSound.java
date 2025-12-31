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
