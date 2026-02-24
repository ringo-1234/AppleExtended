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

package jp.ngt.ngtlib.block;

import jp.ngt.ngtlib.item.ItemArgHolderBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;

public class BlockArgHolder extends ItemArgHolderBase<BlockArgHolder>
{
	private IBlockState state;
	private IBlockAccess access;

	public IBlockState getBlockState()
	{
		return this.state;
	}

	public BlockArgHolder setBlockState(IBlockState par1)
	{
		this.state = par1;
		return this;
	}

	public IBlockAccess getBlockAccess()
	{
		return this.access;
	}

	public BlockArgHolder setBlockAccess(IBlockAccess par1)
	{
		this.access = par1;
		return this;
	}
}