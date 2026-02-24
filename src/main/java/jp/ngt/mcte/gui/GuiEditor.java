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

package jp.ngt.mcte.gui;

import java.io.File;

import org.lwjgl.opengl.GL11;

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.MCTEKeyHandlerClient;
import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.mcte.editor.filter.FilterManager;
import jp.ngt.mcte.network.PacketEditor;
import jp.ngt.mcte.network.PacketResetSlot;
import jp.ngt.ngtlib.gui.GuiButtonCustom;
import jp.ngt.ngtlib.gui.GuiContainerCustom;
import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEditor extends GuiContainerCustom
{
	private final EntityEditor editor;
	private GuiTextField[] textField_name = new GuiTextField[3];
	private GuiTextField[] textField_start = new GuiTextField[3];
	private GuiTextField[] textField_end = new GuiTextField[3];
	private GuiTextField[] textField_clone = new GuiTextField[4];
	private GuiButton buttonFillMode;
	private GuiButton buttonScale;
	private float scale;
	private String selectedFilterName;

	public GuiEditor(EntityEditor par1)
	{
		super(new ContainerEditor(par1));
		this.editor = par1;
		this.setFilterName("Fill");
	}

	@Override
	public boolean doesGuiPauseGame()
    {
        return false;
    }

	@Override
	public void initGui()
    {
		super.initGui();
		this.scale = 1.0F;
		this.mc.player.openContainer = this.inventorySlots;
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(100, 10, 10, 20, 20, "<"));
        this.buttonList.add(new GuiButton(101, 70, 10, 20, 20, ">"));
        this.buttonList.add(new GuiButton(102, 10, 30, 20, 20, "<"));
        this.buttonList.add(new GuiButton(103, 70, 30, 20, 20, ">"));
        this.buttonList.add(new GuiButton(104, 10, 50, 20, 20, "<"));
        this.buttonList.add(new GuiButton(105, 70, 50, 20, 20, ">"));
        int[] start = this.editor.getPos(EntityEditor.START_POS);
        this.textField_start[0] = this.setTextField(30, 10, 40, 20, String.valueOf(start[0]));
        this.textField_start[1] = this.setTextField(30, 30, 40, 20, String.valueOf(start[1]));
        this.textField_start[2] = this.setTextField(30, 50, 40, 20, String.valueOf(start[2]));

        this.buttonList.add(new GuiButton(106, 10, 80, 20, 20, "<"));
        this.buttonList.add(new GuiButton(107, 70, 80, 20, 20, ">"));
        this.buttonList.add(new GuiButton(108, 10, 100, 20, 20, "<"));
        this.buttonList.add(new GuiButton(109, 70, 100, 20, 20, ">"));
        this.buttonList.add(new GuiButton(110, 10, 120, 20, 20, "<"));
        this.buttonList.add(new GuiButton(111, 70, 120, 20, 20, ">"));
        int[] end = this.editor.getPos(EntityEditor.END_POS);
        this.textField_end[0] = this.setTextField(30, 80, 40, 20, String.valueOf(end[0]));
        this.textField_end[1] = this.setTextField(30, 100, 40, 20, String.valueOf(end[1]));
        this.textField_end[2] = this.setTextField(30, 120, 40, 20, String.valueOf(end[2]));

        this.buttonList.add(new GuiButton(112, this.width - 90, 10, 20, 20, "<"));
        this.buttonList.add(new GuiButton(113, this.width - 30, 10, 20, 20, ">"));
        this.buttonList.add(new GuiButton(114, this.width - 90, 30, 20, 20, "<"));
        this.buttonList.add(new GuiButton(115, this.width - 30, 30, 20, 20, ">"));
        this.buttonList.add(new GuiButton(116, this.width - 90, 50, 20, 20, "<"));
        this.buttonList.add(new GuiButton(117, this.width - 30, 50, 20, 20, ">"));
        this.buttonList.add(new GuiButton(118, this.width - 90, 70, 20, 20, "<"));
        this.buttonList.add(new GuiButton(119, this.width - 30, 70, 20, 20, ">"));
        this.textField_clone[0] = this.setTextField(this.width - 70, 10, 40, 20, String.valueOf(this.editor.getCloneBox()[0]));
        this.textField_clone[1] = this.setTextField(this.width - 70, 30, 40, 20, String.valueOf(this.editor.getCloneBox()[1]));
        this.textField_clone[2] = this.setTextField(this.width - 70, 50, 40, 20, String.valueOf(this.editor.getCloneBox()[2]));
        this.textField_clone[3] = this.setTextField(this.width - 70, 70, 40, 20, String.valueOf(this.editor.getCloneBox()[3]));

        this.buttonList.add(new GuiButtonCustom(200, 10, 150, 60, 20, "Fill->", this).addTips(NGTUtil.translate("tips.editor.fill")));
        this.buttonList.add(new GuiButtonCustom(201, 10, 170, 60, 20, "Replace->", this).addTips(NGTUtil.translate("tips.editor.replace")));
        this.buttonFillMode = new GuiButtonCustom(202, 10, 190, 60, 20, this.getFilterName(), this).addTips(NGTUtil.translate("tips.editor.filter"));
        this.buttonList.add(this.buttonFillMode);
        this.buttonList.add(new GuiButtonCustom(203, 70, 190, 20, 20, "Do", this).addTips(NGTUtil.translate("tips.editor.do")));
        this.buttonList.add(new GuiButton(204, 10, 210, 80, 20, "Delete Entity"));

        this.buttonList.add(new GuiButton(300, this.width - 90, 90, 80, 20, "Clone"));

        this.buttonList.add(new GuiButton(400, this.width - 90, 110, 40, 20, "Rotate"));
        this.buttonList.add(new GuiButton(401, this.width - 90, 130, 40, 20, "Rotate"));
        this.buttonList.add(new GuiButton(402, this.width - 90, 150, 40, 20, "Rotate"));
        this.buttonList.add(new GuiButton(403, this.width - 50, 110, 40, 20, "Mirror"));
        this.buttonList.add(new GuiButton(404, this.width - 50, 130, 40, 20, "Mirror"));
        this.buttonList.add(new GuiButton(405, this.width - 50, 150, 40, 20, "Mirror"));

        this.buttonScale = new GuiButton(500, this.width - 90, 190, 40, 20, String.valueOf(this.scale));
        this.buttonList.add(this.buttonScale);
        this.buttonList.add(new GuiButton(501, this.width - 50, 190, 40, 20, "Miniature"));
        this.buttonList.add(new GuiButton(502, this.width - 90, 210, 40, 20, "Export"));
        this.buttonList.add(new GuiButton(503, this.width - 50, 210, 40, 20, "Import"));

        this.currentTextField = this.textField_start[0];
    }

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id >= 100 && button.id <= 117)
		{
			int i0 = 0;
			int i1 = 0;
			int i2 = 0;
			GuiTextField field = null;

			if(button.id >= 100 && button.id <= 105)
			{
				i0 = 0;
				i1 = button.id - 100;
				i2 = i1 / 2;
				field = this.textField_start[i2];
			}
			else if(button.id >= 106 && button.id <= 111)
			{
				i0 = 1;
				i1 = button.id - 106;
				i2 = i1 / 2;
				field = this.textField_end[i2];
			}
			else if(button.id >= 112 && button.id <= 117)
			{
				i0 = 2;
				i1 = button.id - 112;
				i2 = i1 / 2;
				field = this.textField_clone[i2];
			}

			int i3 = i1 % 2 > 0 ? 1 : -1;
			int i4 = this.getTextFieldValue(i0, i2);

			if(field != null)
			{
				field.setText(String.valueOf(i4 + i3));
			}
		}
		else if(button.id >= 118 && button.id <= 119)
		{
			int i1 = button.id - 118;
			int i3 = i1 == 0 ? -1 : 1;
			int i4 = this.getTextFieldValue(2, 3);
			int i5 = i4 + i3;
			this.textField_clone[3].setText(String.valueOf(i5 < 0 ? 0 : (i5 < 64 ? i5 : 64)));
		}
		else if(button.id == 200)
		{
			this.sendPacket();
			FilterManager.INSTANCE.execFilter(this.mc.player, this.editor, "Fill");
			return;
		}
		else if(button.id == 201)
		{
			this.sendPacket("replace");
			return;
		}
		else if(button.id == 202)
		{
			//フィルタ設定画面
			this.mc.displayGuiScreen(new GuiFilterSetting(this, this.selectedFilterName));
		}
		else if(button.id == 203)
		{
			//フィルタ実行
			this.sendPacket();
			FilterManager.INSTANCE.execFilter(this.mc.player, this.editor, this.getFilterName());
		}
		else if(button.id == 204)
		{
			this.sendPacket();
			FilterManager.INSTANCE.execFilter(this.mc.player, this.editor, "DeleteEntity");
			return;
		}
		else if(button.id == 300)
		{
			this.sendPacket("clone");
			return;
		}
		else if(button.id >= 400 && button.id <= 405)
		{
			int type = button.id - 400;
			this.sendPacket("transform:" + type);
		}
		else if(button.id == 500)
		{
			this.scale *= 0.5F;
			if(this.scale < 0.015625F)//1/64
			{
				this.scale = 64.0F;
			}
			this.buttonScale.displayString = String.valueOf(this.scale);
		}
		else if(button.id == 501)
		{
			this.sendPacket("miniature:" + this.scale);
		}
		else if(button.id == 502)
		{
			this.exportBlocks();
		}
		else if(button.id == 503)
		{
			this.importBlocks();
		}

		this.sendPacket();
    }

	private void exportBlocks()
	{
		File file = NGTFileLoader.saveFile(FileType.NGTO, FileType.OBJ);
		if(file != null)
		{
			this.sendPacket("export " + file.getAbsolutePath());
		}
	}

	private void importBlocks()
	{
		File file = NGTFileLoader.selectFile(FileType.NGTO, FileType.MQO, FileType.OBJ);
		if(file != null)
		{
			this.editor.importBlocks(file, this.scale);
		}
	}

	@Override
	protected void keyTyped(char par1, int par2)
    {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode() || par2 == MCTEKeyHandlerClient.keyEditMenu.getKeyCode())//1:Esc
		{
			this.mc.player.closeScreen();
		}

		if((par2 >= 2 && par2 <= 11) || (par2 >= 200 && par2 <= 205) || par2 == 12 || par2 == 14 || par2 == 211)//14:Back, 211:Del
		{
			this.currentTextField.textboxKeyTyped(par1, par2);
		}

		if(par2 == 28)//28:Enter
		{
			this.sendPacket();
		}
    }

	@Override
	protected void onTextFieldClicked(GuiTextField field)
	{
		this.sendPacket();
	}

	/**スロットの位置を調整&サーバーに送信*/
	private void resetSlotPosition()
	{
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = this.width;
		this.ySize = this.height;

		for(int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i);
            if(slot.inventory instanceof InventoryPlayer)
            {
            	int x0 = (this.width / 2) - 88 + slot.getSlotIndex() * 20;
            	int y0 = this.height - 19;
            	if(slot.xPos != x0 && slot.yPos != y0)
            	{
            		slot.xPos = x0;
                	slot.yPos = y0;
                	MCTE.NETWORK_WRAPPER.sendToServer(new PacketResetSlot(this.editor, slot));
            	}
            }
        }
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
        this.drawCustomBackground();
        super.drawScreen(par1, par2, par3);

        this.resetSlotPosition();
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
		this.drawCenteredString(this.fontRenderer, "Start", 50, 0, 0xff0000);
        this.drawString(this.fontRenderer, "X", 5, 15, 0xff0000);
        this.drawString(this.fontRenderer, "Y", 5, 35, 0xff0000);
        this.drawString(this.fontRenderer, "Z", 5, 55, 0xff0000);

        this.drawCenteredString(this.fontRenderer, "End", 50, 70, 0x0000ff);
        this.drawString(this.fontRenderer, "X", 5, 85, 0x0000ff);
        this.drawString(this.fontRenderer, "Y", 5, 105, 0x0000ff);
        this.drawString(this.fontRenderer, "Z", 5, 125, 0x0000ff);

        this.drawCenteredString(this.fontRenderer, "Clone", this.width - 50, 0, 0xffff00);
        this.drawString(this.fontRenderer, "X", this.width - 95, 15, 0xffff00);
        this.drawString(this.fontRenderer, "Y", this.width - 95, 35, 0xffff00);
        this.drawString(this.fontRenderer, "Z", this.width - 95, 55, 0xffff00);
        this.drawString(this.fontRenderer, "R", this.width - 95, 75, 0xffff00);

        this.drawString(this.fontRenderer, "X", this.width - 95, 115, 0x00ff00);
        this.drawString(this.fontRenderer, "Y", this.width - 95, 135, 0x00ff00);
        this.drawString(this.fontRenderer, "Z", this.width - 95, 155, 0x00ff00);

        int sizeX = Math.abs(this.getTextFieldValue(0, 0) - this.getTextFieldValue(1, 0)) + 1;
        this.drawString(this.fontRenderer, "X:" + sizeX, 102, 0, 16777215);
        int sizeY = Math.abs(this.getTextFieldValue(0, 1) - this.getTextFieldValue(1, 1)) + 1;
        this.drawString(this.fontRenderer, "Y:" + sizeY, 102, 10, 16777215);
        int sizeZ = Math.abs(this.getTextFieldValue(0, 2) - this.getTextFieldValue(1, 2)) + 1;
        this.drawString(this.fontRenderer, "Z:" + sizeZ, 102, 20, 16777215);
    }

	private void drawCustomBackground()
	{
		int i0 = -1072689136;
		int i1 = -804253680;
		float f = (float)(i0 >> 24 & 255) / 255.0F;
        float f1 = (float)(i0 >> 16 & 255) / 255.0F;
        float f2 = (float)(i0 >> 8 & 255) / 255.0F;
        float f3 = (float)(i0 & 255) / 255.0F;
        float f4 = (float)(i1 >> 24 & 255) / 255.0F;
        float f5 = (float)(i1 >> 16 & 255) / 255.0F;
        float f6 = (float)(i1 >> 8 & 255) / 255.0F;
        float f7 = (float)(i1 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(100.0F, (float)this.height, (float)this.zLevel);
		tessellator.addVertex(100.0F, 0.0F,               (float)this.zLevel);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex(0.0F, 0.0F,               (float)this.zLevel);
		tessellator.addVertex(0.0F, (float)this.height, (float)this.zLevel);

		tessellator.setColorRGBA_F(f1, f2, f3, f);
		tessellator.addVertex((float)this.width, (float)this.height, (float)this.zLevel);
		tessellator.addVertex((float)this.width, 0.0F,               (float)this.zLevel);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex((float)this.width - 100.0F, 0.0F,               (float)this.zLevel);
		tessellator.addVertex((float)this.width - 100.0F, (float)this.height, (float)this.zLevel);

        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public void drawDefaultBackground(){}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		;
	}

	private void sendPacket()
	{
		this.sendPacket("");
	}

	private void sendPacket(String par1)
	{
		int x;
		int y;
		int z;
		x = this.getTextFieldValue(0, 0);
		y = this.getTextFieldValue(0, 1);
		z = this.getTextFieldValue(0, 2);
		int[] start = {x, y, z};
		x = this.getTextFieldValue(1, 0);
		y = this.getTextFieldValue(1, 1);
		z = this.getTextFieldValue(1, 2);
		int[] end = {x, y, z};
		x = this.getTextFieldValue(2, 0);
		y = this.getTextFieldValue(2, 1);
		z = this.getTextFieldValue(2, 2);
		int r = this.getTextFieldValue(2, 3);
		int[] clone = {x, y, z, r};
		MCTE.NETWORK_WRAPPER.sendToServer(new PacketEditor(this.editor, par1, start, end, clone));

		if(par1.startsWith("fill"))
		{
			FilterManager.INSTANCE.execFilter(this.editor.getPlayer(), this.editor, "Fill");
		}
	}

	/**
	 * @param par1 0:start, 1:end, 2:clone
	 * @param par2 0:x, 1:y, 2:z, 3:-
	 */
	private int getTextFieldValue(int par1, int par2)
	{
		try
		{
			switch(par1)
			{
			case 0: return Integer.valueOf(this.textField_start[par2].getText());
			case 1: return Integer.valueOf(this.textField_end[par2].getText());
			case 2: return Integer.valueOf(this.textField_clone[par2].getText());
			}
		}
		catch(NumberFormatException e)
		{
			switch(par1)
			{
			case 0: return this.editor.getPos(EntityEditor.START_POS)[par2];
			case 1: return this.editor.getPos(EntityEditor.END_POS)[par2];
			case 2: return 0;
			}
		}
		return 0;
	}

	public void setFilterName(String name)
	{
		this.selectedFilterName = name;
		if(this.buttonFillMode != null)//コンストラクタから呼び出し時のぬるぽ防止
		{
			this.buttonFillMode.displayString = name;
		}
	}

	public String getFilterName()
	{
		return this.selectedFilterName;
	}
}