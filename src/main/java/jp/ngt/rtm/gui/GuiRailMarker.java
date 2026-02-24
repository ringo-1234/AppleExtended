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

import java.io.File;
import java.util.List;

import org.lwjgl.input.Keyboard;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.network.PacketMarkerRPClient;
import jp.ngt.rtm.rail.TileEntityMarker;
import jp.ngt.rtm.rail.util.RailPosition;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRailMarker extends GuiScreenCustom
{
	public final TileEntityMarker marker;
	private RailPosition currentRP;

	private GuiButton buttonViewMode;
	private GuiTextFieldCustom fieldAncYaw;
	private GuiTextFieldCustom fieldAncPitch;
	private GuiTextFieldCustom fieldAncH;
	private GuiTextFieldCustom fieldAncV;
	private GuiTextFieldCustom fieldCantCenter;
	private GuiTextFieldCustom fieldCantEdge;
	private GuiTextFieldCustom fieldCantRandom;

	private GuiButton buttonScript;
	private GuiTextFieldCustom fieldArgs;

	public GuiRailMarker(TileEntityMarker par1)
	{
		this.marker = par1;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.currentRP = this.marker.getMarkerRP();
		String script = "";
		String args = "";
		if(this.currentRP.hasScript())
		{
			script = this.currentRP.scriptName;
			args = this.currentRP.scriptArgs;
		}

		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 105, this.height - 28, 100, 20, I18n.format("gui.done", new Object[0])));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 100, 20, I18n.format("gui.cancel", new Object[0])));
		this.buttonScript = new GuiButton(100, this.width - 140, 140, 110, 20, script);
		this.buttonList.add(this.buttonScript);
		this.buttonList.add(new GuiButton(101, this.width - 30, 140, 20, 20, "X"));

		this.fieldAncYaw     = this.setFloatField(this.width - 140, 20, 60, 20, String.valueOf(this.currentRP.anchorYaw));
		this.fieldAncPitch   = this.setFloatField(this.width - 140, 50, 60, 20, String.valueOf(this.currentRP.anchorPitch));
		this.fieldAncH       = this.setFloatField(this.width - 140, 80, 60, 20, String.valueOf(this.currentRP.anchorLengthHorizontal));
		this.fieldAncV       = this.setFloatField(this.width - 140, 110, 60, 20, String.valueOf(this.currentRP.anchorLengthVertical));
		this.fieldCantCenter = this.setFloatField(this.width - 70, 20, 60, 20, String.valueOf(this.currentRP.cantCenter));
		this.fieldCantEdge   = this.setFloatField(this.width - 70, 50, 60, 20, String.valueOf(this.currentRP.cantEdge));
		this.fieldCantRandom = this.setFloatField(this.width - 70, 80, 60, 20, String.valueOf(this.currentRP.cantRandom));
		this.fieldArgs       = this.setFloatField(this.width - 140, 170, 130, 20, args);

		if(this.marker.getBlockType() == RTMBlock.markerSwitch)
		{
			this.fieldAncPitch.setEnabled(false);
			this.fieldAncV.setEnabled(false);
			this.fieldCantCenter.setEnabled(false);
			this.fieldCantEdge.setEnabled(false);
		}
		else if(!this.marker.isCoreMarker())
		{
			this.fieldCantCenter.setEnabled(false);
			this.fieldCantRandom.setEnabled(false);
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		super.drawScreen(par1, par2, par3);

		this.renderLineAndAnchor();
		this.drawCenteredString(this.fontRenderer, "Anchor Yaw",      this.width - 110, 10, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Anchor Pitch",    this.width - 110, 40, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Anchor Length H", this.width - 110, 70, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Anchor Length V", this.width - 110, 100, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Cant Center",     this.width - 40, 10, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Cant Edge",       this.width - 40, 40, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Cant Random",     this.width - 40, 70, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Line Script",     this.width - 75, 130, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "Line Script Args",this.width - 75, 160, 0xFFFFFF);
	}

	private void renderLineAndAnchor()
	{
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			this.mc.displayGuiScreen(null);
			this.sendPacket();
		}
		else if(button.id == 1)
		{
			this.mc.displayGuiScreen(null);
		}
		else if(button.id == 100)
		{
			List<File> fileList = NGTFileLoader.findFile((file)->{
				String path = file.getAbsolutePath();
				String name = file.getName();
				return !path.contains("block") && !path.contains("item") && name.startsWith("Line") && name.endsWith(".js");
			});
		}
		else if(button.id == 101)
		{
			this.buttonScript.displayString = "";
		}

		super.actionPerformed(button);
	}

	private void updateValues()
	{
		this.currentRP.anchorYaw = this.getFieldValue(this.fieldAncYaw, this.currentRP.anchorYaw);
		this.currentRP.anchorPitch = this.getFieldValue(this.fieldAncPitch, this.currentRP.anchorPitch);
		this.currentRP.anchorLengthHorizontal = this.getFieldValue(this.fieldAncH, this.currentRP.anchorLengthHorizontal);
		this.currentRP.anchorLengthVertical = this.getFieldValue(this.fieldAncV, this.currentRP.anchorLengthVertical);
		this.currentRP.cantCenter = this.getFieldValue(this.fieldCantCenter, this.currentRP.cantCenter);
		this.currentRP.cantEdge = this.getFieldValue(this.fieldCantEdge, this.currentRP.cantEdge);
		this.currentRP.cantRandom = this.getFieldValue(this.fieldCantRandom, this.currentRP.cantRandom);
		this.currentRP.scriptName = this.buttonScript.displayString;
		this.currentRP.scriptArgs = this.fieldArgs.getText();
	}

	private float getFieldValue(GuiTextFieldCustom field, float defaultVal)
	{
		try
		{
			return Float.valueOf(field.getText());
		}
		catch (NumberFormatException e)
		{
			return defaultVal;
		}
	}

	private void sendPacket()
	{
		this.updateValues();
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketMarkerRPClient(this.marker));
	}
}