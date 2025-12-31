package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.OrnamentConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetOrnament extends ModelSetBase<OrnamentConfig>
{
	public ModelSetOrnament()
	{
		super();
	}

	public ModelSetOrnament(OrnamentConfig par1)
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
			this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/container/button_19g_JRF_0.png");
		}
		else
		{
			OrnamentConfig cfg = this.getConfig();
			this.modelObj = new ModelObject(cfg.model, this, null);
			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
		}
	}

	@Override
	public OrnamentConfig getDummyConfig()
	{
		return OrnamentConfig.getDummy();
	}
}