package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.ConnectorConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetConnector extends ModelSetBase<ConnectorConfig>
{
	public ModelSetConnector()
	{
		super();
	}

	public ModelSetConnector(ConnectorConfig cfg)
	{
		super(cfg);
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
			ConnectorConfig cfg = this.getConfig();
			this.modelObj = new ModelObject(cfg.model, this, null);
			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
		}
	}

	@Override
	public ConnectorConfig getDummyConfig()
	{
		return ConnectorConfig.getDummy();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderModelInGui(Minecraft par1)
	{
        ModelObject mo = this.modelObj;
        NGTUtilClient.bindTexture(mo.textures[0].material.texture);
        mo.model.renderAll(false);
	}
}