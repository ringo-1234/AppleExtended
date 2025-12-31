package jp.ngt.rtm.modelpack.cfg;


public class RRSConfig extends TextureConfig
{
	@Override
	public void init()
	{
		super.init();

		this.width = 0.5F;
		this.height = 0.5F;
	}

	public RRSConfig(String name)
	{
		this.texture = fixName(name);
	}

	public static String fixName(String par1)
	{
		if(!par1.contains("textures"))
        {
        	return "textures/rrs/" + par1;
        }
		return par1;
	}

	@Override
	public int getUCountInGui()
	{
		return 8;
	}

	@Override
	public int getVCountInGui()
	{
		return 4;
	}
}