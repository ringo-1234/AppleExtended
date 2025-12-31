package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.FirearmConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetFirearm extends ModelSetBase<FirearmConfig>
{
	public ModelSetFirearm()
	{
		super();
	}

	public ModelSetFirearm(FirearmConfig par1)
	{
		super(par1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		if(this.isDummy())
		{
			this.modelObj = ModelObject.getDummy();
			this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/firearm/button_40cmArtillery.png");
		}
		else
		{
			FirearmConfig cfg = this.getConfig();
			this.modelObj = new ModelObject(cfg.model, this, null);
			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
		}
	}

	@Override
	public FirearmConfig getDummyConfig()
	{
		return FirearmConfig.getDummyConfig();
	}
}