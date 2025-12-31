package jp.ngt.rtm.gui.vendor;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class VendorScreen
{
	protected GuiTicketVendor vendor;

	public VendorScreen(GuiTicketVendor par1)
	{
		this.vendor = par1;
	}

	public abstract void init(int guiLeft, int guiTop);

	public abstract void onClickButton(GuiButton button);

	public void drawScreen(){}
}