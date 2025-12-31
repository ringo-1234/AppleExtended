package jp.ngt.rtm.modelpack.cfg;

import jp.ngt.rtm.modelpack.state.DataMap;

/**全てのコンフィグの親, JSONからの読み込みに使用*/
public abstract class ResourceConfig
{
	/**name重複時の優先度決定用*/
	public short version;

	public boolean useCustomColor;

	/**検索用タグ*/
	public String tags;

	/**DataMapのデフォルト値*/
	public DMInitValue[] defaultValues;
	@Deprecated
	public String defaultData;

	/**Resource名, 重複不可*/
	public abstract String getName();

	/**Configの初期化時に呼ばれる*/
	public void init()
	{
		if(this.defaultValues == null && this.defaultData != null)
		{
			String[][] array = DataMap.convertArg(this.defaultData);
			this.defaultValues = new DMInitValue[array.length];
			for(int i = 0; i < array.length; ++i)
			{
				DMInitValue dmiv = new DMInitValue();
				dmiv.key = array[i][0];
				dmiv.type = array[i][1];
				dmiv.value = array[i][2];
				this.defaultValues[i] = dmiv;
			}
		}
	}

	protected String fixSoundPath(String path)
	{
		if(path == null || path.length() == 0)
		{
			return null;
		}
		else if(!path.contains(":"))
		{
			return "rtm:" + path;//ドメイン未指定のファイルパスがminecraftドメインと解釈されないように
		}
		return path;
	}

	public class DMInitValue
	{
		public String type;
		public String key;
		public String value;
		/**入力候補*/
		public String[] suggestions;
		/**[min, max]*/
		public double[] minmax;
		/**[start, contains, end]*/
		public String[] pattern;
	}
}