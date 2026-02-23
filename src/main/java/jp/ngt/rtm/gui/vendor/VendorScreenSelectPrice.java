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

import jp.ngt.rtm.gui.GuiButtonColored;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**切符の値段選択*/
@SideOnly(Side.CLIENT)
public class VendorScreenSelectPrice extends VendorScreen
{
	private final String options;

	public VendorScreenSelectPrice(GuiTicketVendor par1, String options)
	{
		super(par1);
		this.options = options;
	}

	@Override
	public void init(int guiLeft, int guiTop)
	{
		int sizeX = 40;
		int sizeY = 20;

		for(int i = 0; i < 4; ++i)//Y
		{
			for(int j = 0; j < 5; ++j)//X
			{
				int id = i * 5 + j;
				int x = guiLeft + 10 + j * (sizeX + 5);
				int y = guiTop + 10 + i * (sizeY + 5);
				String s = String.valueOf(160 + id * 60);
				this.vendor.addButtonToGUI(new GuiButtonColored(id, x, y, sizeX, sizeY, s, 0x00FFFF, 0x000000));
			}
		}
	}

	@Override
	public void onClickButton(GuiButton button)
	{
		if(button.id >= 0 && button.id < 20)
		{
			;
		}
	}
}