package jp.ngt.rtm.modelpack.cfg;

public class MachineConfig extends ModelConfig implements IConfigWithType
{
	/**名前(重複不可)*/
	private String name;
	/**モデル*/
	public ModelSource model;
	public String machineType;

	public String sound_OnActivate;
	public String sound_Running;

	public boolean rotateByMetadata;
	public boolean followRailAngle;//ACTなどをカントに追従
	public int[] brightness;

	@Override
	public void init()
	{
		super.init();

		this.sound_OnActivate = this.fixSoundPath(this.sound_OnActivate);
		this.sound_Running = this.fixSoundPath(this.sound_Running);

		if(this.brightness == null || this.brightness.length < 2)
		{
			this.brightness = new int[]{0, 0};
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
		return this.machineType;
	}

	public static MachineConfig getDummy()
	{
		MachineConfig cfg = new MachineConfig();
		cfg.name = "dummy";
		cfg.machineType = "N";
		cfg.init();
		return cfg;
	}
}