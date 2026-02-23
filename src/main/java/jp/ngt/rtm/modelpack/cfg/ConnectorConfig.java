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


public class ConnectorConfig extends ModelConfig implements IConfigWithType
{
	private String name;
	public ModelSource model;
	/**Relay, Input, Output*/
	public String connectorType;
	/**{x, y, z}*/
	public float[] wirePos;

	@Override
	public void init()
	{
		super.init();

		if(this.wirePos == null)
		{
			this.wirePos = new float[]{0.0F, 0.0F, 0.0F};
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
		return this.connectorType;
	}

	public static ConnectorConfig getDummy()
	{
		ConnectorConfig cfg = new ConnectorConfig();
		cfg.name = "dummy";
		cfg.connectorType = "N";
		cfg.init();
		return cfg;
	}
}