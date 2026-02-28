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

package jp.ngt.rtm.modelpack.cfg;


public abstract class TextureConfig extends ResourceConfig {
    /**
     * 使用する画像のパス
     */
    public String texture;
    /**
     * マイクラ内での大きさ
     */
    public float height, width, depth;

    @Override
    public String getName() {
        return this.texture;
    }

    public int getUCountInGui() {
        return 4;
    }

    public int getVCountInGui() {
        return 2;
    }
}