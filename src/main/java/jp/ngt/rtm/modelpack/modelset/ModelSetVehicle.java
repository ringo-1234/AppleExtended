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

import jp.ngt.rtm.modelpack.cfg.VehicleConfig;

public class ModelSetVehicle extends ModelSetVehicleBase<VehicleConfig>
{
	public ModelSetVehicle()
	{
		super();
	}

	public ModelSetVehicle(VehicleConfig par1)
	{
		super(par1);
	}

	@Override
	public VehicleConfig getDummyConfig()
	{
		return VehicleConfig.getDummy();
	}
}