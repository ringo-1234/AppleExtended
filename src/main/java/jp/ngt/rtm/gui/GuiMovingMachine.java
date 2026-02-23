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
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.block.tileentity.TileEntityMovingMachine;
import jp.ngt.rtm.entity.vehicle.VehicleNGTO;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMovingMachine extends GuiScreenCustom
{
	private static final String[] VEHICLE_TYPE = {"Car", "Ship", "Plane"};

	private final TileEntityMovingMachine tileEntity;
	private GuiTextField[] range;
	private GuiTextField[] offset;
	private GuiTextField[] vngtoSetting;
	private GuiTextField speed;
	private GuiButton buttonV;
	private GuiButton buttonType;

	private boolean guideVisibility;
	/**0:MovingMachine, 1:VehicleGenerator*/
	private int blockType;
	private int type;

	public GuiMovingMachine(TileEntityMovingMachine par1)
	{
		this.tileEntity = par1;
		this.guideVisibility = par1.guideVisibility;
		this.blockType = par1.getBlockMetadata();
	}

	@Override
	public void initGui()
    {
		super.initGui();

		int hw = this.width / 2;

		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, hw - 155, this.height - 28, 150, 20, I18n.format("gui.done", new Object[0])));
		this.buttonList.add(new GuiButton(1, hw + 5, this.height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));

		int h = 20;
		this.buttonV = new GuiButton(100, hw - 120, h, 100, 20, "GuideVisibility : " + this.guideVisibility);
		this.buttonList.add(this.buttonV);

		h += 30;
		this.range = new GuiTextField[3];
		this.range[0] = this.setTextField(hw - 120, h, 60, 20, String.valueOf(this.tileEntity.width));h += 24;
		this.range[1] = this.setTextField(hw - 120, h, 60, 20, String.valueOf(this.tileEntity.height));h += 24;
		this.range[2] = this.setTextField(hw - 120, h, 60, 20, String.valueOf(this.tileEntity.depth));h += 24;

		h = 20;
		int w = hw + 70;
		if(this.blockType == 0)
		{
			this.speed = this.setTextField(w - 30, h, 60, 20, String.valueOf(this.tileEntity.speed));
		}
		else
		{
			this.buttonType = this.addButton(new GuiButton(101, w, h, 100, 20, VEHICLE_TYPE[this.tileEntity.vngto.type]));h += 24;
			this.type = this.tileEntity.vngto.type;

			this.offset = new GuiTextField[3];
			this.offset[0] = this.setTextField(w, h, 60, 20, String.valueOf(this.tileEntity.offsetX));h += 24;
			this.offset[1] = this.setTextField(w, h, 60, 20, String.valueOf(this.tileEntity.offsetY));h += 24;
			this.offset[2] = this.setTextField(w, h, 60, 20, String.valueOf(this.tileEntity.offsetZ));h += 24;

			this.vngtoSetting = new GuiTextField[4];
			float f1 = this.tileEntity.vngto.offsetX - this.tileEntity.offsetX - 0.5F;
			float f2 = this.tileEntity.vngto.offsetY - this.tileEntity.offsetY - 0.5F;
			float f3 = this.tileEntity.vngto.offsetZ - this.tileEntity.offsetZ - 0.5F;
			this.vngtoSetting[0] = this.setTextField(w, h, 60, 20, String.valueOf(this.tileEntity.vngto.scale));h += 24;
			this.vngtoSetting[1] = this.setTextField(w, h, 60, 20, String.valueOf(f1));h += 24;
			this.vngtoSetting[2] = this.setTextField(w, h, 60, 20, String.valueOf(f2));h += 24;
			this.vngtoSetting[3] = this.setTextField(w, h, 60, 20, String.valueOf(f3));h += 24;
		}
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
			this.guideVisibility ^= true;
			this.buttonV.displayString = "GuideVisibility : " + this.guideVisibility;
        }
		else if(button.id == 101)
        {
			this.type = (this.type + 1) % VEHICLE_TYPE.length;
			this.buttonType.displayString = VEHICLE_TYPE[this.type];
        }

		super.actionPerformed(button);
    }

	private void sendPacket()
	{
		this.tileEntity.width = NGTMath.getIntFromString(this.range[0].getText(), 0, Integer.MAX_VALUE, 0);
		this.tileEntity.height = NGTMath.getIntFromString(this.range[1].getText(), 0, Integer.MAX_VALUE, 0);
		this.tileEntity.depth = NGTMath.getIntFromString(this.range[2].getText(), 0, Integer.MAX_VALUE, 0);

		if(this.blockType == 0)
		{
			this.tileEntity.speed = NGTMath.getFloatFromString(this.speed.getText(), 0, Integer.MAX_VALUE, 0);

		}
		else if(this.blockType == 1)
		{
			this.tileEntity.offsetX = NGTMath.getIntFromString(this.offset[0].getText(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			this.tileEntity.offsetY = NGTMath.getIntFromString(this.offset[1].getText(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			this.tileEntity.offsetZ = NGTMath.getIntFromString(this.offset[2].getText(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);

			float f0 = NGTMath.getFloatFromString(this.vngtoSetting[0].getText(), 0.0F, Float.MAX_VALUE, 0.0F);
			float f1 = NGTMath.getFloatFromString(this.vngtoSetting[1].getText(), Float.MIN_VALUE, Float.MAX_VALUE, 0.0F);
			float f2 = NGTMath.getFloatFromString(this.vngtoSetting[2].getText(), Float.MIN_VALUE, Float.MAX_VALUE, 0.0F);
			float f3 = NGTMath.getFloatFromString(this.vngtoSetting[3].getText(), Float.MIN_VALUE, Float.MAX_VALUE, 0.0F);
			float ox = f1 + this.tileEntity.offsetX + 0.5F;
			float oy = f2 + this.tileEntity.offsetY + 0.5F;
			float oz = f3 + this.tileEntity.offsetZ + 0.5F;

			this.tileEntity.vngto = new VehicleNGTO(null, ox, oy, oz, f0);
			this.tileEntity.vngto.riderPosX = f1;
			this.tileEntity.vngto.riderPosY = f2;
			this.tileEntity.vngto.riderPosZ = f3;
			this.tileEntity.vngto.type = this.type;
		}

		this.tileEntity.guideVisibility = this.guideVisibility;

		PacketNBT.sendToServer(this.tileEntity);
		/*RTMCore.NETWORK_WRAPPER.sendToServer(
				new PacketMovingMachine(this.tileEntity, w, h, d, x, y, z, s, this.guideVisibility));*/
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        int w2 = (this.width / 2) + 10;
        int h = 56;
        this.drawString(this.fontRenderer, "Width", w2 - 160, h, 0xFFFFFF);h += 24;
        this.drawString(this.fontRenderer, "Height", w2 - 160, h, 0xFFFFFF);h += 24;
        this.drawString(this.fontRenderer, "Depth", w2 - 160, h, 0xFFFFFF);h += 24;

        h = 26;
        if(this.blockType == 0)
		{
        	this.drawString(this.fontRenderer, "Speed", w2, h, 0xFFFFFF);
		}
        else
        {
        	h += 24;
        	this.drawString(this.fontRenderer, "OffsetX", w2, h, 0xFFFFFF);h += 24;
            this.drawString(this.fontRenderer, "OffsetY", w2, h, 0xFFFFFF);h += 24;
            this.drawString(this.fontRenderer, "OffsetZ", w2, h, 0xFFFFFF);h += 24;
            this.drawString(this.fontRenderer, "Scale", w2, h, 0xFFFFFF);h += 24;
            this.drawString(this.fontRenderer, "VehicleOffsetX", w2, h, 0xFFFFFF);h += 24;
            this.drawString(this.fontRenderer, "VehicleOffsetY", w2, h, 0xFFFFFF);h += 24;
            this.drawString(this.fontRenderer, "VehicleOffsetZ", w2, h, 0xFFFFFF);h += 24;
        }
    }
}