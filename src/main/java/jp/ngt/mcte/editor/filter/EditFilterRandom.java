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

package jp.ngt.mcte.editor.filter;

import jp.ngt.mcte.editor.Editor;
import jp.ngt.ngtlib.math.AABBInt;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class EditFilterRandom extends EditFilterBase
{
	@Override
	public void init(Config par)
	{
		super.init(par);
		par.addInt("Denominator", 2, 1, 65536);
	}

	@Override
	public String getFilterName()
	{
		return "Random";
	}

	@Override
	public boolean edit(final Editor editor)
	{
		AABBInt box = editor.getSelectBox();
		if(box != null)
		{
			final int n = this.getCfg().getInt("Denominator");
			editor.record(box);
			editor.repeat(box, (box2, index, rep, x, y, z)->{
				World world = editor.getWorld();
				Block fillBlock = editor.getEntity().getSlotBlock(0);
				int meta = editor.getEntity().getSlotBlockMetadata(0);
				if(world.rand.nextInt(n) == 0)
				{
					editor.setBlock(x, y, z, fillBlock, meta, true);
				}
			}, 1);
			return true;
		}
		return false;
	}
}