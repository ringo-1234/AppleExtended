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
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.AABBInt;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EditFilterFillSurface extends EditFilterBase {
    @Override
    public void init(Config par) {
        super.init(par);
    }

    @Override
    public String getFilterName() {
        return "FillSurface";
    }

    @Override
    public boolean edit(final Editor editor) {
        AABBInt box = editor.getSelectBox();
        if (box != null) {
            editor.record(box);
            editor.repeat(box, (box2, index, rep, x, y, z) -> {
                World world = editor.getWorld();
                Block baseBlock = BlockUtil.getBlock(world, x, y - 1, z);
                Block fillBlock = editor.getEntity().getSlotBlock(0);
                int meta = editor.getEntity().getSlotBlockMetadata(0);
                boolean flag = world.canSeeSky(new BlockPos(x, y, z)) && (baseBlock != Blocks.AIR) && (baseBlock != fillBlock);
                if (flag) {
                    editor.setBlock(x, y, z, fillBlock, meta, true);
                }
            }, 1);
            return true;
        }
        return false;
    }
}