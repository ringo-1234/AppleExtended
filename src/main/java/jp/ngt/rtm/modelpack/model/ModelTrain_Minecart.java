package jp.ngt.rtm.modelpack.model;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.renderer.model.MCModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModelTrain_Minecart extends MCModel
{
	private ModelBase model = new ModelMinecart();

	public ModelTrain_Minecart()
	{
		this.setSizeBox(-1.5F, 0.0F, -1.5F, 1.5F, 3.0F, 1.5F);
	}

	@Override
	public void renderAll(boolean smoothing)
	{
		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F * 3.0F);
		GL11.glPopMatrix();
	}
}