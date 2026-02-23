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

import jp.ngt.ngtlib.block.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class GuiButtonItem extends GuiButton
{
	public ItemStack displayItem;

	public GuiButtonItem(int id, int xPos, int yPos, int width, String string, BlockSet set)
	{
		super(id, xPos, yPos, width, 20, string);
		this.displayItem = new ItemStack(set.block, 1, set.metadata);
		if(this.displayItem.getItem() == null)
		{
			this.displayItem = null;
		}
	}

	@Override
	public void drawButton(Minecraft par1, int par2, int par3, float partialTicks)
    {
		if(this.visible)
        {
            FontRenderer fontrenderer = par1.fontRenderer;
            par1.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            this.mouseDragged(par1, par2, par3);
            int l = 14737632;

            if(packedFGColour != 0)
            {
                l = packedFGColour;
            }
            else if(!this.enabled)
            {
                l = 10526880;
            }
            else if(this.hovered)
            {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);

            if(this.displayItem != null)
            {
            	this.drawItem(par1, this.x, this.y);
            }
        }
    }

	private void drawItem(Minecraft par1, int par2, int par3)
	{
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        par1.getRenderItem().renderItemIntoGUI(this.displayItem, par2 + 2, par3 + 2);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}

	public void setItem(ItemStack par1)
	{
		this.displayItem = par1;
	}

	public BlockSet getBlockSet()
	{
		if(this.displayItem == null)
		{
			return new BlockSet(Blocks.AIR, 0);
		}

		Block block = Block.getBlockFromItem(this.displayItem.getItem());
		int meta = this.displayItem.getItemDamage();
		return new BlockSet(block, meta);
	}
}