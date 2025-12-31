package jp.ngt.rtm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.renderer.model.EncryptedModel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConvertModel extends GuiScreenCustom
{
	private EntityPlayer player;
	private GuiButton buttonDone;

	private File modelFile;
	private List<File> jsonList = new ArrayList<>();

	public GuiConvertModel(EntityPlayer player)
	{
		super();
		this.player = player;
	}

	@Override
	public void initGui()
    {
		super.initGui();

		int hw = this.width / 2;
		this.buttonList.clear();

		this.buttonDone = new GuiButton(0, hw - 155, this.height - 28, 150, 20, I18n.format("gui.done"));
		this.buttonDone.enabled = false;
		this.buttonList.add(this.buttonDone);
        this.buttonList.add(new GuiButton(1, hw + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));

        this.buttonList.add(new GuiButton(100, 20, 40, 100, 20, "Load Model"));
        this.buttonList.add(new GuiButton(101, 20, 70, 100, 20, "Load JSON"));
    }

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id == 0)
        {
			this.exportFile();
            this.mc.displayGuiScreen(null);
        }
		else if(button.id == 1)
        {
            this.mc.displayGuiScreen(null);
        }
		else if(button.id == 100)
        {
			this.selectModel();
        }
		else if(button.id == 101)
        {
			this.selectJSON();
        }
    }

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        if(this.modelFile != null)
        {
        	this.drawString(this.fontRenderer, this.modelFile.getAbsolutePath(), 140, 45, 0xFFFFFF);
        }

        for(int i = 0; i < this.jsonList.size(); ++i)
        {
        	this.drawString(this.fontRenderer, this.jsonList.get(i).getAbsolutePath(), 140, 75 + i * 20, 0xFFFFFF);
        }
    }

	private void selectModel()
	{
		File file = NGTFileLoader.selectFile(FileType.OBJ, FileType.MQO);
		if(file != null)
		{
			this.buttonDone.enabled = true;
			this.modelFile = file;
			NGTLog.debug("Add model file : %s", file.getAbsolutePath());
		}
	}

	private void selectJSON()
	{
		File file = NGTFileLoader.selectFile(FileType.JSON);
		if(file != null)
		{
			this.jsonList.add(file);
			NGTLog.debug("Add json file : %s", file.getAbsolutePath());
		}
	}

	private void exportFile()
	{
		String fileName = this.modelFile.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		fileName = fileName + "." + FileType.NPM.getExtension();
		File file = new File(NGTFileLoader.getModsDir().get(0), fileName);
		try
		{
			EncryptedModel model = new EncryptedModel(this.modelFile, this.jsonList.toArray(new File[this.jsonList.size()]));
			file.createNewFile();
			model.exportData(file);
			NGTLog.sendChatMessage(this.player, "Export to : " + file.getAbsolutePath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			NGTLog.sendChatMessage(this.player, "Failed to export file.");
		}
	}
}