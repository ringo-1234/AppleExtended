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

import jp.ngt.rtm.block.tileentity.MechanismType;

public class MechanismConfig extends ModelConfig
{
	/**名前(重複不可)*/
	private String name;
	/**モデル*/
	public ModelSource model;

	public MechanismType type;
	/**[yp, zp, xn, zn, xp]*/
	public float[] transmissionRatioON, transmissionRatioOFF;
	/**rpm*/
	public float maxSpeed;
	/**rpmt*/
	public float acceleration;
	/**m*/
	public float radius;
	/**歯数*/
	public int teethCount;
	/**伝達比がON/OFFで異なる場合は自動でtrue*/
	public boolean useRedstonePower;

	@Override
	public void init()
	{
		super.init();

		if(this.transmissionRatioON == null || this.transmissionRatioON.length != 5)
		{
			this.transmissionRatioON = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F};
		}

		if(this.transmissionRatioOFF == null || this.transmissionRatioOFF.length != 5)
		{
			this.transmissionRatioOFF = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F};
		}

		if(!this.useRedstonePower)
		{
			for(int i = 0; i < this.transmissionRatioOFF.length; ++i)
			{
				this.useRedstonePower |= (this.transmissionRatioOFF[i] != this.transmissionRatioON[i]);
			}
		}
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public static MechanismConfig getDummy()
	{
		MechanismConfig cfg = new MechanismConfig();
		cfg.name = "dummy";
		cfg.type = MechanismType.GEAR;
		cfg.init();
		return cfg;
	}
}
