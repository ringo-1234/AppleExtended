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

package jp.ngt.rtm.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.renderer.model.ModelLoader;
import jp.ngt.ngtlib.renderer.model.PolygonModel;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import jp.ngt.rtm.item.ItemGun;
import jp.ngt.rtm.item.ItemGun.GunType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBullet extends Render<EntityBullet>
{
	private final PolygonModel cannon = ModelLoader.loadModel(new ResourceLocation("rtm", "models/cannonball.obj"), VecAccuracy.MEDIUM);
	private final PolygonModel rocket = ModelLoader.loadModel(new ResourceLocation("rtm", "models/200mm_rocket.mqo"), VecAccuracy.MEDIUM);
	private static PolygonModel razerPart0;
	private static PolygonModel razerPart1;
	private static final ResourceLocation TEX_CANNON = new ResourceLocation("rtm", "textures/cannonball.png");
	private static final ResourceLocation TEX_ROCKET = new ResourceLocation("rtm", "textures/200mm_rocket.png");
	private static final ResourceLocation TEX_FLASH = new ResourceLocation("rtm", "textures/effect/muzzle_flash.png");

	public RenderBullet(RenderManager renderManager)
	{
		super(renderManager);
		razerPart0 = ModelLoader.loadModel(new ResourceLocation("rtm", "models/razer_part0.mqo"), VecAccuracy.MEDIUM);
		razerPart1 = ModelLoader.loadModel(new ResourceLocation("rtm", "models/razer_part1.mqo"), VecAccuracy.MEDIUM);
	}

	private final void renderBullet(EntityBullet entity, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);

        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);

        BulletType type = entity.getBulletType();

        int pass = MinecraftForgeClient.getRenderPass();
        if(pass == 0)
        {
        	GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);

        	boolean brightBullet = ((type == BulletType.rifle_5_56mm || type == BulletType.rifle_7_62mm || type == BulletType.rifle_12_7mm)
            		&& entity.getCanBreakBlock());

            if(brightBullet)
            {
            	GL11.glDisable(GL11.GL_TEXTURE_2D);
            	if(type == BulletType.rifle_12_7mm)
            	{
            		GL11.glScalef(0.1F, 1.5F, 0.1F);
            	}
            	else
            	{
            		GL11.glScalef(0.05F, 0.75F, 0.05F);
            	}

            	GLHelper.disableLighting();
            	GLHelper.setLightmapMaxBrightness();
    	        GL11.glColor4f(1.0F, 1.0F, 0.25F, 1.0F);
            }
            else if(type == BulletType.handgun_9mm || type == BulletType.rifle_5_56mm || type == BulletType.rifle_7_62mm)
            {
            	this.bindTexture(TEX_CANNON);
            	GL11.glScalef(0.05F, 0.05F, 0.05F);
            }
            else if(type == BulletType.rifle_12_7mm)
            {
            	this.bindTexture(TEX_CANNON);
            	GL11.glScalef(0.1F, 0.1F, 0.1F);
            }
            else if(type == BulletType.cannon_40cm || type == BulletType.cannon_Atomic)
            {
            	this.bindTexture(TEX_CANNON);
            }
            else if(type == BulletType.rocket)
            {
            	this.bindTexture(TEX_ROCKET);
            }

            if(type == BulletType.rocket)
            {
            	this.rocket.renderAll(false);
            }
            else
            {
            	this.cannon.renderAll(false);
            }

            if(brightBullet)
            {
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	GLHelper.enableLighting();
            	GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
        }
        else if(pass == 1)
        {
        	if(type == BulletType.rocket)
            {
        		GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
                NGTRenderer.renderFire(0.5F, 3.0F, 0xFFAA00, 25);

                GL11.glDisable(GL11.GL_BLEND);
            }
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

	public static void onPlayerRender(EntityPlayer player, boolean firstPersonView)
	{
		int useCount = player.getItemInUseCount();
		ItemStack stack = player.inventory.getCurrentItem();
		onShooterRender(player, 0.0D, 0.0D, 0.0D, stack, useCount, firstPersonView);
	}

	public static void onNPCRender(EntityNPC npc, double x, double y, double z)
	{
		int useCount = npc.getItemUseCount();
		ItemStack stack = npc.getHeldItem();
		onShooterRender(npc, x, y, z, stack, useCount, false);
	}

	private static void onShooterRender(EntityLivingBase entity, double x, double y, double z, ItemStack stack, int useCount, boolean firstPersonView)
	{
		if(shouldRenderMuzzleFlash(stack, useCount))
		{
			GunType gunType = ((ItemGun)stack.getItem()).gunType;
			if(gunType == GunType.razer_gun)
			{
				float size = ((float)useCount / (float)gunType.useDuration);//useCountは使用ごとに減っていく
				renderRazer(entity, x, y, z, size);
			}
			else
			{
				renderMuzzleFlash(entity, x, y, z, firstPersonView);
			}
		}
	}

	private static boolean shouldRenderMuzzleFlash(ItemStack stack, int useCount)
	{
		if(stack != null && stack.getItem() instanceof ItemGun)
		{
			GunType gunType = ((ItemGun)stack.getItem()).gunType;
			if(gunType == GunType.razer_gun)
			{
				if(useCount > 0)
				{
					return true;
				}
			}
			else
			{
				if(useCount % ItemGun.INTERVAL > 0)
				{
					if(gunType.rapidFire || useCount == gunType.useDuration - 1)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	//RenderFish参考に位置調整必要
	private static void renderMuzzleFlash(EntityLivingBase entity, double x, double y, double z, boolean firstPersonView)
	{
		GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        setupPosAndRotation(entity);

        GL11.glDisable(GL11.GL_CULL_FACE);
		GLHelper.disableLighting();
		GLHelper.setLightmapMaxBrightness();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        NGTUtilClient.bindTexture(TEX_FLASH);
        float flashR = 0.75F;
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-flashR, flashR,  0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(-flashR, -flashR, 0.0F, 1.0F, 1.0F);
        tessellator.addVertexWithUV(flashR, -flashR,  0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(flashR, flashR,   0.0F, 0.0F, 0.0F);
        tessellator.draw();

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLHelper.enableLighting();

		GL11.glPopMatrix();
	}

	private static void renderRazer(EntityLivingBase entity, double x, double y, double z, float size)
	{
		GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        setupPosAndRotation(entity);

        GLHelper.disableLighting();
		GLHelper.setLightmapMaxBrightness();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        NGTTessellator tessellator = NGTTessellator.instance;
        int i0 = 25;
        for(int i = 0; i < i0; ++i)
        {
        	float f0 = (float)(i + 1) / (float)i0;//径0.1~1.0
        	float f1 = 1.0F - (1.0F - f0) * 0.1F;
        	GL11.glScalef(f1 * size, f1 * size, 1.0F);
        	//GL11.glColor4f(f0, f0, 1.0F, 1.0F - f0);//B固定,R&G変化
        	tessellator.startDrawing(GL11.GL_TRIANGLES);
        	tessellator.setColorRGBA_F(f0, f0, 1.0F, 0.125F);

        	tessellator.addTranslation(0.0F, 0.0F, 127.5F);
            for(int j = 0; j < 128; ++j)
            {
            	razerPart1.tessellateAll(tessellator, true);
            	tessellator.addTranslation(0.0F, 0.0F, -1.0F);
            }
            tessellator.resetTranslation();
            razerPart0.tessellateAll(tessellator, true);
            tessellator.draw();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLHelper.enableLighting();

		GL11.glPopMatrix();
	}

	private static void setupPosAndRotation(EntityLivingBase entity)
	{
        if(entity == NGTUtilClient.getMinecraft().player && NGTUtilClient.getMinecraft().getRenderManager().options.thirdPersonView == 0)
        {
        	//FP視点
        	GL11.glTranslatef(0.2F, 0.05F, -1.0F);
        	GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        }
        else
        {
        	GL11.glRotatef(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        	//GL11.glTranslatef(0.0F, 1.25F - (float)entity.getYOffset(), 0.0F);
    		GL11.glTranslatef(0.0F, entity.getEyeHeight(), 0.0F);
            GL11.glRotatef(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
        	GL11.glTranslatef(-0.36F, -0.05F, 0.4F);
        }
	}

	//RenderFishより
	@Deprecated
	private static void setupPosAndRotation2(EntityLivingBase entity)
	{
		RenderManager renMg = NGTUtilClient.getMinecraft().getRenderManager();
		float partialTicks = 0.0F;

        //GlStateManager.rotate(180.0F - renMg.playerViewY, 0.0F, 1.0F, 0.0F);
        //GlStateManager.rotate((float)(renMg.options.thirdPersonView == 2 ? -1 : 1) * -renMg.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(renMg.options.thirdPersonView == 2 ? -1 : 1) * -entity.rotationPitch, 1.0F, 0.0F, 0.0F);

		int k = entity.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
        float f7 = entity.getSwingProgress(partialTicks);
        float f8 = MathHelper.sin((float)NGTMath.firstSqrt(f7) * (float)Math.PI);
        float f9 = (entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks) * 0.017453292F;
        double d0 = (double)MathHelper.sin(f9);
        double d1 = (double)MathHelper.cos(f9);
        double d2 = (double)k * 0.35D;
        double d3 = 0.8D;
        double d4;
        double d5;
        double d6;
        double d7;

        if ((renMg.options == null || renMg.options.thirdPersonView <= 0) && entity == Minecraft.getMinecraft().player)
        {
            Vec3 vec3d = PooledVec3.create((double)k * -0.36D, -0.05D, 0.4D);
            vec3d = vec3d.rotateAroundX(-(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks));
            vec3d = vec3d.rotateAroundY(-(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks));
            vec3d = vec3d.rotateAroundY(f8 * 0.5F);
            vec3d = vec3d.rotateAroundX(-f8 * 0.7F);
            d4 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks + vec3d.getX();
            d5 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + vec3d.getY();
            d6 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks + vec3d.getZ();
            d7 = (double)entity.getEyeHeight();
        }
        else
        {
            d4 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks - d1 * d2 - d0 * 0.8D;
            d5 = entity.prevPosY + (double)entity.getEyeHeight() + (entity.posY - entity.prevPosY) * (double)partialTicks - 0.45D;
            d6 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks - d0 * d2 + d1 * 0.8D;
            d7 = entity.isSneaking() ? -0.1875D : 0.0D;
        }

        double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + 0.25D;
        double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        float pX = (float)(d4 - d13);
        float pY = (float)(d5 - d8 + d7);
        float pZ = (float)(d6 - d9);
        GL11.glTranslatef(pX, pY, pZ);
	}

	@Override
    protected ResourceLocation getEntityTexture(EntityBullet par1)
    {
        return null;
    }

	@Override
    public void doRender(EntityBullet par1, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBullet(par1, par2, par4, par6, par8, par9);
    }
}