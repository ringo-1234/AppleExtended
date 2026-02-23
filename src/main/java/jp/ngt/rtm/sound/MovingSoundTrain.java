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

package jp.ngt.rtm.sound;

import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundTrain extends MovingSoundEntity
{
	public boolean changePitch;

	protected MovingSoundTrain(EntityTrainBase train, String sound, boolean par3)
	{
		super(train, sound, par3);
	}

	@Override
	public void update()
	{
		super.update();

		if(this.changePitch)
		{
			EntityTrainBase train = (EntityTrainBase)this.entity;
			ModelSetTrain modelset = train.getResourceState().getResourceSet();
			float f0 = modelset.getConfig().maxSpeed[0];
			float f1 = (Math.abs(train.getSpeed()) - f0) / (modelset.getConfig().maxSpeed[4] - f0) + 1.0F;
			this.pitch = f1;
		}
	}
}