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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiFreightCar extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation("textures/gui/container/hopper.png");
    private IInventory inventoryPlayer;
    private IInventory inventory;

	public GuiFreightCar(EntityPlayer player, IInventory par2Inv)
	{
		super(new ContainerFreightCar(player, par2Inv));
		this.inventoryPlayer = player.inventory;
		this.inventory = par2Inv;
		this.allowUserInput = false;
        this.ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRenderer.drawString(this.inventory.hasCustomName() ? this.inventory.getName() : I18n.format(this.inventory.getName(), new Object[0]), 8, 6, 4210752);
        this.fontRenderer.drawString(this.inventoryPlayer.hasCustomName() ? this.inventoryPlayer.getName() : I18n.format(this.inventoryPlayer.getName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

	@Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
		this.drawDefaultBackground();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}