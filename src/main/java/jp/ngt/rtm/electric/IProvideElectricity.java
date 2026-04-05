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

package jp.ngt.rtm.electric;

public interface IProvideElectricity {
    /**
     * 送信する信号の取得
     */
    int getElectricity();

    /**
     * 信号を受信したときの挙動
     */
    void setElectricity(int x, int y, int z, int level);
}