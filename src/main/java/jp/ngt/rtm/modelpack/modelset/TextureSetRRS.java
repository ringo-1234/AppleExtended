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

package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.cfg.RRSConfig;

public class TextureSetRRS extends TextureSetBase<RRSConfig>
{
	public TextureSetRRS()
	{
		super();
	}

	public TextureSetRRS(RRSConfig par1)
	{
		super(par1);
	}

	@Override
	public RRSConfig getDummyConfig()
	{
		RRSConfig dummy = new RRSConfig("");
		dummy.texture = "textures/rrs/rrs_01.png";
		return dummy;
	}
}