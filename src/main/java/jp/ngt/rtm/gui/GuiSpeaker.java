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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.rtm.RTMSound;
import jp.ngt.rtm.electric.TileEntitySpeaker;
import jp.ngt.rtm.sound.SpeakerSounds;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSpeaker extends GuiScreenCustom
{
	//private final TileEntitySpeaker tileEntity;
	private GuiTextField searchField;
	private final List<GuiButton> selectButtons = new ArrayList<>();
	private final List<GuiButton> soundButtons = new ArrayList<>();
	private int currentScrollMain;
	private int currentScrollSub;
	private int chooseSoundId;

	public GuiSpeaker(TileEntitySpeaker par1)
	{
		//this.tileEntity = par1;
	}

	@Override
	public void initGui()
    {
		super.initGui();

		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 + 20, this.height - 28, 120, 20, I18n.format("gui.done", new Object[0])));
		//this.buttonList.add(new GuiButton(1, this.width / 2 + 20, this.height - 28, 120, 20, I18n.format("gui.cancel", new Object[0])));

		this.searchField = this.setTextField(this.width / 2 - 140, this.height - 28, 120, 20, "").addTips("Search Box");

		int selWidth = 30;
		int sndWidth = 200;

		this.selectButtons.clear();
		for(int i = 0; i < SpeakerSounds.MAX_SOUND_ID; ++i)
		{
			int y = (i - this.currentScrollMain) * 20;
			int x = this.width - selWidth;
			GuiButton button = new GuiButton(100 + i, x, y, selWidth, 20, "select");
			this.buttonList.add(button);
			this.selectButtons.add(button);
			if(i == this.chooseSoundId - 1)
			{
				button.enabled = false;
			}
		}

		this.initSoundList();
    }

	private void initSoundList()
	{
		int selWidth = 30;
		int sndWidth = 200;

		this.currentScrollSub = 0;
		if(!this.soundButtons.isEmpty())
		{
			this.buttonList.removeAll(this.soundButtons);
		}
		this.soundButtons.clear();

		if(this.chooseSoundId > 0)
		{
			String searchWrd = this.searchField.getText();

			int i = 0;
			for(String fileName : RTMSound.ALL_OGG_FILES)
			{
				if(fileName.contains(searchWrd))
				{
					int y = (i - this.currentScrollSub) * 20;
					int x = this.width - (selWidth + sndWidth);
					GuiButton button = new GuiButton(500 + i, x, y, sndWidth, 20, fileName);
					this.buttonList.add(button);
					this.soundButtons.add(button);
					++i;
				}
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        for(int i = 0; i < this.selectButtons.size(); ++i)
		{
        	int posY = (i - this.currentScrollMain) * 20 + 4;
        	String s = String.format("%d : %s", i + 1, SpeakerSounds.getInstance(false).getSound(i + 1));
        	this.drawString(this.fontRenderer, s, 20, posY, 0xFFFFFF);
		}
    }

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id == 0)
        {
            this.mc.displayGuiScreen(null);
            //this.sendPacket();
        }
		else if(button.id == 1)
        {
            this.mc.displayGuiScreen(null);
        }
		else if(button.id >= 100 && button.id < 100 + SpeakerSounds.MAX_SOUND_ID)
        {
			int id = button.id - 100;
			this.chooseSoundId = id + 1;
			this.initGui();
        }
		else if(button.id >= 500 && button.id < 500 + RTMSound.ALL_OGG_FILES.size())
        {
			SpeakerSounds.getInstance(false).setSound(this.chooseSoundId, button.displayString, true);
			this.chooseSoundId = 0;
			this.initGui();
        }
		super.actionPerformed(button);
    }

	@Override
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int i0 = Mouse.getEventDWheel();
        if(i0 != 0)
        {
        	i0 = i0 > 0 ? 1 : -1;
            this.scroll(i0);
        }
    }

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
    {
		super.keyTyped(par1, par2);

		this.initSoundList();
    }

	private void scroll(int par1)
	{
		if(this.chooseSoundId > 0)
		{
			this.currentScrollSub = this.scroll(this.currentScrollSub - par1, this.soundButtons.size());
		}
		else
		{
			this.currentScrollMain = this.scroll(this.currentScrollMain - par1, this.selectButtons.size());
		}
        this.resetButtonPos();
	}

	private int scroll(int scroll, int max)
	{
		if(scroll < 0)
        {
            return 0;
        }
        else if(scroll >= max)
        {
            return max - 1;
        }
		return scroll;
	}

	/**ボタンの位置更新*/
	private void resetButtonPos()
	{
		if(this.chooseSoundId > 0)
		{
			for(int i = 0; i < this.soundButtons.size(); ++i)
			{
				this.soundButtons.get(i).y = (i - this.currentScrollSub) * 20;
			}
		}
		else
		{
			for(int i = 0; i < this.selectButtons.size(); ++i)
			{
				this.selectButtons.get(i).y = (i - this.currentScrollMain) * 20;
			}
		}
	}
}
