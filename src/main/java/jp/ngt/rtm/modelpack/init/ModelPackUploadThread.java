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

package jp.ngt.rtm.modelpack.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.network.PacketModelPack;

/**S->Cへモデルパックを送信、Cから要求があれば開始*/
public class ModelPackUploadThread extends Thread
{
	private ByteBuffer buffer = ByteBuffer.allocate(RTMCore.PACKET_SIZE);

	public ModelPackUploadThread()
	{
	    super("RTM ModelPack Upload");
	}

	public static void startThread()
	{
		if(!RTMCore.useServerModelPack){return;}
		ModelPackUploadThread thread = new ModelPackUploadThread();
		thread.start();
	}

	@Override
	public void run()
	{
		NGTLog.debug("[RTM](UploadThread) Start uploading ModelPack");
		List<File> fileList = NGTFileLoader.findFile((file)->{return file.getName().startsWith("ModelPack_") && file.getName().endsWith(".zip");});
		for(File file : fileList)
		{
			try
			{
				NGTLog.debug("[RTM](UploadThread) Start uploading " + file.getName());
				RTMCore.NETWORK_WRAPPER.sendToAll(new PacketModelPack("start_file:" + file.getName(), 0, ByteBuffer.allocate(RTMCore.PACKET_SIZE)));

				@SuppressWarnings("resource")
				FileChannel channel = new FileInputStream(file).getChannel();
				long size = channel.size();
				while(channel.read(this.buffer) >= 0)
				{
					this.buffer.flip();
					RTMCore.NETWORK_WRAPPER.sendToAll(new PacketModelPack(file.getName(), size, this.buffer));
					this.buffer.clear();
					this.sleep(100);
				}
				channel.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketModelPack("finish", 0, ByteBuffer.allocate(RTMCore.PACKET_SIZE)));
		NGTLog.debug("[RTM](UploadThread) Finish uploading ModelPack");
	}
}