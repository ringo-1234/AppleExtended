package jp.ngt.ngtlib.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonCustom extends GuiButton
{
	private final GuiScreen screen;
	private final List<String> tips = new ArrayList<>();

	public GuiButtonCustom(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, GuiScreen pScr)
	{
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.screen = pScr;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float ptick)
    {
		super.drawButton(mc, mouseX, mouseY, ptick);

		if(this.hovered && !this.tips.isEmpty())
		{
			GuiScreenCustom.drawHoveringTextS(this.tips, mouseX, mouseY, this.screen);
		}
    }

	public GuiButtonCustom addTips(String par1)
	{
		if(par1 != null){this.tips.add(par1);}
		return this;
	}
}