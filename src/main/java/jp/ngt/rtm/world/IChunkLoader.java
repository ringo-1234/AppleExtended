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

package jp.ngt.rtm.world;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoader {
    /**
     * チャンクローダー機能が有効かどうか
     */
    boolean isChunkLoaderEnable();

    void forceChunkLoading(int chunkX, int chunkZ);

    void forceChunkLoading();

    void setChunkTicket(Ticket ticket);
}