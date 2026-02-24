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

package jp.ngt.rtm.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.rtm.gui.GuiButtonIcon.IconElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectIcon extends GuiScreenCustom
{
	private static final int FIRST_BUTTON_ID = 1000;

	private final GuiDecorationBlock parent;
	private final List<GuiButtonIcon> iconButtons = new ArrayList<>();
	private final List<IconElement> icons;
	private final int iconSize;

	public GuiSelectIcon(GuiDecorationBlock par1, List<IconElement> par2, int par3)
	{
		super();
		this.parent = par1;
		this.icons = par2;
		this.iconSize = par3;
	}

	@Override
	public void initGui()
    {
		super.initGui();

		this.buttonList.clear();
		this.iconButtons.clear();

		//iconソート必要
		//スクロール上下限設定
		int w = this.width / this.iconSize;
		int h = this.height / this.iconSize;
		for(int index = 0; index < this.icons.size(); ++index)
		{
			int x = index % w;
			int y = index / w;
			IconElement icon = this.icons.get(index);
			GuiButtonIcon button = new GuiButtonIcon(FIRST_BUTTON_ID + index, x * this.iconSize, y * this.iconSize, this.iconSize, this.iconSize, icon);
			this.buttonList.add(button);
			this.iconButtons.add(button);
		}
    }

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id >= FIRST_BUTTON_ID)
		{
			if(button instanceof GuiButtonIcon)
			{
				((GuiButtonIcon)button).icon.onClick();
				this.closeGui();
				return;
			}
		}
    }

	private void closeGui()
	{
		this.mc.displayGuiScreen(this.parent);
	}

	@Override
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int scroll = Mouse.getEventDWheel();
        if(scroll != 0)
        {
        	for(GuiButtonIcon button : this.iconButtons)
            {
            	button.moveButton(scroll);
            }
        }
    }

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
    {
		if(par2 == Keyboard.KEY_ESCAPE)
        {
            this.closeGui();
            return;
        }

    	super.keyTyped(par1, par2);
    }

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}