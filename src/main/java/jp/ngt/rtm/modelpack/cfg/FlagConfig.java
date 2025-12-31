package jp.ngt.rtm.modelpack.cfg;

public class FlagConfig extends TextureConfig
{
	/**縦横の解像度*/
	public int resolutionV, resolutionU;
	public float poleLength;

	@Override
	public void init()
	{
		super.init();

		if(this.resolutionU <= 0)
		{
			this.resolutionU = 24;
		}

		if(this.resolutionV <= 0)
		{
			this.resolutionV = 16;
		}

		if(this.poleLength <= 0.0F)
		{
			this.poleLength = 1.0F;
		}
	}
}
