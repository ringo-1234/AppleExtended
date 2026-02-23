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

package jp.ngt.ngtlib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import net.minecraft.entity.player.EntityPlayer;

public class NGTCertificate
{
	private static boolean KEY_REGISTERED;

	public static boolean canUse()
	{
		return KEY_REGISTERED;
	}

	public static boolean checkPlayerData(String player)//ClientProxy.preInit()
	{
		File keyFile = getKeyFile();
		if(keyFile.exists())
		{
			List<String> list;
			try
			{
				list = NGTText.readText(keyFile, "");
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return false;
			}

			if(list.size() >= 2)
			{
				byte[] ba = Base64.decodeBase64(list.get(1));
				String s = new String(ba);
				if(player.equals(s) || (list.size() == 3 && list.get(2) != null && list.get(2).equals("develop_mode")))
				{
					KEY_REGISTERED = true;
					return true;
				}
				else
				{
					NGTLog.debug("not matched player data : " + s);
				}
			}
			else
			{
				NGTLog.debug("illegal file");
			}
		}
		return false;
	}

	public static boolean registerKey(EntityPlayer player, String key)
	{
		List<String> strings = new ArrayList<String>();
		try
		{
			URL url = new URL(NGTCore.CERTIFICATION_URL);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		    String string;
			while((string = br.readLine()) != null)
			{
				strings.add(string);
			}
			br.close();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
			NGTLog.sendChatMessage(player, "message.regKey.0");
			return false;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			NGTLog.sendChatMessage(player, "message.regKey.1");
			return false;
		}

		if(strings.size() > 0)
		{
			String[] sa = strings.toArray(new String[strings.size()]);
			if(sa[0] == null || sa[0].equals("unavailable"))
			{
				NGTLog.sendChatMessage(player, "message.regKey.2");
				return false;
			}
			else if(sa[0].equals("available"))
			{
				NGTLog.sendChatMessage(player, "message.regKey.3");
				return true;
			}
			else if(sa[0].equals(key))
			{
				NGTLog.sendChatMessage(player, "message.regKey.4");
				return true;
			}
		}

		NGTLog.sendChatMessage(player, "message.regKey.5");
		return false;
	}

	public static void writePlayerData(String player)
	{
		File keyFile = getKeyFile();
		if(keyFile.exists())
		{
			keyFile.delete();
		}

		String s1 = Base64.encodeBase64String(NGTCore.metadata.version.getBytes());
		String s2 = Base64.encodeBase64String(player.getBytes());
		NGTText.writeToText(keyFile, s1 + "\n" + s2);

		KEY_REGISTERED = true;
		NGTCore.proxy.removeGuiWarning();
	}

	private static File getKeyFile()
	{
		File modsDir = NGTFileLoader.getModsDir().get(0);
		File ngtDir = new File(modsDir, "ngt");
		if(!ngtDir.exists())
		{
			ngtDir.mkdir();
		}
		return new File(ngtDir, "data.ngt");
	}
}