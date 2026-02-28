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

package jp.ngt.rtm.modelpack.init;

/**
 * 広告ボタンのjsonシリアライズ用
 */
public class Advertisement {
    /**
     * 表示画像(サイズは1024x576)
     */
    public String picture;
    /**
     * 作者名
     */
    public String author;
    /**
     * 画像クリック時に飛ぶURL
     */
    public String url;
}
