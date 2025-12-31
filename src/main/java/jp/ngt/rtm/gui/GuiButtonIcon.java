package jp.ngt.rtm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonIcon extends GuiButton
{
	public final IconElement icon;

	public GuiButtonIcon(int id, int xPos, int yPos, int w, int h, IconElement par6)
	{
		super(id, xPos, yPos, w, h, "");
		this.icon = par6;
	}

	@Override
	public void drawButton(Minecraft mc, int par2, int par3, float ptick)
    {
		this.icon.draw(this, mc, par2, par3);
    }

	public void moveButton(int moveY)
	{
		this.y += moveY;
	}

	public float getZLevel()
	{
		return this.zLevel;
	}

	public static interface IconElement
	{
		void draw(GuiButtonIcon button, Minecraft mc, int par2, int par3);

		void onClick();
	}
}