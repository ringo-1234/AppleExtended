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

package jp.apple.rail.util;

import jp.ngt.rtm.rail.util.RailPosition;


public final class RailSection {
    private final double startRatio;
    private final double endRatio;
    private final RailPosition startRP;
    private final RailPosition endRP;

    public RailSection(double startRatio, double endRatio, RailPosition startRP, RailPosition endRP) {
        this.startRatio = startRatio;
        this.endRatio = endRatio;
        this.startRP = startRP;
        this.endRP = endRP;
    }

    public double getStartRatio() {
        return startRatio;
    }

    public double getEndRatio() {
        return endRatio;
    }

    public RailPosition getStartRP() {
        return startRP;
    }

    public RailPosition getEndRP() {
        return endRP;
    }
}