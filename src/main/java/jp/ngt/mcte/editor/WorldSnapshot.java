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

package jp.ngt.mcte.editor;

import jp.ngt.mcte.editor.filter.Repeatable;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.AABBInt;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

/**
 * World上のBlockとEntityを保存
 */
public class WorldSnapshot {
    public static final String IGNORE_AIR = "IgnoreAir";
    public static final String IGNORE_WATER = "IgnoreWater";

    private final List<BlockSet> blockList = new ArrayList<BlockSet>();
    //Entityを回転に対応させてない
    private final List<Entity> entityList = new ArrayList<Entity>();
    private final AABBInt origBox;
    /**
     * インポートデータはfalse
     */
    private final boolean hasOrigPos;

    public WorldSnapshot(NGTObject ngto) {
        this.blockList.addAll(ngto.blockList);
        this.origBox = new AABBInt(ngto.origX, ngto.origY, ngto.origZ,
                ngto.origX + ngto.xSize, ngto.origY + ngto.ySize, ngto.origZ + ngto.zSize);
        this.hasOrigPos = false;
    }

    public WorldSnapshot(List<BlockSet> list, AABBInt box) {
        this.blockList.addAll(list);
        this.origBox = box;
        this.hasOrigPos = false;
    }

    public WorldSnapshot(Editor editor, AABBInt box, String options) {
        this.save(editor, box, options);
        this.origBox = box;
        this.hasOrigPos = true;
    }

    private void save(final Editor editor, AABBInt box, final String options) {
        NGTLog.startTimer();
        editor.repeat(box, (box2, index, rep, x, y, z) -> {
            BlockSet blockSet = editor.getBlockSet(x, y, z);
            if (options.contains(IGNORE_WATER)) {
                IBlockState state = blockSet.toBlockState();
                if (state.getMaterial().isLiquid()) {
                    blockSet = BlockSet.AIR;
                }
            }
            WorldSnapshot.this.blockList.add(blockSet);
        }, 1);

        List list = editor.getWorld().getEntitiesWithinAABBExcludingEntity(
                editor.getEntity(), new AxisAlignedBB(
                        (double) box.minX, (double) box.minY, (double) box.minZ,
                        (double) box.maxX, (double) box.maxY, (double) box.maxZ));
        this.entityList.addAll(list);
        NGTLog.stopTimer("save snapshot");//負荷低、数msで終わる
    }

    /**
     * ブロックの配置を復元
     */
    public void restore(Editor editor) {
        if (this.hasOrigPos) {
            for (BlockSet blockSet : this.blockList) {
                editor.setBlock(blockSet.x, blockSet.y, blockSet.z, blockSet, false);
            }
            editor.updateBlocks(this.origBox);
        }
    }

    public void setBlocks(final Editor editor, int x, int y, int z, final String options) {
        Repeatable repeater = (rbox, index, rep, rx, ry, rz) -> {
            BlockSet blockSet = WorldSnapshot.this.blockList.get(index);
            if (options.contains(IGNORE_AIR) && blockSet.block == Blocks.AIR) {
                return;
            }
            editor.setBlock(rx, ry, rz, blockSet, true);
        };
        AABBInt box = new AABBInt(x, y, z,
                x + this.origBox.sizeX(), y + this.origBox.sizeY(), z + this.origBox.sizeZ());
        editor.repeat(box, repeater, 2);//松明やレールが壊れないように2回設置
        editor.updateBlocks(box);
    }

    public NGTObject convertNGTO() {
        NBTTagList tagList = NGTWorld.writeEntitiesToNBT(this.entityList);
        return NGTObject.createNGTO(this.blockList, tagList,
                this.origBox.sizeX(), this.origBox.sizeY(), this.origBox.sizeZ(),
                this.origBox.minX, this.origBox.minY, this.origBox.minZ);
    }

    public int getSize() {
        return this.blockList.size();
    }

    public List<BlockSet> getBlocks() {
        return this.blockList;
    }

    public List<Entity> getEntities() {
        return this.entityList;
    }
}