package jp.ngt.rtm.entity.train.parts;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.ClientProxy;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.cfg.ModelConfig.Parts;
import jp.ngt.rtm.modelpack.modelset.ModelSetFirearm;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderArtillery extends Render<EntityArtillery>
{
	public RenderArtillery(RenderManager renderManager)
	{
		super(renderManager);
	}

	public void renderArtillery(EntityArtillery entity, double par2, double par4, double par6, float par8, float par9)
    {
		GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);

        ModelSetFirearm modelSet = entity.getResourceState().getResourceSet();
        if(modelSet == null || modelSet.isDummy())
        {
        	RTMCore.proxy.renderMissingModel();
        }
        else if(modelSet.getConfig().fpvMode && !this.shouldRender(entity))
        {
        	;
        }
        else
        {
        	GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            //GL11.glRotatef(-par1.rotationPitch, 1.0F, 0.0F, 0.0F);

        	if(modelSet.getConfig().useOldSystem)
        	{
        		this.renderArtilleryOld(entity, modelSet);
        	}
        	else
        	{
        		int pass = MinecraftForgeClient.getRenderPass();
        		modelSet.modelObj.render(entity, modelSet.getConfig(), pass, par8);
        	}
        }

        GL11.glPopMatrix();
    }

	private void renderArtilleryOld(EntityArtillery entity, ModelSetFirearm set)
	{
		this.bindTexture(set.modelObj.textures[0].material.texture);

		GL11.glTranslatef(set.getConfig().modelPartsN.pos[0], set.getConfig().modelPartsN.pos[1], set.getConfig().modelPartsN.pos[2]);
        this.renderParts(set.modelObj.model, set.getConfig().modelPartsN);

        float[] posY = set.getConfig().modelPartsY.pos;
		GL11.glTranslatef(posY[0], posY[1], posY[2]);
		GL11.glRotatef(entity.getBarrelYaw(), 0.0F, 1.0F, 0.0F);

		GL11.glPushMatrix();
		GL11.glTranslatef(-posY[0], -posY[1], -posY[2]);
        this.renderParts(set.modelObj.model, set.getConfig().modelPartsY);
        GL11.glPopMatrix();

        float[] posX = set.getConfig().modelPartsX.pos;
		GL11.glTranslatef(posX[0]-posY[0], posX[1]-posY[1], posX[2]-posY[2]);
		GL11.glRotatef(entity.getBarrelPitch(), 1.0F, 0.0F, 0.0F);

		GL11.glPushMatrix();
		GL11.glTranslatef(-posX[0], -posX[1], -posX[2]);
        this.renderParts(set.modelObj.model, set.getConfig().modelPartsX);
        GL11.glPopMatrix();

        float[] posB = set.getConfig().modelPartsBarrel.pos;
		GL11.glTranslatef(posB[0]-posX[0], posB[1]-posX[1], posB[2]-posX[2]);

		GL11.glPushMatrix();
		GL11.glTranslatef(-posB[0], -posB[1], -posB[2]);
		if(entity.getRecoil() > 0.0F)
		{
			float recoil = set.getConfig().recoil * entity.getRecoil();
			GL11.glTranslatef(0.0F, 0.0F, -recoil);
		}
        this.renderParts(set.modelObj.model, set.getConfig().modelPartsBarrel);
        GL11.glPopMatrix();
	}

	private void renderParts(IModelNGT model, Parts parts)
	{
		model.renderOnly(RTMCore.smoothing, parts.objects);
	}

	@Override
    public void doRender(EntityArtillery par1, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderArtillery(par1, par2, par4, par6, par8, par9);
    }

	@Override
    protected ResourceLocation getEntityTexture(EntityArtillery par1)
    {
        return null;
    }

	@Override
	protected boolean bindEntityTexture(EntityArtillery entiy){return false;}

    private boolean shouldRender(EntityArtillery par1)
    {
    	if(par1.getFirstPassenger() != null && par1.getFirstPassenger().equals(NGTUtilClient.getMinecraft().player))
    	{
    		if(ClientProxy.getViewMode(NGTUtilClient.getMinecraft().player) == ClientProxy.ViewMode_Artillery)
        	{
        		return false;
        	}
    	}
    	return true;
    }
}