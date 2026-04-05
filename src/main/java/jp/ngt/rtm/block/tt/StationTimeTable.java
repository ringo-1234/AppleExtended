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

package jp.ngt.rtm.block.tt;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.block.tt.TimeTable.TTEntry;

public class StationTimeTable {
    public final TimeTable timeTable;
    public final String station;
    public final byte track;

    private final int rowIndex;

    public StationTimeTable(String pTT, String pStation, int pTrack) {
        this.timeTable = TimeTableManager.INSTANCE.getTimeTable(pTT);
        this.station = pStation;
        this.track = (byte) pTrack;

        this.rowIndex = this.timeTable.stationAxis.get(this.station);
    }

    /**
     * @param track -1で未指定
     * @return 該当列車ない場合はTTの列数
     *
     */
    public int getMatchTrainIndex(int track) {
        int time;
        if (this.timeTable.useRealTime) {
            time = (int) ((System.currentTimeMillis() / 1000L) % (24 * 60 * 60));
        } else {
            int mcTime = 0;
            if (!NGTUtil.isServer()) {
                mcTime = (int) ((NGTUtil.getClientWorld().getWorldTime() + 6000) % 24000L);
            }
            time = mcTime * (24 * 60 * 60) / 24000;
        }

        for (int i = 0; i < this.timeTable.ttData[this.rowIndex].length; ++i) {
            TTEntry entry = this.timeTable.ttData[this.rowIndex][i];
            if (time <= entry.departureTime && (track < 0 || track == entry.trackNum)) {
                return i;
            }
        }

        return this.getSize();
    }

    public int getSize() {
        return this.timeTable.ttData[this.rowIndex].length;
    }

    public String getData(int trainId, int col) {
        return this.timeTable.ttData[this.rowIndex][trainId].data[col];
    }
}