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

package jp.ngt.rtm.msims;

public class TicketFactory {
    /**
     * 切符の名前
     */
    private String name;
    /**
     * 切符の種類
     */
    private TicketType type;
    /**
     * 営業キロ(単位km)
     */
    private float[] distance;
    /**
     * 運賃, 定額の場合は長さ1配列
     */
    private int[] fare;
    /**
     * 経由路線, 乗り放題なら乗車可能路線
     */
    private String[] routes;
}
