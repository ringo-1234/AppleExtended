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

public class MachineConfig extends ModelConfig implements IConfigWithType
{
	/**名前(重複不可)*/
	private String name;
	/**モデル*/
	public ModelSource model;
	public String machineType;

	public String sound_OnActivate;
	public String sound_Running;

	public boolean rotateByMetadata;
	public boolean followRailAngle;//ACTなどをカントに追従
	public int[] brightness;

	@Override
	public void init()
	{
		super.init();

		this.sound_OnActivate = this.fixSoundPath(this.sound_OnActivate);
		this.sound_Running = this.fixSoundPath(this.sound_Running);

		if(this.brightness == null || this.brightness.length < 2)
		{
			this.brightness = new int[]{0, 0};
		}
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getSubType()
	{
		return this.machineType;
	}

	public static MachineConfig getDummy()
	{
		MachineConfig cfg = new MachineConfig();
		cfg.name = "dummy";
		cfg.machineType = "N";
		cfg.init();
		return cfg;
	}
}