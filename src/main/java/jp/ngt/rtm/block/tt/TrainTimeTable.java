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

public class TrainTimeTable {
    public final TimeTable timeTable;
    public final String train;

    /**
     * -1ならこのオブジェクトはダミー
     */
    public final int colIndex;

    public TrainTimeTable(String pTrain) {
        this.timeTable = TimeTableManager.INSTANCE.getTimeTableByTrainName(pTrain);
        this.train = pTrain;

        Integer i = this.timeTable.trainAxis.get(this.train);
        this.colIndex = i == null ? -1 : i;
    }
}
