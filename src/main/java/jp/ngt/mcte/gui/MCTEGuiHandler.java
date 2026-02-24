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

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.editor.EntityEditor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MCTEGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == MCTE.guiIdEditor)
		{
			Entity entity = world.getEntityByID(x);
			if(entity != null && entity instanceof EntityEditor)
			{
				return new ContainerEditor((EntityEditor)entity);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == MCTE.guiIdEditor)
		{
			Entity entity = world.getEntityByID(x);
			if(entity != null && entity instanceof EntityEditor)
			{
				return new GuiEditor((EntityEditor)entity);
			}
		}
		else if(ID == MCTE.guiIdGenerator)
		{
			return new GuiGenerator(world, x, y, z);
		}
		else if(ID == MCTE.guiIdPainter)
		{
			return new GuiPainter(player);
		}
		else if(ID == MCTE.guiIdItemMiniature)
		{
			return new GuiItemMiniature(player);
		}

		return null;
	}
}