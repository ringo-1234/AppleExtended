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

public enum MarkerState {
    DISTANCE,
    GRID,
    LINE1,
    LINE2,
    ANCHOR21,
    FIT_NEIGHBOR,
    ;

    private MarkerState() {
        ;
    }

    private int bitMask() {
        return 1 << this.ordinal();
    }

    public boolean get(int data) {
        int mask = this.bitMask();
        return (data & mask) > 0;
    }

    public int set(int data, boolean state) {
        int mask = this.bitMask();
        if (state) {
            return data | mask;
        } else {
            return (data | mask) - mask;
        }
    }

    public int flip(int data) {
        int mask = this.bitMask();
        return data ^ mask;
    }
}