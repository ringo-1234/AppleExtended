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

package jp.ngt.ngtlib.io;

import java.io.File;
import java.io.IOException;

import jp.ngt.ngtlib.NGTCore;

public class TwitterSetting
{
	public transient String consumerKey;
	public transient String consumerSecret;

	//public String userName;
	public String accessToken;
	public String accessTokenSecret;

	public void init()
	{
		//String key = NGTText.readText(this.secretFile, false, "UTF-8");
		String key = NGTCore.TWI_KEY;
		this.consumerKey = key.substring(15, 40);
		this.consumerSecret = key.substring(50, 100);
	}

	public boolean isValid()
	{
		return this.accessToken != null && !this.accessToken.isEmpty()
				&& this.accessTokenSecret != null && !this.accessTokenSecret.isEmpty();
	}

	public static TwitterSetting load(File file)
	{
		TwitterSetting setting = null;

		try
		{
			String json = NGTText.readText(file, false, "UTF-8");
			setting = NGTJson.getObjectFromJson(json, TwitterSetting.class);
		}
		catch(IOException e)
		{
			setting = new TwitterSetting();
		}

		setting.init();
		return setting;
	}

	public void save(File file)
	{
		String json = NGTJson.getJsonFromObject(this);
		NGTText.writeToText(file, json);
	}
}