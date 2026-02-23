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
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.TextureConfig;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.modelpack.modelset.TextureSetBase;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.network.PacketSelectResource;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectTexture extends GuiScreenCustom
{
	public final IResourceSelector holder;
	private final GuiScreen parent;
	private List<ResourceSet> resourceSets;
	private int currentScroll;
	private int prevScroll;
	private int uCount, vCount;

	public GuiSelectTexture(IResourceSelector par1, @Nullable GuiScreen screen)
	{
		this.holder = par1;
		this.resourceSets = ModelPackManager.INSTANCE.getModelList(par1.getResourceState().type);
		this.parent = screen;
	}

	@Override
	public void initGui()
	{
		TextureConfig cfg = ((TextureSetBase)this.resourceSets.get(0)).getConfig();
		this.uCount = !this.resourceSets.isEmpty() ? cfg.getUCountInGui() : 1;
		this.vCount = !this.resourceSets.isEmpty() ? cfg.getVCountInGui() : 1;
		int x = this.width / this.uCount;
		int y = this.height / vCount;
		this.buttonList.clear();

		int yCount = (this.resourceSets.size() / this.uCount) + 1;

		for(int v = 0; v < yCount; ++v)
		{
			for(int u = 0; u < this.uCount; ++u)
			{
				int index = v * this.uCount + u;
				if(index >= this.resourceSets.size())
				{
					break;
				}
				TextureSetBase set = (TextureSetBase)this.resourceSets.get(index);
				TextureConfig prop = set.getConfig();
				float f0 = 1.0F;
				if(prop.width > prop.height)
				{
					f0 = (float)x / prop.width;
				}
				else
				{
					f0 = (float)y / prop.height;
				}

				int w = (int)(prop.width * f0);
				int h = (int)(prop.height * f0);
				int xPos = x * u + ((x - w) / 2);
				int yPos = y * v + ((y - h) / 2);
				this.buttonList.add(new GuiButtonSelectTexture(index, xPos, yPos, w, h,
						this.holder.getResourceState().type, set));
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);
    }

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 256)
		{
			this.mc.displayGuiScreen(this.parent);
		}

		if(button.id < this.resourceSets.size())
		{
			ResourceState state = this.holder.getResourceState();
			String name = ((GuiButtonSelectTexture)button).property.getConfig().getName();
			state.setResourceName(name);
			if(this.holder.closeGui(state))
			{
				RTMCore.NETWORK_WRAPPER.sendToServer(new PacketSelectResource(this.holder));
			}
			this.mc.displayGuiScreen(this.parent);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();

        if(scroll != 0)
        {
        	this.prevScroll = this.currentScroll;
        	scroll = scroll > 0 ? 1 : (scroll < 0 ? -1 : 0);
            this.currentScroll -= scroll;

            if(this.currentScroll < 0)
            {
                this.currentScroll = 0;
            }

            int size2 = this.resourceSets.size() / this.uCount;

            if(this.currentScroll >= size2)
            {
                this.currentScroll = size2 - 1;
            }

            this.renewButton(this.currentScroll);
        }
    }

	protected void renewButton(int scroll)
	{
		if(this.currentScroll != this.prevScroll)
		{
			int y = this.height / this.vCount;
			if(this.prevScroll > this.currentScroll)
			{
				y = -y;
			}

			for(int i = 0; i < this.buttonList.size(); ++i)
			{
				((GuiButtonSelectTexture)this.buttonList.get(i)).moveButton(y);
			}
		}
	}
}