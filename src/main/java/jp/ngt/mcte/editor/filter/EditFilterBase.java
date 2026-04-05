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

package jp.ngt.mcte.editor.filter;

import jp.ngt.mcte.editor.Editor;
import jp.ngt.ngtlib.io.NGTFileLoader;

import java.io.File;

public abstract class EditFilterBase {
    protected Config cfg;

    public EditFilterBase() {
    }

    public void init(Config par) {
        this.cfg = par;
    }

    public void save() {
        this.cfg.save(this.getCfgFile());
    }

    public File getCfgFile() {
        return new File(NGTFileLoader.getModsDir().get(0), FilterManager.FILTER_PATH + this.getFilterName() + ".cfg");
    }

    public Config getCfg() {
        return this.cfg;
    }

    public abstract String getFilterName();

	/*@Deprecated
	public String getCfgName()
	{
		return this.getFilterName();
	}*/

    public abstract boolean edit(Editor editor);
}