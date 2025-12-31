package jp.ngt.rtm.modelpack.modelset;

import javax.script.ScriptEngine;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig;
import jp.ngt.rtm.render.BasicVehiclePartsRenderer;
import jp.ngt.rtm.render.ModelObject;
import jp.ngt.rtm.render.PartsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModelSetVehicleBase<T extends VehicleBaseConfig> extends ModelSetBase<T>
{
	@SideOnly(Side.CLIENT)
	public ResourceLocation rollsignTexture;
    @SideOnly(Side.CLIENT)
    public ResourceLocation rollsignTexture2;
	@SideOnly(Side.CLIENT)
	public ScriptEngine soundSE;

	public ModelSetVehicleBase()
	{
		super();

	}

	public ModelSetVehicleBase(VehicleBaseConfig par1)
	{
		super((T)par1);
	}

	@Override
	public void constructOnServer()
	{
		super.constructOnServer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		if(this.isDummy())
		{
			this.modelObj = ModelObject.getDummy();
			this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/train/hoge.png");
			this.rollsignTexture = null;
		}
		else
		{
			PartsRenderer renderer = (!PartsRenderer.validPath(cfg.getModel().rendererPath)) ? new BasicVehiclePartsRenderer(String.valueOf(true)) : null;
			this.modelObj = new ModelObject(cfg.getModel(), this, renderer, "vehicle");

			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
			this.rollsignTexture = cfg.rollsignTexture == null ? null : ModelPackManager.INSTANCE.getResource(cfg.rollsignTexture);
            this.rollsignTexture2 = (cfg.rollsignTexture2 == null || cfg.rollsignTexture2.isEmpty()) ?
                    null : ModelPackManager.INSTANCE.getResource(cfg.rollsignTexture2);

			if(this.cfg.soundScriptPath != null)
			{
				this.soundSE = ScriptUtil.doScript(ModelPackManager.INSTANCE.getScript(this.getConfig().soundScriptPath));
			}
		}
	}

	@Override
	public T getConfig()
	{
		return this.cfg;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderModelInGui(Minecraft par1)
	{
        VehicleBaseConfig cfg = (VehicleBaseConfig)this.cfg;
        this.modelObj.render(null, cfg, 0, 0.0F);
        this.modelObj.render(null, cfg, 1, 0.0F);
		this.renderPartsInGui(par1);
	}

	@SideOnly(Side.CLIENT)
	protected void renderPartsInGui(Minecraft par1)
	{
		;
	}
}