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

package jp.ngt.rtm.gui;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.BiMap;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.ClientProxy;
import jp.ngt.rtm.block.tt.TimeTable.TTEntry;
import jp.ngt.rtm.block.tt.TrainTimeTable;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.gui.camera.Camera;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIngameCustom extends GuiScreen
{
	private static final ResourceLocation TEX_CAB = new ResourceLocation("minecraft", "textures/gui/cab.png");
	private static final ResourceLocation TEX_SCOPE = new ResourceLocation("rtm", "textures/gui/scope.png");
	private static final ResourceLocation TEX_NVD = new ResourceLocation("rtm", "textures/gui/nvd.png");

	private TrainTimeTable timeTable;

	public GuiIngameCustom(Minecraft par1)
	{
		super();
		this.mc = par1;
		this.zLevel = -100.0F;
	}

	public void onRenderGui(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType() == ElementType.HOTBAR)
		{
			if(!this.mc.player.isRiding() || this.mc.gameSettings.thirdPersonView != 0){return;}

			this.setScale(event.getResolution());

			Entity ridingEntity = this.mc.player.getRidingEntity();
			if(ridingEntity instanceof EntityVehicleBase)
			{
				this.renderVehicleGui((EntityVehicleBase)ridingEntity);
			}
			else if(ridingEntity instanceof EntityArtillery)
			{
				this.renderArtilleryGui((EntityArtillery)ridingEntity);
			}

			NGTUtilClient.bindTexture(ICONS);
		}
		else if(event.getType() == ElementType.HELMET)
		{
			byte viewMode = ClientProxy.getViewMode(this.mc.player);
			if(viewMode >= 0)
			{
				int w = event.getResolution().getScaledWidth();
				int h = event.getResolution().getScaledHeight();
				if(viewMode == 3)
				{
					this.renderNVD(w, h);
				}
				else if(viewMode == 1 || viewMode == 2)
				{
					this.renderScope(w, h);
				}
				event.setCanceled(true);
			}

			if(viewMode == 4)
			{
				Camera.INSTANCE.render(this.mc, event,
						event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
			}
			else
			{
				Camera.INSTANCE.off();
			}
		}
	}

	private void renderVehicleGui(EntityVehicleBase vehicle)
	{
		ModelSetVehicleBase modelSet = (ModelSetVehicleBase)vehicle.getResourceState().getResourceSet();
		if(modelSet != null && !modelSet.getConfig().notDisplayCab)
		{
			NGTUtilClient.bindTexture(modelSet.guiTexture != null ? modelSet.guiTexture : TEX_CAB);

			if(modelSet.guiSE != null)
			{
				ScriptUtil.doScriptIgnoreError(modelSet.guiSE, "renderGui", vehicle, this);
			}
			else if(vehicle instanceof EntityTrainBase)
			{
				this.renderDefaultTrainGui((EntityTrainBase)vehicle);
				this.renderTimeTable((EntityTrainBase)vehicle);
			}
		}
	}

	private void renderDefaultTrainGui(EntityTrainBase train)
	{
		int halfW = this.getWidth() / 2;
		this.drawTexturedModalRect(halfW - 208, this.getHeight() - 48, 0, 0, 416, 48);
		this.drawMeterAndLever(train);
		this.drawWatch();

		FontRenderer fontrenderer = this.mc.fontRenderer;
		fontrenderer.drawStringWithShadow(String.valueOf(this.getSpeed(train)), halfW - 138, this.height - 11, 0x00FF00);
		fontrenderer.drawStringWithShadow(String.valueOf(this.getBrake(train)), halfW - 178, this.height - 11, 0x00FF00);
		fontrenderer.drawStringWithShadow(String.valueOf(this.getWorldTime()), halfW + 130, this.height - 40, 0x00FF00);
		fontrenderer.drawStringWithShadow(this.getTime(), halfW + 130, this.height - 30, 0x00FF00);
	}

	private void renderTimeTable(EntityTrainBase train)
	{
		String trainName = train.getResourceState().getName();
		if(this.timeTable == null || !trainName.equals(this.timeTable.train))
		{
			this.timeTable = new TrainTimeTable(trainName);
		}

		if(this.timeTable.colIndex < 0)
		{
			return;
		}

		TTEntry[][] tt = this.timeTable.timeTable.ttData;
		int fontH = 15;
		int x0 = 5;
		int w1 = 50;
		int w2 = 20;
		int w3 = 20;
		int y0 = 5;
		int maxH = y0 + (tt.length + 2) * fontH;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(0xFFFFFF, 0xFF);
		NGTRenderHelper.addQuadGuiFace(x0, y0, x0 + w1 + w2 + w3, maxH, this.zLevel);
		tessellator.draw();

		tessellator.startDrawing(GL11.GL_LINES);
		tessellator.setColorRGBA_I(0x000000, 0xFF);
		NGTRenderHelper.addQuadGuiFrame(x0, y0, x0 + w1 + w2 + w3, maxH, this.zLevel);
		tessellator.addVertex(x0 + w1, y0, this.zLevel);
		tessellator.addVertex(x0 + w1, maxH, this.zLevel);
		tessellator.addVertex(x0 + w1 + w2, y0 + fontH, this.zLevel);
		tessellator.addVertex(x0 + w1 + w2, maxH, this.zLevel);
		for(int i = 0; i < tt.length + 1; ++i)
		{
			int y = y0 + (i + 1) * fontH;
			tessellator.addVertex(x0, y, this.zLevel);
			tessellator.addVertex(x0 + w1 + w2 + w3, y, this.zLevel);
		}
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		FontRenderer renderer = this.mc.fontRenderer;
		int y = 10;
		int x = 10;
		renderer.drawString(this.timeTable.train, x, y, 0x000000);
		renderer.drawString(train.getFormation().entries.length + "両", x + w1, y, 0xFF1000);
		y += fontH;
		int color = 0x808080;
		renderer.drawString("停車場名", x, y, color);
		renderer.drawString("着", x + w1, y, color);
		renderer.drawString("発", x + w1 + w2, y, color);

		BiMap<Integer, String> map = this.timeTable.timeTable.stationAxis.inverse();
		y += fontH;
		color = 0x000000;
		for(int i = 0; i < tt.length; ++i)
		{
			TTEntry entry = tt[i][this.timeTable.colIndex];
			renderer.drawString(map.get(i), x, y + fontH * i, color);
			renderer.drawString(entry.data[1], x + w1, y + fontH * i, color);
			renderer.drawString(entry.data[2], x + w1 + w2, y + fontH * i, color);
		}
	}

	private void renderArtilleryGui(EntityArtillery artillery)
	{
		FontRenderer renderer = this.mc.fontRenderer;
		renderer.drawStringWithShadow("Yaw : " + artillery.getBarrelYaw(), 2, this.height - 40, 16777215);
		renderer.drawStringWithShadow("Pitch : " + -artillery.getBarrelPitch(), 2, this.height - 30, 16777215);
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	public int getSpeed(EntityVehicleBase train)
	{
		return (int)((train.getSpeed() * 72.0F) + 0.5F);
	}

	public int getBrake(EntityTrainBase train)
	{
		return train.brakeCount * 3;
	}

	public int getWorldTime()
	{
		return (int)this.mc.player.world.getWorldTime() % 24000;
	}

	public String getTime()
	{
		int t0 = this.getWorldTime();
		int hour = ((t0 / 1000) + 6) % 24;
		int minute = (int)((float)(t0 % 1000) * 0.06F);
		StringBuilder sb = (new StringBuilder(String.valueOf(hour))).append(":").append(minute);
		return sb.toString();
	}

	private void setScale(ScaledResolution par1)
	{
		this.width = par1.getScaledWidth();
		this.height = par1.getScaledHeight();
	}

	private void drawMeterAndLever(EntityTrainBase train)
	{
		int halfW = this.getWidth() / 2;
		float rMax = 240.0F;
		int startX = halfW - 176;
		int startY = this.height - 29;

		float r1 = rMax * (float)train.brakeAirCount / 2880.0F;
		this.drawMeter(startX, startY, 32, 32, 48, r1, 512);
		float r2 = rMax * (float)this.getBrake(train) / 432.0F;
		this.drawMeter(startX, startY, 32, 0, 48, r2, 512);

		float f3 = (train.getResourceState().getResourceSet().getConfig()).maxSpeed[4];
		float f4 = 270.0F * Math.abs(train.getSpeed()) / f3;
		this.drawMeter(halfW - 136, this.height - 29, 32, 64, 48, f4, 512);

		this.drawLever(train);
	}

	private void drawLever(EntityTrainBase train)
	{
		int halfW = this.width / 2;
		float d = 1.0F / 512.0F;

		int notch = train.getNotch() * 3;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(halfW - 104), (float)(this.height + notch) - 19.5F, 0.0F);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(4.0F, 1.5F, this.zLevel, 8.0F * d, 83.0F * d);
		tessellator.addVertexWithUV(4.0F, -1.5F, this.zLevel, 8.0F * d, 80.0F * d);
		tessellator.addVertexWithUV(-4.0F, -1.5F, this.zLevel, 0.0F, 80.0F * d);
		tessellator.addVertexWithUV(-4.0F, 1.5F, this.zLevel, 0.0F, 83.0F * d);
		tessellator.draw();
		GL11.glPopMatrix();
	}

	private void drawWatch()
	{
		int startX = (this.width / 2) + 112;
		int startY = this.height - 16;
		int t0 = this.getWorldTime();

		int t1 = ((t0 / 1000) + 6) % 12;
		float hour = 360.0F *  (float)t1 / 12.0F;
		this.drawMeter(startX, startY, 32, 96, 48, hour + 135.0F, 512);

		int t2 = (int)((float)(t0 % 1000) * 0.06F);
		float minute = 360.0F *  (float)t2 / 60.0F;
		this.drawMeter(startX, startY, 32, 128, 48, minute + 135.0F, 512);
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h)
	{
		this.drawRectangle(x, y, u, v, w, h, 512);
	}

	public void drawRectangle(int x, int y, int u, int v, int w, int h, int texSize)
	{
		this.drawRectangle(x, y, 0, 0, w, h, u, v, texSize);
	}

	public void drawRectangle(int startX, int startY, int offsetX, int offsetY, int w, int h, int u, int v, int texSize)
	{
		float fu = 1.0F / (float)texSize;
		float fv = 1.0F / (float)texSize;
		int x = startX + offsetX;
		int y = startY + offsetY;
		float z = (float)this.zLevel;
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((float)x,       (float)(y + h), z, ((float)(u + 0) * fu), ((float)(v + h) * fv));
		tessellator.addVertexWithUV((float)(x + w), (float)(y + h), z, ((float)(u + w) * fu), ((float)(v + h) * fv));
		tessellator.addVertexWithUV((float)(x + w), (float)y,       z, ((float)(u + w) * fu), ((float)(v + 0) * fv));
		tessellator.addVertexWithUV((float)x,       (float)y,       z, ((float)(u + 0) * fu), ((float)(v + 0) * fv));
		tessellator.draw();
	}

	public void drawMeter(int startX, int startY, int size, int u, int v, float rotation, int texSize)
	{
		int offset = -(size / 2);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)startX, (float)startY, 0.0F);
		GL11.glRotatef(rotation, 0.0F, 0.0F, 1.0F);
		this.drawRectangle(0, 0, offset, offset, size, size, u, v, texSize);
		GL11.glPopMatrix();
	}

	protected void renderScope(int w, int h)
	{
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		float d0 = (float)(w - h) / 2.0F;
		float d1 = -90.0F;
		NGTUtilClient.bindTexture(TEX_SCOPE);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(d0,            (float)h, d1, 0.0F, 1.0F);
		tessellator.addVertexWithUV(d0 + (float)h, (float)h, d1, 1.0F, 1.0F);
		tessellator.addVertexWithUV(d0 + (float)h, 0.0F,     d1, 1.0F, 0.0F);
		tessellator.addVertexWithUV(d0,            0.0F,     d1, 0.0F, 0.0F);
		tessellator.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(0);
		tessellator.addVertexWithUV(0.0F, (float)h, d1, 0.0F, 1.0F);
		tessellator.addVertexWithUV(d0,   (float)h, d1, 1.0F, 1.0F);
		tessellator.addVertexWithUV(d0,   0.0F,     d1, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0F, 0.0F,     d1, 0.0F, 0.0F);

		tessellator.addVertexWithUV(d0 + (float)h, (float)h, d1, 0.0F, 1.0F);
		tessellator.addVertexWithUV((float)w,      (float)h, d1, 1.0F, 1.0F);
		tessellator.addVertexWithUV((float)w,      0.0F,     d1, 1.0F, 0.0F);
		tessellator.addVertexWithUV(d0 + (float)h, 0.0F,     d1, 0.0F, 0.0F);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	protected void renderNVD(int w, int h)
	{
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		float d1 = -90.0F;

		NGTTessellator tessellator = NGTTessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(0x309030);
		tessellator.addVertexWithUV(0.0F,     (float)h, d1, 0.0F, 1.0F);
		tessellator.addVertexWithUV((float)w, (float)h, d1, 1.0F, 1.0F);
		tessellator.addVertexWithUV((float)w, 0.0F,     d1, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0F,     0.0F,     d1, 0.0F, 0.0F);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		NGTUtilClient.bindTexture(TEX_NVD);
		tessellator.startDrawingQuads();
		float f0 = ((float)h / (float)w) * 0.5F;
		tessellator.addVertexWithUV(0.0F,     (float)h, d1, 0.0F, 0.5F + f0);
		tessellator.addVertexWithUV((float)w, (float)h, d1, 1.0F, 0.5F + f0);
		tessellator.addVertexWithUV((float)w, 0.0F,     d1, 1.0F, 0.5F - f0);
		tessellator.addVertexWithUV(0.0F,     0.0F,     d1, 0.0F, 0.5F - f0);
		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}