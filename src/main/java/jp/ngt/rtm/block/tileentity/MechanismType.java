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

package jp.ngt.rtm.block.tileentity;

public enum MechanismType {
    POWER(true),
    TRANSMISSION(true),
    GEAR(false),
    PULLEY(false);

    public final boolean useRS;

    private MechanismType(boolean par1) {
        this.useRS = par1;
    }
}
