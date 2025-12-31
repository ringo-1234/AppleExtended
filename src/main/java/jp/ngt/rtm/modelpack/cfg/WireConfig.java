package jp.ngt.rtm.modelpack.cfg;

public class WireConfig extends ModelConfig
{
	private String name;
	public ModelSource model;
	/**たわみ係数*/
	public float deflectionCoefficient;
	/**長さ係数*/
	public float lengthCoefficient;
	public float sectionLength;
	public float yOffset;

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public static WireConfig getDummy()
	{
		WireConfig cfg = new WireConfig();
		cfg.name = "dummy";
		cfg.init();
		return cfg;
	}
}