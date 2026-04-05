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

package jp.ngt.rtm;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;

public class RTMMaterial extends Material {
    public static final Material RAIL_BASE = (new RTMMaterial(MapColor.OBSIDIAN) {
        @Override
        public boolean isOpaque() {
            return false;
        }
    }).setRequiresTool();
    public static final Material fireproof = (new RTMMaterial(MapColor.OBSIDIAN)).setRequiresTool();
    public static final Material melted = (new MaterialLiquid(MapColor.TNT));

    public RTMMaterial(MapColor color) {
        super(color);
    }
}