package jp.ngt.rtm.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.ColorUtil;
import jp.ngt.rtm.item.PaintProperty;
import jp.ngt.rtm.item.PaintProperty.EnumPaintType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPaintTool extends GuiScreenCustom
{
	private static ResourceLocation texture = new ResourceLocation("rtm", "textures/gui/paint_tool.png");

	private final EntityPlayer player;
	private final ItemStack paintItem;
	private PaintProperty prop;
	private int color;
	private int paintTypeIndex;

	private GuiTextField colorR;
	private GuiTextField colorG;
	private GuiTextField colorB;
	private GuiTextField colorHex;
	private GuiTextField alpha;
	private GuiTextField radius;
	private GuiButton type;

	public GuiPaintTool(EntityPlayer par1)
	{
		this.player = par1;
		this.paintItem = par1.inventory.getCurrentItem();
		this.prop = PaintProperty.getProperty(this.paintItem);
	}

	@Override
	public void initGui()
    {
		super.initGui();

		this.paintTypeIndex = this.prop.type;
		int halfW = this.width / 2;

		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, halfW - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(1, halfW + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        this.type = new GuiButtonPenType(100, halfW + 40, 20);
        this.buttonList.add(this.type);

        this.textFields.clear();
        this.colorR = this.setTextField(halfW - 80, 20, 60, 20, "");
        this.colorG = this.setTextField(halfW - 80, 40, 60, 20, "");
        this.colorB = this.setTextField(halfW - 80, 60, 60, 20, "");
        this.colorHex = this.setTextField(halfW - 80, 85, 60, 20, "");
        this.alpha = this.setTextField(halfW - 80, 110, 60, 20, String.valueOf(this.prop.alpha));
        this.radius = this.setTextField(halfW + 28, 60, 40, 20, String.valueOf(this.prop.radius));

        this.setColor(this.prop.color, 1 + 2 + 4 + 8);
    }

	/**@param flag hex, b, g, r*/
	private void setColor(int c, int flag)
	{
		this.color = c;
		this.prop.color = c;

		if((flag & 1) != 0)
		{
			this.colorR.setText(String.valueOf(ColorUtil.getR(this.color)));
		}

		if((flag & (1 << 1)) != 0)
		{
			this.colorG.setText(String.valueOf(ColorUtil.getG(this.color)));
		}

		if((flag & (1 << 2)) != 0)
		{
			this.colorB.setText(String.valueOf(ColorUtil.getB(this.color)));
		}

		if((flag & (1 << 3)) != 0)
		{
			this.colorHex.setText(ColorUtil.toString(this.color));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id == 0)
        {
			this.sendPacket();
            this.mc.displayGuiScreen(null);
        }
		else if(button.id == 1)
        {
            this.mc.displayGuiScreen(null);
        }
		else if(button.id == 100)
        {
			++this.paintTypeIndex;
			if(this.paintTypeIndex >= EnumPaintType.values().length)
			{
				this.paintTypeIndex = 0;
			}
			this.prop.type = this.paintTypeIndex;
        }
    }

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
    {
		super.keyTyped(par1, par2);

		if(this.currentTextField != null)
		{
			try
			{
				if(this.currentTextField == this.colorR)
				{
					int r = Integer.valueOf(this.colorR.getText());
					int c = ColorUtil.encode(r, ColorUtil.getG(this.color), ColorUtil.getB(this.color));
					this.setColor(c, 2 + 4 + 8);
				}
				else if(this.currentTextField == this.colorG)
				{
					int g = Integer.valueOf(this.colorG.getText());
					int c = ColorUtil.encode(ColorUtil.getR(this.color), g, ColorUtil.getB(this.color));
					this.setColor(c, 1 + 4 + 8);
				}
				else if(this.currentTextField == this.colorB)
				{
					int b = Integer.valueOf(this.colorB.getText());
					int c = ColorUtil.encode(ColorUtil.getR(this.color), ColorUtil.getG(this.color), b);
					this.setColor(c, 1 + 2 + 8);
				}
				else if(this.currentTextField == this.colorHex)
				{
					int c = ColorUtil.toInteger(this.colorHex.getText());
					this.setColor(c, 1 + 2 + 4);
				}
				else if(this.currentTextField == this.alpha)
				{
					int a = Integer.valueOf(this.alpha.getText());
					this.prop.alpha = a;
				}
				else if(this.currentTextField == this.radius)
				{
					int r = Integer.valueOf(this.radius.getText());
					this.prop.radius = r;
				}
			}
			catch(NumberFormatException e)
			{
				//NGTLog.debug("GuiPaintTool_NFE");
			}
		}
    }

	private void sendPacket()
	{
		this.prop.setProperty(this.paintItem);
		PacketNBT.sendToServer(this.player, this.paintItem);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        int halfW = this.width / 2;
        this.drawCenteredString(this.fontRenderer, "R" , halfW - 90, 25, 0xFF0000);
        this.drawCenteredString(this.fontRenderer, "G" , halfW - 90, 45, 0x00FF00);
        this.drawCenteredString(this.fontRenderer, "B" , halfW - 90, 65, 0x0000FF);
        this.drawCenteredString(this.fontRenderer, "Hex" , halfW - 90, 90, 0xffffff);
        this.drawCenteredString(this.fontRenderer, "Alpha" , halfW - 95, 115, 0xffffff);
        this.drawCenteredString(this.fontRenderer, "Radius" , halfW + 12, 65, 0xffffff);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(this.color, 0xFF);
        tessellator.addVertex(halfW + 8, 20, this.zLevel);
        tessellator.addVertex(halfW + 8, 52, this.zLevel);
        tessellator.addVertex(halfW + 40, 52, this.zLevel);
        tessellator.addVertex(halfW + 40, 20, this.zLevel);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

	private class GuiButtonPenType extends GuiButton
	{
		public GuiButtonPenType(int id, int xPos, int yPos)
		{
			super(id, xPos, yPos, 32, 32, "");
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y, float ptick)
	    {
	        if(this.visible)
	        {
	        	EnumPaintType pType = EnumPaintType.values()[GuiPaintTool.this.paintTypeIndex];

	            mc.getTextureManager().bindTexture(texture);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
	            int k = this.getHoverState(this.hovered);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            this.drawTexturedModalRect(this.x, this.y, pType.iconU, pType.iconV, this.width, this.height);
	            this.mouseDragged(mc, x, y);
	        }
	    }
	}
}