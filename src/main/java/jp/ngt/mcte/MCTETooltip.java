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

package jp.ngt.mcte;

import jp.ngt.ngtlib.util.Usage;

public final class MCTETooltip
{
	public static void init()
	{
		Usage.INSTANCE.add(MCTE.portIn,  -1, "usage.block.port_in");
		Usage.INSTANCE.add(MCTE.portOut, -1, "usage.block.port_out");
		String[] usageEditor = new String[9];
		for(int i= 0; i < usageEditor.length; ++i)
		{
			usageEditor[i] = "usage.item.editor." + i;
		}
		Usage.INSTANCE.add(MCTE.editor,    -1, usageEditor);
		Usage.INSTANCE.add(MCTE.generator, -1, "usage.item.generator");
		Usage.INSTANCE.add(MCTE.painter,   -1, "usage.item.painter");
		//Usage.INSTANCE.add(MCTE.itemMiniature, -1, "usage.item.istlobj.fluorescent");
	}
}