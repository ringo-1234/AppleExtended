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