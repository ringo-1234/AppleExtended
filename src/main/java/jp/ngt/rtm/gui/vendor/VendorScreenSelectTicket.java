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

package jp.ngt.rtm.gui.vendor;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.gui.GuiButtonColored;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VendorScreenSelectTicket extends VendorScreen
{
	public VendorScreenSelectTicket(GuiTicketVendor par1)
	{
		super(par1);
	}

	@Override
	public void init(int guiLeft, int guiTop)
	{
		this.vendor.addButtonToGUI(new GuiButtonColored(10, guiLeft + 24, guiTop + 20, 100, 50, "Ticket", 0x00FFFF, 0x000000));
    	this.vendor.addButtonToGUI(new GuiButtonColored(11, guiLeft + 132, guiTop + 20, 100, 50, "Ticket book", 0x50FF30, 0x000000));
	}

	@Override
	public void onClickButton(GuiButton button)
	{
		if(button.id == 10)
		{
			//this.vendor.setVendorScreen(new VendorScreenSelectPrice(this.vendor, "Ticket"));
			this.sendTicket("vendor:ticket");
		}
		else if(button.id == 11)
		{
			//this.vendor.setVendorScreen(new VendorScreenSelectPrice(this.vendor, "TicketBook"));
			this.sendTicket("vendor:ticketbook");
		}
	}

	private void sendTicket(String par1)
	{
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, par1, this.vendor.mc.player));
	}
}