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

package jp.ngt.rtm.render;

import jp.ngt.rtm.electric.TileEntitySignal;
import jp.ngt.rtm.modelpack.modelset.ModelSetSignal;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SignalPartsRenderer extends TileEntityPartsRenderer<ModelSetSignal>
{
	public SignalPartsRenderer(String... par1)
	{
		super(par1);
	}

	public int getTick(TileEntity par1)
	{
		return par1 == null ? 0 : ((TileEntitySignal)par1).tick;
	}

	public float getBlockDirection(TileEntity par1)
	{
		if(par1 == null){return 0.0F;}
		return ((TileEntitySignal)par1).getBlockDirection();
	}

	public float getRotation(TileEntity par1)
	{
		if(par1 == null){return 0.0F;}
		return ((TileEntitySignal)par1).getRotation();
	}

	public int getSignal(TileEntity par1)
	{
		if(par1 == null){return 0;}
		return ((TileEntitySignal)par1).getSignal();
	}

	public Block getBlock(TileEntity par1)
	{
		if(par1 == null){return Blocks.AIR;}
		return ((TileEntitySignal)par1).getRenderBlock();
	}

	public boolean isOpaqueCube(TileEntity par1)
	{
		if(par1 == null){return true;}
		return par1.getWorld().getBlockState(par1.getPos()).isOpaqueCube();
	}
}