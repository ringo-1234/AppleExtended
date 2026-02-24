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

import jp.ngt.mcte.editor.filter.Config;
import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementInt extends GuiTextFieldCustom implements IGuiElement
{
	private final Config cfg;
	private final String paramName;

	public GuiElementInt(int id, int x, int y, int w, int h, Config cfg, String key)
	{
		super(id, NGTUtilClient.getMinecraft().fontRenderer, x, y, w, h, null);
		this.cfg = cfg;
		this.paramName = key;
		this.setText(String.valueOf(cfg.getInt(key)));
	}

	@Override
	public void setFocused(boolean par1)
    {
		super.setFocused(par1);
		if(!par1)
		{
			this.updateValue();
		}
    }

	private void updateValue()
	{
		try
		{
			int value = Integer.valueOf(this.getText());
			this.cfg.setInt(this.paramName, value);
			this.setText(String.valueOf(this.cfg.getInt(this.paramName)));
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void init(GuiScreenCustom gui)
	{
		gui.getTextFields().add(this);
	}

	@Override
	public void setYPos(int y)
	{
		this.y = y;
	}
}