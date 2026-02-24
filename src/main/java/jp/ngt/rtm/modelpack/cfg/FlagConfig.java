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

public class FlagConfig extends TextureConfig
{
	/**縦横の解像度*/
	public int resolutionV, resolutionU;
	public float poleLength;

	@Override
	public void init()
	{
		super.init();

		if(this.resolutionU <= 0)
		{
			this.resolutionU = 24;
		}

		if(this.resolutionV <= 0)
		{
			this.resolutionV = 16;
		}

		if(this.poleLength <= 0.0F)
		{
			this.poleLength = 1.0F;
		}
	}
}
