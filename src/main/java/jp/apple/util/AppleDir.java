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

package jp.apple.util;

import java.io.File;

public class AppleDir {

    public static File appleLibDir;

    public static void init(File mcRoot) {
        appleLibDir = new File(mcRoot, "AppleLib");
        if (!appleLibDir.exists()) {
            appleLibDir.mkdirs();
        }
    }
}
