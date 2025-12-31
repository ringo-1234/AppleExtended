package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.cfg.RRSConfig;

public class TextureSetRRS extends TextureSetBase<RRSConfig>
{
	public TextureSetRRS()
	{
		super();
	}

	public TextureSetRRS(RRSConfig par1)
	{
		super(par1);
	}

	@Override
	public RRSConfig getDummyConfig()
	{
		RRSConfig dummy = new RRSConfig("");
		dummy.texture = "textures/rrs/rrs_01.png";
		return dummy;
	}
}