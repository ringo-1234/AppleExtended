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

package jp.ngt.ngtlib.math;

public interface ILine {
    /**
     * @param par1 分割数
     * @param par2 進める割合
     * @return double[]{x, y}
     */
    double[] getPoint(int par1, int par2);

    /**
     * @param par1 分割数
     * @param par2 x
     * @param par3 z
     * @return 進める数
     */
    int getNearlestPoint(int par1, double par2, double par3);

    /**
     * @param par1 分割数
     * @param par2 進める割合
     * @return 傾き（ラジアン）
     */
    double getSlope(int par1, int par2);

    /**
     * @return 線分の長さ
     */
    double getLength();

    /**
     * 曲線長で正規化した位置(0.0～1.0)から座標を取得する
     */
    default double[] getPoint(double ratio) {
        double value = Math.max(0.0D, Math.min(1.0D, ratio));
        int split = 1000000;
        return this.getPoint(split, (int) Math.round(value * (double) split));
    }

    /**
     * 曲線長で正規化した位置(0.0～1.0)から傾きを取得する
     */
    default double getSlope(double ratio) {
        double value = Math.max(0.0D, Math.min(1.0D, ratio));
        int split = 1000000;
        return this.getSlope(split, (int) Math.round(value * (double) split));
    }
}