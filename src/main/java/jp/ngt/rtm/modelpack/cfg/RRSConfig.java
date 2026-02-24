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

package jp.ngt.rtm.modelpack.cfg;


public class RRSConfig extends TextureConfig
{
	@Override
	public void init()
	{
		super.init();

		this.width = 0.5F;
		this.height = 0.5F;
	}

	public RRSConfig(String name)
	{
		this.texture = fixName(name);
	}

	public static String fixName(String par1)
	{
		if(!par1.contains("textures"))
        {
        	return "textures/rrs/" + par1;
        }
		return par1;
	}

	@Override
	public int getUCountInGui()
	{
		return 8;
	}

	@Override
	public int getVCountInGui()
	{
		return 4;
	}
}