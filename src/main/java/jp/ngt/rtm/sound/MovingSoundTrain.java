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
			float f1 = (train.getSpeed() - f0) / (modelset.getConfig().maxSpeed[4] - f0) + 1.0F;//0.5~2.0
			this.pitch = f1;
		}
	}
}