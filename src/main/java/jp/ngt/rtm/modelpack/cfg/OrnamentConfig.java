package jp.ngt.rtm.modelpack.cfg;

public class OrnamentConfig extends ModelConfig implements IConfigWithType
{
	private String name;
	public ModelSource model;
	public String ornamentType;
	/**階段, 足場をコンベアとする場合の速度*/
	public float conveyorSpeed;
	/**設置時にランダムにスケール変更(の最小値)*/
	public float minRandomScale;

	@Override
	public void init()
	{
		super.init();

		if(this.minRandomScale <= 0.0F)
		{
			this.minRandomScale = 1.0F;
		}
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getSubType()
	{
		return this.ornamentType;
	}

	public static OrnamentConfig getDummy()
	{
		OrnamentConfig cfg = new OrnamentConfig();
		cfg.name = "dummy";
		cfg.ornamentType = "N";
		cfg.init();
		return cfg;
	}
}