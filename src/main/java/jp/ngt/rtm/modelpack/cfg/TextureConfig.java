package jp.ngt.rtm.modelpack.cfg;


public abstract class TextureConfig extends ResourceConfig
{
	/**使用する画像のパス*/
	public String texture;
	/**マイクラ内での大きさ*/
	public float height, width, depth;

	@Override
	public String getName()
	{
		return this.texture;
	}

	public int getUCountInGui()
	{
		return 4;
	}

	public int getVCountInGui()
	{
		return 2;
	}
}