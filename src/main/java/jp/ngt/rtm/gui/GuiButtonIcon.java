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