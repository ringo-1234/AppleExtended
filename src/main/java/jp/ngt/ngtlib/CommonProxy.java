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

package jp.ngt.ngtlib;

import java.io.File;

import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonProxy
{
	public boolean isServer()
	{
		return true;
	}

	public World getWorld()
	{
		return null;
	}

	public EntityPlayer getPlayer()
	{
		return null;
	}

	public File getMinecraftDirectory(String folder)
	{
		return NGTUtil.getServer().getFile(folder);
	}

	public String getUserName()
	{
		return "";
	}

	public void preInit(){}

	public void init(){}

	public void postInit(){}

	public void removeGuiWarning(){}

	public void breakBlock(World world, int x, int y, int z, int meta)
	{
		world.setBlockToAir(new BlockPos(x, y, z));
	}

	public void zoom(EntityPlayer player, int count){}

	public int getChunkLoadDistance()
	{
		return 256;
	}
}