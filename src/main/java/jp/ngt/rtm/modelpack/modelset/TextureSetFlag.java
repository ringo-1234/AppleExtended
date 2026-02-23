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

import jp.ngt.rtm.modelpack.cfg.FlagConfig;

public class TextureSetFlag extends TextureSetBase<FlagConfig>
{
	public TextureSetFlag()
	{
		super();
	}

	public TextureSetFlag(FlagConfig par1)
	{
		super(par1);
	}

	@Override
	public FlagConfig getDummyConfig()
	{
		FlagConfig dummy = new FlagConfig();
		dummy.texture = "textures/flag/flag_RTM3Anniversary.png";
		dummy.width = 1.0F;
		dummy.height = 1.0F;
		return dummy;
	}
}