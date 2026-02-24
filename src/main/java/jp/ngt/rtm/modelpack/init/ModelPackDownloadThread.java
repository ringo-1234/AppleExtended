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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;

@Deprecated
public class ModelPackDownloadThread extends Thread
{
	private final ModelPackWriter writer;
	/**0:wait, 1:start, 2:writing, 3:finish*/
	public int writingStatus;
	public String fileName;
	public FileChannel channel;

	public ModelPackDownloadThread(ModelPackWriter par1)
	{
	    super("RTM ModelPack Download");
	    this.writer = par1;
	    this.writingStatus = 0;
	}

	@SuppressWarnings("resource")
	@Override
	public void run()
	{
		NGTLog.debug("[RTM](DownloadThread) Start downloading ModelPack");
		File modsDir = NGTFileLoader.getModsDir().get(0);

		while(this.writingStatus != 3)
		{
			if(this.writingStatus == 1)
			{
				NGTLog.debug("[RTM](DownloadThread) Start writing " + this.fileName);

				try
				{
					this.channel = new FileOutputStream(new File(modsDir, this.fileName)).getChannel();
					this.setState(2);
					while(this.writingStatus == 2)
					{
						this.sleep(50);
					}
					this.channel.close();
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				this.sleep(50);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		NGTLog.debug("[RTM](DownloadThread) Finish downloading ModelPack");
	}

	private void setState(int par1)
	{
		NGTLog.debug("set status " + par1);
		this.writingStatus = par1;
		this.writer.resume();
	}
}