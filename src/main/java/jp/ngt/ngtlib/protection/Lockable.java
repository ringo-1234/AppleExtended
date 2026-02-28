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

package jp.ngt.ngtlib.protection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface Lockable {
    /**
     * int[3] or TileEntity or Entity
     */
    Object getTarget(World world, int x, int y, int z);

    boolean lock(EntityPlayer player, String code);

    boolean unlock(EntityPlayer player, String code);

    /**
     * 禁止動作
     *
     * @return 1:L, 2:R, 3:L&R
     *
     */
    int getProhibitedAction();
}