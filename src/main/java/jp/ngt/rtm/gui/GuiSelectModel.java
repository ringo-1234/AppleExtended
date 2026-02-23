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

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JFrame;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.gui.GuiButtonCustom;
import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.IConfigWithType;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.network.PacketSelectResource;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectModel extends GuiScreenCustom
{
	public static final ResourceLocation ButtonBlue = new ResourceLocation("rtm", "textures/gui/button_blue.png");

	public final IResourceSelector selector;
	private List<ResourceSet> modelListAll;
	private List<ResourceSet> modelListSelect;
	private GuiButtonSelectModel[] selectButtons;
	private GuiTextField nameField;
	protected GuiTextField argField;
	private GuiTextField searchField;
	private GuiButtonCustom colorButton;

	private int modelColor;
	private int currentScroll;
	private boolean wasClicking;

	public GuiSelectModel(World par1, IResourceSelector par2)
	{
		this.xSize = 352;
		this.ySize = 240;
		this.selector = par2;

		ResourceState state = par2.getResourceState();
		if(state.type.subType == null)
		{
			this.modelListAll = ModelPackManager.INSTANCE.getModelList(state.type);
		}
		else
		{
			String subType = state.type.subType;
			List<ResourceSet> list = ModelPackManager.INSTANCE.getModelList(state.type);
			this.modelListAll = new ArrayList<>();
			for(ResourceSet modelSet : list)
			{
				if(((IConfigWithType)modelSet.getConfig()).getSubType().equals(subType))
				{
					this.modelListAll.add(modelSet);
				}
			}
		}
		this.modelListSelect = new ArrayList<>();
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.buttonList.clear();

		ResourceState state = this.selector.getResourceState();
		this.nameField = this.setTextField(this.width - 205, 5, 120, 20, state.getName()).addTips("Custom Name");
		this.argField = this.setTextField(this.width - 205, 30, 100, 20, state.getArg()).addTips("Custom Parameters");
		this.searchField = this.setTextField(this.width - 80, 5, 60, 20, "").addTips("Search Box");
		this.modelColor = state.color;
		this.colorButton = this.addButton(new GuiButtonCustom(10000, this.width - 80, 30, 40, 20, "0xFFFFFF", this)).addTips("Select Color");
		this.setColorToButton(this.modelColor);
		this.addButton(new GuiButtonCustom(10001, this.width - 105, 30, 20, 20, " ", this)).addTips("DataMap");

		this.resetModelList();
	}

	/**入力されたキーワードを含むモデルを抽出*/
	private void resetModelList()
	{
		if(this.selectButtons != null)
		{
			this.buttonList.removeAll(Arrays.asList(this.selectButtons));
		}
		this.modelListSelect.clear();
		this.currentScroll = 0;

		String keyword = this.searchField.getText();
		if(keyword == null || keyword.length() == 0)
		{
			this.modelListSelect.addAll(this.modelListAll);
		}
		else
		{
			for(ResourceSet set : this.modelListAll)
			{
				if(set.getConfig().tags.contains(keyword))
				{
					this.modelListSelect.add(set);
				}
			}
		}

		//名前順にソート
		Collections.sort(this.modelListSelect, (o1, o2)->{
			return o1.getConfig().getName().compareTo(o2.getConfig().getName());
		});

		int i0 = (this.height / 2) - 16;
		this.selectButtons = new GuiButtonSelectModel[this.modelListSelect.size()];
		for(int i = 0; i < this.selectButtons.length; ++i)
		{
			ResourceSet modelSet = this.modelListSelect.get(i);
			this.selectButtons[i] = new GuiButtonSelectModel(i, 10, i0 + 32 * i,
					(ModelSetBase)modelSet, modelSet.getConfig().getName(), this);
			this.buttonList.add(this.selectButtons[i]);

			if(modelSet.getConfig().getName().equals(this.selector.getResourceState().getResourceName()))
			{
				this.currentScroll = i;
				this.selectButtons[i].isSelected = true;
			}
		}
		this.resetButtonPos();

		this.buttonList.add(new GuiButton(10900, this.width + 36, this.height - 20, 100, 20, "cansel"));
	}

	private void setColorToButton(int color)
	{
		String colorS = "0x" + Integer.toHexString(color);
		this.colorButton.displayString = colorS;
	}

	private void resetColor()
	{
		try
		{
			this.modelColor = Integer.decode(this.colorButton.displayString);
			this.selector.getResourceState().color = this.modelColor;
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
        //ここにConfigのモデルの説明文を表示
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(this.modelListSelect.size() > 0)
		{
			this.drawScrollBar(par2, par3);
		}
		this.drawColorPalet();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		if(this.modelListAll.size() == 0)
		{
			this.fontRenderer.drawString("Can't get list", (this.width - this.xSize) / 2, (this.height - this.ySize) / 2, 0xff0000);
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.wasClicking = Mouse.isButtonDown(0);
		boolean clickIsAvailable = par1 < this.width && par1 >= this.width - 16;
		if(this.wasClicking && clickIsAvailable)
		{
			int mouseY = par2 < 8 ? 8 : (par2 >= this.height ? this.height : par2);
			int i1 = NGTMath.floor((float)mouseY * (float)(this.modelListSelect.size() + 1) / (float)(this.height - 16));
			this.scroll(i1);
		}

		float z = this.zLevel;
		this.zLevel = -1000.0F;//モデルプレビューの後ろが切れないように
		this.drawDefaultBackground();
		this.zLevel = z;

		super.drawScreen(par1, par2, par3);
    }

	private void drawColorPalet()
	{
		NGTTessellator tessellator = NGTTessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(this.modelColor);
		tessellator.addVertex(this.width - 20, 50, this.zLevel);
		tessellator.addVertex(this.width - 20, 30, this.zLevel);
		tessellator.addVertex(this.width - 40, 30, this.zLevel);
		tessellator.addVertex(this.width - 40, 50, this.zLevel);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void drawScrollBar(int mouseX, int mouseY)
	{
		NGTTessellator tessellator = NGTTessellator.instance;

		//バー描画
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.setColorOpaque_I(0xFFFFFF);
		tessellator.startDrawingQuads();
		tessellator.addVertex((float)(this.width - 7), (float)(this.height - 8), (float)this.zLevel);
		tessellator.addVertex((float)(this.width - 7), 8.0F,                     (float)this.zLevel);
		tessellator.addVertex((float)(this.width - 9), 8.0F,                     (float)this.zLevel);
		tessellator.addVertex((float)(this.width - 9), (float)(this.height - 8), (float)this.zLevel);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		int buttonY = 0;
		if(this.wasClicking)
		{
			buttonY = (mouseY < 8 ? 8 : (mouseY >= this.height - 8 ? this.height - 8 : mouseY)) - 8;
		}
		else if(this.modelListSelect.size() > 1)
		{
			buttonY = this.currentScroll * (this.height - 16) / (this.modelListSelect.size() - 1);
		}

		//ボタン描画
		this.mc.getTextureManager().bindTexture(ButtonBlue);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((float)(this.width),      (float)(buttonY + 16), (float)this.zLevel, 1.0F, 0.0625F);
		tessellator.addVertexWithUV((float)(this.width),      (float)buttonY,        (float)this.zLevel, 1.0F, 0.0F);
		tessellator.addVertexWithUV((float)(this.width - 16), (float)buttonY,        (float)this.zLevel, 0.9375F, 0.0F);
		tessellator.addVertexWithUV((float)(this.width - 16), (float)(buttonY + 16), (float)this.zLevel, 0.9375F, 0.0625F);
		tessellator.draw();
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int z, int u, int v, int p_73729_6_)
    {
        float f = 1.0F / 512.0F;
        float f1 = 1.0F / 512.0F;
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((float)(x + 0), (float)(y + p_73729_6_), (float)this.zLevel, ((float)(z + 0) * f), ((float)(u + p_73729_6_) * f1));
        tessellator.addVertexWithUV((float)(x + v), (float)(y + p_73729_6_), (float)this.zLevel, ((float)(z + v) * f), ((float)(u + p_73729_6_) * f1));
        tessellator.addVertexWithUV((float)(x + v), (float)(y + 0), (float)this.zLevel, ((float)(z + v) * f), ((float)(u + 0) * f1));
        tessellator.addVertexWithUV((float)(x + 0), (float)(y + 0), (float)this.zLevel, ((float)(z + 0) * f), ((float)(u + 0) * f1));
        tessellator.draw();
    }

	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
    {
        super.mouseClicked(par1, par2, par3);
    }

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
    {
		super.keyTyped(par1, par2);

		this.resetModelList();
		this.resetColor();
    }

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 10900)
		{
			if(this.selector.closeGui(null))
			{
				this.mc.displayGuiScreen(null);
			}
		}
		else if(button.id == 10000)
		{
			this.openColorChooser();
		}
		else if(button.id == 10001)
		{
			this.openDataMapEditor();
		}
		else if(button.id < this.modelListSelect.size())
		{
			ResourceState state = this.selector.getResourceState();
			String name = this.modelListSelect.get(button.id).getConfig().getName();
			state.setResourceName(name);
			state.getResourceSet();//DataMap等初期化
			state.color = this.modelColor;
			state.setName(this.nameField.getText());
			state.setArg(this.argField.getText(), true);
			if(this.saveData(state))
			{
				RTMCore.NETWORK_WRAPPER.sendToServer(new PacketSelectResource(this.selector));
				this.mc.displayGuiScreen(null);
			}
		}
	}

	private void openColorChooser()
	{
		JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setBounds(20, 20, 450, 400);
	    frame.setTitle("Select Color");

	    JColorChooser chooser = new JColorChooser(new Color(this.modelColor));
	    chooser.getSelectionModel().addChangeListener((event)->{
	    	int color = chooser.getColor().getRGB() & 0xFFFFFF;//ARGB
	    	this.setColorToButton(color);
	    	this.resetColor();
	    });
	    frame.getContentPane().add(chooser, BorderLayout.CENTER);

	    frame.setVisible(true);
	}

	private void openDataMapEditor()
	{
		DataMapEditor editor = new DataMapEditor(this);
	}

	protected boolean saveData(ResourceState state)
	{
		return this.selector.closeGui(state);
	}

	@Override
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int i0 = Mouse.getEventDWheel();
        if(i0 != 0)
        {
            this.scroll(this.currentScroll - (i0 > 0 ? 1 : -1));
        }
    }

	private void scroll(int par1)
	{
		this.currentScroll = par1;

        if(this.currentScroll < 0)
        {
            this.currentScroll = 0;
        }
        else if(this.currentScroll >= this.selectButtons.length)
        {
            this.currentScroll = this.selectButtons.length - 1;
        }

        this.resetButtonPos();
	}

	/**ボタンの位置更新*/
	private void resetButtonPos()
	{
		int i0 = (this.height / 2) - 16;

		for(int i = 0; i < this.selectButtons.length; ++i)
		{
			this.selectButtons[i].y = i0 + 32 * (i - this.currentScroll);
		}
	}
}