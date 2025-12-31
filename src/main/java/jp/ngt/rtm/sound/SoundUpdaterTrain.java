package jp.ngt.rtm.sound;

import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.EnumNotch;
import jp.ngt.rtm.entity.train.util.Formation;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundUpdaterTrain extends SoundUpdaterVehicle<EntityTrainBase>
{
    /**{sound1, bell}*/
    private MovingSoundEntity[] atsSound = new MovingSoundEntity[2];
    private int currentSignal;

    public SoundUpdaterTrain(SoundHandler par1, EntityTrainBase par2)
    {
        super(par1, par2);
    }

    @Override
    public void update()
    {
    	EntityTrainBase train = this.vehicle;
    	EntityBogie bogie = train.getBogie(train.getTrainDirection());
    	if(bogie == null){return;}

    	int signal = train.getSignal();
    	if(train.getSpeed() > 0.0F)
    	{
    		switch(signal)
    		{
    		case 1:
    			if(this.currentSignal != 1)
				{
					if(this.atsSound[0] != null){this.atsSound[0].stop();}
					if(this.atsSound[1] != null){this.atsSound[1].stop();}

					this.atsSound[0] = MovingSoundMaker.create(bogie, "rtm:sounds/train/ats.ogg", true);
					this.soundHandler.playSound(this.atsSound[0]);
					this.atsSound[1] = MovingSoundMaker.create(bogie, "rtm:sounds/train/ats_bell.ogg", true);
					this.soundHandler.playSound(this.atsSound[1]);
					this.currentSignal = 1;
				}
				break;

    		case -1:
    			if(this.currentSignal != -1)
    			{
    				if(this.atsSound[1] != null){this.atsSound[1].stop();}
					this.currentSignal = -1;
				}
				break;
    		}
    	}
    	else
    	{
    		if(signal != -1 && this.currentSignal != 0)
    		{
    			if(this.atsSound[0] != null){this.atsSound[0].stop();}
				if(this.atsSound[1] != null){this.atsSound[1].stop();}
    			this.currentSignal = 0;
    		}
    	}

    	super.update();
    }

    @Override
    protected String getSound(ModelSetVehicleBase modelset)
    {
    	float speed = this.vehicle.getSpeed();
    	if(speed > 0)
    	{
    		float acceleration = EnumNotch.getAcceleration(this.getNotch(), speed);

    		if(speed < ((TrainConfig)modelset.getConfig()).maxSpeed[0])
    		{
    			return acceleration > 0.0F ? modelset.getConfig().sound_S_A : modelset.getConfig().sound_D_S;
    		}
    		else
    		{
    			return acceleration > 0.0F ? modelset.getConfig().sound_Acceleration : modelset.getConfig().sound_Deceleration;
    		}
		}
    	return modelset.getConfig().sound_Stop;
    }

    @Override
    protected boolean changePitch()
    {
    	ModelSetTrain modelset = this.vehicle.getResourceState().getResourceSet();
    	float speed = this.vehicle.getSpeed();
    	return speed > 0 ? (speed < modelset.getConfig().maxSpeed[0] ? false : true) : false;
    }

    public float getMaxSpeed()
    {
    	return this.vehicle.getResourceState().getResourceSet().getConfig().maxSpeed[4] * 72.0F;
    }

    public int getNotch()
    {
    	Formation formation = this.vehicle.getFormation();
    	return formation == null ? 0 : formation.getNotch();
    }

    public byte getState(int id)
    {
    	return this.vehicle.getVehicleState(TrainStateType.get(id));
    }

    public boolean isComplessorActive()
    {
    	return this.vehicle.complessorActive;
    }

    public int complessorCount()
    {
    	return this.vehicle.brakeAirCount - EntityTrainBase.MIN_AIR_COUNT;
    }
}