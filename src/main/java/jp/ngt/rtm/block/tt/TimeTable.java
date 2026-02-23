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

package jp.ngt.rtm.block.tt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TimeTable
{
	public final String fileName;
	public String name;
	public String description;
	/**現実時間を使用(←→MC時間)*/
	public boolean useRealTime;
	public final Map<Pattern, Integer> textColorMap = new HashMap<>();
	/**[駅][列車]*/
	public TTEntry[][] ttData;
	/**列車名*/
	public String[] trainName;
	/**列車番号→横軸No*/
	public final BiMap<String, Integer> trainAxis = HashBiMap.create();
	/**駅名→縦軸No*/
	public final BiMap<String, Integer> stationAxis = HashBiMap.create();
	/**Entryの列名*/
	public final List<String> colNames = new ArrayList<>();

	public TimeTable(String name, String[][] csv)
	{
		this.fileName = name;

		this.loadCSV(csv);
	}

	private void loadCSV(String[][] csv)
	{
		TTSection currentSection = TTSection.Config;
		int sectionCount = 0;
		List<List<String>> idListTemp = new ArrayList<>();
		Map<String, TTEntry> entryMapTemp = new HashMap<>();

		for(String[] sa : csv)
		{
			if(sa == null || sa.length == 0){continue;}

			String firstStr = sa[0];

			if(firstStr.startsWith("#"))//コメント行
			{
				;
			}
			else if(firstStr.startsWith("<"))
			{
				TTSection section = TTSection.get(firstStr);
				if(section != null)
				{
					currentSection = section;
					sectionCount = 0;
				}
			}
			else if(currentSection == TTSection.Config)
			{
				if(firstStr.startsWith("name"))
				{
					this.name = this.parseString(firstStr);
				}
				else if(firstStr.startsWith("description"))
				{
					this.description = this.parseString(firstStr);
				}
				else if(firstStr.startsWith("useRealTime"))
				{
					this.useRealTime = Boolean.valueOf(this.parseString(firstStr));
				}
			}
			else if(currentSection == TTSection.Color)
			{
				String[] sa2 = firstStr.split("=");
				String key = sa2[0];
				int color = Integer.decode(sa2[1]);
				this.textColorMap.put(Pattern.compile(key), color);
			}
			else if(currentSection == TTSection.TimeTable)
			{
				if(sectionCount == 1)
				{
					for(int i = 1; i < sa.length; ++i)
					{
						this.trainAxis.put(sa[i], i - 1);
						TimeTableManager.INSTANCE.addTTAndTrain(sa[i], this);
					}
				}
				else if(sectionCount == 2)
				{
					this.trainName = new String[sa.length - 1];
					for(int i = 1; i < sa.length; ++i)
					{
						this.trainName[i - 1] = sa[i];
					}
				}
				else
				{
					int row = sectionCount - 3;
					this.stationAxis.put(sa[0], row);
					List<String> list = new ArrayList<>();
					idListTemp.add(list);
					for(int i = 1; i < sa.length; ++i)
					{
						list.add(sa[i]);
					}
				}
			}
			else if(currentSection == TTSection.Entry)
			{
				if(sectionCount == 1)
				{
					for(String s : sa)
					{
						this.colNames.add(s);
					}
				}
				else
				{
					entryMapTemp.put(sa[0], new TTEntry(sa));
				}
			}

			++sectionCount;
		}

		this.convertData(idListTemp, entryMapTemp);
	}

	private void convertData(List<List<String>> ids, Map<String, TTEntry> entries)
	{
		this.ttData = new TTEntry[ids.size()][];
		for(int i = 0; i < ids.size(); ++i)
		{
			List<String> list = ids.get(i);
			this.ttData[i] = new TTEntry[list.size()];
			for(int j = 0; j < list.size(); ++j)
			{
				this.ttData[i][j] = entries.get(list.get(j));
			}
		}
	}

	private String parseString(String str)
	{
		return str.split("=")[1];
	}

	public class TTEntry
	{
		public final String[] data;
		/**到着時刻(秒)*/
		public final int arrivalTime;
		/**発車時刻(秒)*/
		public final int departureTime;
		public final byte trackNum;

		public TTEntry(String[] entry)
		{
			this.data = entry;
			this.arrivalTime = this.convertTime(entry[1]);
			this.departureTime = this.convertTime(entry[2]);
			this.trackNum = Byte.valueOf(entry[3]);
		}

		private int convertTime(String s)
		{
			String[] sa = s.split(":");
			int hour = Byte.valueOf(sa[0]);
			int minute = Byte.valueOf(sa[1]);
			return (hour * 60 + minute) * 60;
		}
	}

	public enum TTSection
	{
		Config,
		Color,
		TimeTable,
		Entry;

		public static TTSection get(String str)
		{
			for(TTSection type : TTSection.values())
			{
				if(str.contains(type.toString()))
				{
					return type;
				}
			}
			return null;
		}
	}
}
