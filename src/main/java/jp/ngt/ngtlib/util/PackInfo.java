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

package jp.ngt.ngtlib.util;

public class PackInfo {
    public String name;
    public String homepage;
    public String updateURL;
    public String version;

    public PackInfo(String par1, String par2, String par3, String par4) {
        this.name = par1;
        this.homepage = par2;
        this.updateURL = par3;
        this.version = par4;
    }
}