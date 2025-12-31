package jp.ngt.rtm.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderMMBB extends Render<EntityMMBoundingBox>
{
	public RenderMMBB(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void doRender(EntityMMBoundingBox entity, double x, double y, double z, float p_76986_8_, float p_76986_9_)
    {
        //this.renderAABB(entity, x, y, z);
    }

	private void renderAABB(Entity entity, double x, double y, double z)
	{
		GL11.glPushMatrix();
        renderOffsetAABB(entity.getEntityBoundingBox(), x - entity.lastTickPosX, y - entity.lastTickPosY, z - entity.lastTickPosZ);
        GL11.glPopMatrix();
	}

	@Override
    protected ResourceLocation getEntityTexture(EntityMMBoundingBox entity)
    {
        return null;
    }
}