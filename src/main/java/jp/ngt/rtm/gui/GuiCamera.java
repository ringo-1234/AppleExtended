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

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCamera extends GuiScreenCustom
{
	public GuiCamera(EntityPlayer player)
	{
		super();
	}

	//RenderGrobal.makeEntityOutlineShader見よ
	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        int halfW = this.width / 2;
        this.drawCenteredString(this.fontRenderer, "R" , halfW - 90, 25, 0xFF0000);
        this.drawCenteredString(this.fontRenderer, "G" , halfW - 90, 45, 0x00FF00);
        this.drawCenteredString(this.fontRenderer, "B" , halfW - 90, 65, 0x0000FF);
        this.drawCenteredString(this.fontRenderer, "Hex" , halfW - 90, 90, 0xffffff);
        this.drawCenteredString(this.fontRenderer, "Alpha" , halfW - 95, 115, 0xffffff);
        this.drawCenteredString(this.fontRenderer, "Radius" , halfW + 12, 65, 0xffffff);

        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(halfW + 8, 20, this.zLevel);
        tessellator.addVertex(halfW + 8, 52, this.zLevel);
        tessellator.addVertex(halfW + 40, 52, this.zLevel);
        tessellator.addVertex(halfW + 40, 20, this.zLevel);
        tessellator.draw();
    }
}