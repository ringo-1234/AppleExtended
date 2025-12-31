package jp.ngt.ngtlib.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerCustom extends GuiContainer
{
	private static int NEXT_FIELD_ID;

	protected boolean drawTextBox = true;

	public GuiContainerCustom(Container par1)
	{
		super(par1);
	}

	protected List<GuiTextField> textFields = new ArrayList<GuiTextField>();
	protected GuiTextField currentTextField;

	@Override
	public void initGui()
    {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		NEXT_FIELD_ID = 0;
    }

	@Override
	public void onGuiClosed()
    {
		super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

	protected GuiTextFieldCustom setTextField(int xPos, int yPos, int w, int h, String text)
	{
		GuiTextFieldCustom field = new GuiTextFieldCustom(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this);
		field.setMaxStringLength(32767);
		field.setFocused(false);
		field.setText(text);
		this.textFields.add(field);
		return field;
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
    {
        super.mouseClicked(par1, par2, par3);

        for(GuiTextField field : this.textFields)
        {
        	field.mouseClicked(par1, par2, par3);
        	if(field.isFocused())
        	{
        		this.currentTextField = field;
        		this.onTextFieldClicked(field);
        		break;
        	}
        }
    }

	protected void onTextFieldClicked(GuiTextField field){}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
    {
		super.keyTyped(par1, par2);

		if(this.currentTextField != null)
		{
			this.currentTextField.textboxKeyTyped(par1, par2);
		}
    }

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		if(this.drawTextBox)
		{
			for(GuiTextField field : this.textFields)
	        {
	        	field.drawTextBox();
	        }
		}

		super.drawScreen(par1, par2, par3);
    }

	@Override
	public void updateScreen()
    {
		super.updateScreen();

		if(this.currentTextField != null)
		{
			this.currentTextField.updateCursorCounter();
		}
    }

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y)
    {
		super.drawHoveringText(textLines, x, y);
    }
}