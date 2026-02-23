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

package jp.ngt.rtm.msims;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.block.tileentity.TileEntityStation;

/**Minecraft Station Information Management System*/
public final class MSIMS
{
	public static final MSIMS INSTANCE = new MSIMS();

	private final Map<String, TileEntityStation> stationMap = new HashMap<>();
	private final File dataFolder;

	private MSIMS()
	{
		this.dataFolder = new File(NGTFileLoader.getModsDir().get(0), "rtm/msims");
	}

	public void add(TileEntityStation station)
	{
		if(NGTUtil.isServer())
		{
			this.stationMap.put(station.getName(), station);
		}
	}

	public void loadData()
	{
		if(!this.dataFolder.exists())
		{
			this.copySampleData();
		}

		;//csv読み込み
	}

	private void copySampleData()
	{
		this.dataFolder.mkdirs();
	}
}
