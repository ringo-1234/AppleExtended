package jp.ngt.rtm.render;

import jp.ngt.ngtlib.renderer.model.MCModel;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.minecraft.entity.Entity;

public class MCVehicleRenderer extends VehiclePartsRenderer
{
	private MCModel model;
	private boolean light;
	private boolean alphaBlend;

	public MCVehicleRenderer(String... par1)
	{
		super(par1);
	}

	@Override
	public void init(ModelSetVehicleBase par1, ModelObject par2)
	{
		this.model = (MCModel)par2.model;
		this.light = par2.light;
		this.alphaBlend = par2.alphaBlend;
	}

	@Override
	public void render(Entity entity, RenderPass pass, float par3)
	{
		if((!this.light && pass.id >= 2) || (!this.alphaBlend && pass == RenderPass.TRANSPARENT)){return;}

		this.model.renderAll(false);
	}
}