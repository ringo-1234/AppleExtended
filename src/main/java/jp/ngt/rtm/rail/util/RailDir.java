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

package jp.ngt.rtm.rail.util;

public enum RailDir {
    /**
     * =-1
     */
    RIGHT(-1),
    /**
     * =1
     */
    LEFT(1),
    /**
     * =0
     */
    NONE(0);

    public final byte id;

    private RailDir(int par1) {
        this.id = (byte) par1;
    }

    /**
     * 向きを反転
     */
    public RailDir invert() {
        return (this == RIGHT) ? LEFT : (this == LEFT) ? RIGHT : NONE;
    }
}