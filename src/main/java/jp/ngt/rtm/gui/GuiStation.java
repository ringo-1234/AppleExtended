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

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.block.tileentity.TileEntityStation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStation extends GuiScreenCustom
{
	private final TileEntityStation tileEntity;
	private GuiTextField stationName;

	public GuiStation(TileEntityStation par1)
	{
		this.tileEntity = par1;
	}

	@Override
	public void initGui()
    {
		super.initGui();

		int hw = this.width / 2;

		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, hw - 155, this.height - 28, 150, 20, I18n.format("gui.done", new Object[0])));
		this.buttonList.add(new GuiButton(1, hw + 5, this.height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));

		this.stationName = this.setTextField(hw - 10, 80, 100, 20, this.tileEntity.getName());
    }

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id == 0)
        {
            this.tileEntity.setName(this.stationName.getText());
            PacketNBT.sendToServer(this.tileEntity);

            this.mc.displayGuiScreen(null);
        }
		else if(button.id == 1)
        {
            this.mc.displayGuiScreen(null);
        }

		super.actionPerformed(button);
    }

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        int hw = this.width / 2;
        this.drawString(this.fontRenderer, "Station name" , hw - 70, 86, 0xFFFFFF);
    }
}