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
import jp.ngt.mcte.editor.WorldSnapshot;
import jp.ngt.ngtlib.math.AABBInt;

public class EditFilterPaste extends EditFilterBase
{
	@Override
	public void init(Config par)
	{
		super.init(par);
		par.addBoolean("IgnoreAir", false);
	}

	@Override
	public String getFilterName()
	{
		return "Paste";
	}

	/*@Override
	public String getCfgName()
	{
		return "Copy";
	}*/

	@Override
	public boolean edit(Editor editor)
	{
		boolean ignoreAir = this.getCfg().getBoolean("IgnoreAir");
		StringBuilder sb = new StringBuilder();
		if(ignoreAir)
		{
			sb.append(WorldSnapshot.IGNORE_AIR);
		}

		AABBInt box = editor.getPasteBox();
		if(box != null)
		{
			editor.record(box);
			editor.paste(box, sb.toString());
			return true;
		}
		return false;
	}
}