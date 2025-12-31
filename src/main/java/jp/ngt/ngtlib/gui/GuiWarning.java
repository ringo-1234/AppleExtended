package jp.ngt.ngtlib.gui;

import jp.ngt.ngtlib.util.NGTCertificate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiWarning extends GuiScreen
{
	//private static final ResourceLocation texture = new ResourceLocation("rtm", "textures/gui/cab.png");
	private int counter;
	private boolean field_01;

	public GuiWarning(Minecraft par1)
    {
		super();
        this.mc = par1;
    }

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event)
	{
		if(event.isCancelable() || event.getType() != ElementType.EXPERIENCE)
	    {
			return;
	    }

		if(!NGTCertificate.canUse())
		{
			if(this.counter < 10)
			{
				++this.counter;
			}
			else
			{
				this.counter = 0;
				this.field_01 = !this.field_01;
			}

			this.setScale(event.getResolution());
			GL11.glPushMatrix();
			GL11.glScalef(2.5F, 2.5F, 2.5F);
			int i0 = this.field_01 ? 0xFF0000 : 0xFFFF00;
			this.mc.fontRenderer.drawStringWithShadow(I18n.format("gui.warning", new Object[]{}), 2, 5, i0);
			GL11.glPopMatrix();
		}
	}

	private void setScale(ScaledResolution par1)
	{
		this.width = par1.getScaledWidth();
		this.height = par1.getScaledHeight();
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
		float f = 1.0F / 512.0F;
        float f1 = 1.0F / 512.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        worldrenderer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
}