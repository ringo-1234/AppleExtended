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


public class SignboardConfig extends TextureConfig
{
	/**0:裏表同じ, 1:裏表別, 2:裏テクスチャなし(側面の色と同じ)*/
	public int backTexture;
	/**フレーム数*/
	public int frame;
	/**アニメーションの間隔*/
	public int animationCycle;
	/**側面の色*/
	public int color;
	/**
	 * 明るさ<br>
	 *  0~15:通常<br>
	 *  -15~-1:RSオン時のみ明るくなる<br>
	 *  -16:点滅(ランダム)<br>
	 */
	public int lightValue;

	@Override
	public void init()
	{
		super.init();

		this.texture = fixName(this.texture);

		if(this.frame <= 0)
		{
			this.frame = 1;
		}

		if(this.animationCycle <= 0)
		{
			this.animationCycle = 1;
		}

		if(this.color < 0)
		{
			this.color = 0x101010;
		}
	}

	public static String fixName(String par1)
	{
		if(!par1.contains("textures"))
        {
        	return "textures/signboard/" + par1 + ".png";
        }
		return par1;
	}
}