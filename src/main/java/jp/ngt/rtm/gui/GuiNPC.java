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

import java.util.List;

import org.lwjgl.opengl.GL11;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.network.PacketSelectResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNPC extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("rtm", "textures/gui/npc.png");

	protected final EntityPlayer player;
	protected final EntityNPC npc;
	protected final List<ResourceSet> modelList;
	private int index;

	public GuiNPC(EntityPlayer par1, EntityNPC par2)
    {
		super(new ContainerNPC(par1, par2));
		this.player = par1;
		this.npc = par2;
		this.modelList = ModelPackManager.INSTANCE.getModelList(RTMResource.NPC);

		this.xSize = 176;
		this.ySize = 224;
    }

	@Override
	public void initGui()
    {
		super.initGui();

		this.buttonList.clear();
    	this.buttonList.add(new GuiButtonNPC(100, this.guiLeft + 78 - 10, this.guiTop + 78 - 15, 0));
    	this.buttonList.add(new GuiButtonNPC(101, this.guiLeft + 26, this.guiTop + 78 - 15, 1));

    	this.index = this.modelList.indexOf(this.npc.getResourceState().getResourceSet());
    	if(this.index == -1)
    	{
    		this.index = 0;
    	}
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x0 = (this.width - this.xSize) / 2;
        int y0 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x0, y0, 0, 0, this.xSize, this.ySize);
        //Entityモデル描画
        GuiInventory.drawEntityOnScreen(x0 + 51, y0 + 75, 30, (float)(x0 + 51 - x), (float)(y0 + 25 - y), this.npc);
	}

	@Override
    protected void actionPerformed(GuiButton button)
    {
    	if(button.id == 100)
    	{
    		this.setModel(++this.index);
    	}
    	else if(button.id == 101)
    	{
    		this.setModel(--this.index);
    	}
    }

	private void setModel(int par1)
	{
		if(par1 >= this.modelList.size())
		{
			par1 = 0;
		}
		else if(par1 < 0)
		{
			par1 = this.modelList.size() - 1;
		}
		this.index = par1;
		String s = this.modelList.get(par1).getConfig().getName();
		this.npc.getResourceState().setResourceName(s);
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketSelectResource(this.npc));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        String name = this.npc.getResourceState().getResourceName();
        this.fontRenderer.drawString(name, this.guiLeft + 90, this.guiTop + 8, 0x000000);
        this.fontRenderer.drawString("HP:" + this.npc.getHealth(), this.guiLeft + 90, this.guiTop + 16, 0x000000);

        this.renderHoveredToolTip(par1, par2);
    }

	private class GuiButtonNPC extends GuiButton
	{
		private int type;

		public GuiButtonNPC(int id, int x, int y, int t)
		{
			super(id, x, y, 10, 15, "");
			this.type = t;
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y, float ptick)
	    {
	        if (this.visible)
	        {
	            mc.getTextureManager().bindTexture(TEXTURE);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
	            int k = this.getHoverState(this.hovered);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            int u = ((k == 2) ? 16 : 0) + 176;
	            int v = (this.type == 1) ? 16 : 0;
	            this.drawTexturedModalRect(this.x, this.y, u, v, 10, 15);
	            this.mouseDragged(mc, x, y);
	        }
	    }
	}
}