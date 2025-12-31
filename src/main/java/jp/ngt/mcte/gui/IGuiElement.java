package jp.ngt.mcte.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiElement
{
	/**buttonListへの登録など行う*/
	void init(GuiScreenCustom gui);

	void setYPos(int y);
}