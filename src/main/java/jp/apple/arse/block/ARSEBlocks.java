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

package jp.apple.arse.block;

import net.minecraft.block.Block;

public class ARSEBlocks {
    public static Block sounder;
    public static Block soundRemover;

    public void preInit() {
        sounder = new Sounder();
        soundRemover = new SoundRemover();
    }
}
