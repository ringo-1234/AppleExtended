/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.rtm.entity.npc;

import java.util.Calendar;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.rtm.item.ItemGun;
import jp.ngt.rtm.modelpack.modelset.ModelSetNPC;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderNPC extends RenderBiped<EntityNPC>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("rtm", "textures/motorman.png");
	private static final ResourceLocation TEX_SANTA = new ResourceLocation("rtm", "textures/motorman_santa.png");
	private static final ResourceLocation TEX_SHISHI = new ResourceLocation("rtm", "textures/motorman_shishi.png");

	private int textureType;

	public RenderNPC(RenderManager renderManager)
	{
		super(renderManager, new ModelBiped(0.0F, 0.0F, 64, 64), 0.5F);

		this.addLayer(new LayerLight(this));

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if(month == 1 && day >= 1 && day <= 3)
		{
			this.textureType = 1;
		}
		else if(month == 12 && day >= 24 && day <= 26)
		{
			this.textureType = 12;
		}
	}

	@Override
	public void doRender(EntityNPC entity, double x, double y, double z, float par8, float partialTick)
    {
		if(entity.getResourceState().getResourceSet().modelObj == null)
		{
			ItemStack heldItem = entity.getHeldItem();
			boolean hasGun = (heldItem != null && heldItem.getItem() instanceof ItemGun);
			boolean usingGun = hasGun && ((EntityNPC)entity).isUsingItem();
			if(usingGun)
			{
				((ModelBiped)this.mainModel).rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			}

			super.doRender(entity, x, y, z, par8, partialTick);
		}
		else
		{
			this.renderCustomModel(entity, x, y, z, par8, partialTick);
		}
    }

	private void renderCustomModel(EntityNPC entity, double x, double y, double z, float par8, float partialTick)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float)x, (float)y, (float)z);

		ModelSetNPC modelSet = entity.getResourceState().getResourceSet();
		modelSet.modelObj.render(entity, modelSet.getConfig(), 0, partialTick);

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityNPC entity)
	{
		if(entity.isMotorman())
		{
			switch(this.textureType)
			{
			case 1: return TEX_SHISHI;
			case 12: return TEX_SANTA;
			default: return TEXTURE;
			}
		}
		else
		{
			return entity.getResourceState().getResourceSet().texture;
		}
	}

	public class LayerLight implements LayerRenderer<EntityNPC>
	{
		private final RenderNPC parentRenderer;

		public LayerLight(RenderNPC renderer)
		{
			this.parentRenderer = renderer;
		}

		@Override
		public void doRenderLayer(EntityNPC entity, float par2, float par3, float partialTicks, float par5, float par6, float par7, float scale)
		{
			if(entity.isMotorman()){return;}
			ResourceLocation tex = entity.getResourceState().getResourceSet().lightTexture;
			if(tex == null){return;}

			this.parentRenderer.bindTexture(tex);
	        GlStateManager.enableBlend();
	        GlStateManager.disableAlpha();
	        GlStateManager.blendFunc(1, 1);
	        GlStateManager.disableLighting();
	        GlStateManager.depthMask(!entity.isInvisible());
	        int i = 61680;
	        int j = i % 65536;
	        int k = i / 65536;
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
	        GlStateManager.enableLighting();
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        this.parentRenderer.getMainModel().render(entity, par2, par3, par5, par6, par7, scale);
	        this.parentRenderer.setLightmap(entity);
	        GlStateManager.depthMask(true);
	        GlStateManager.disableBlend();
	        GlStateManager.enableAlpha();
		}

		@Override
		public boolean shouldCombineTextures()
		{
			return false;
		}
	}
}