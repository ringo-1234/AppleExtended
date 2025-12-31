package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.cfg.FlagConfig;

public class TextureSetFlag extends TextureSetBase<FlagConfig>
{
	public TextureSetFlag()
	{
		super();
	}

	public TextureSetFlag(FlagConfig par1)
	{
		super(par1);
	}

	@Override
	public FlagConfig getDummyConfig()
	{
		FlagConfig dummy = new FlagConfig();
		dummy.texture = "textures/flag/flag_RTM3Anniversary.png";
		dummy.width = 1.0F;
		dummy.height = 1.0F;
		return dummy;
	}
}