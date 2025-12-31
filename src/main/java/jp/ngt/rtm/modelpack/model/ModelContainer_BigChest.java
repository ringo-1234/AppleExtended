package jp.ngt.rtm.modelpack.model;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.renderer.model.MCModel;
import net.minecraft.client.model.ModelChest;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModelContainer_BigChest extends MCModel
{
	private ModelChest model = new ModelChest();

	public ModelContainer_BigChest()
	{
		this.setSizeBox(-1.5F, 0.0F, -1.5F, 1.5F, 3.0F, 1.5F);
	}

	@Override
	public void renderAll(boolean smoothing)
	{
		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(-1.5F, -3.0F, -1.5F);
		float scale = 3.0F;
		GL11.glScalef(scale, scale, scale);
		this.model.renderAll();
		GL11.glPopMatrix();
	}
}