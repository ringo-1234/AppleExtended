package jp.ngt.rtm.event;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.entity.vehicle.EntityVehicle;
import net.minecraft.entity.player.EntityPlayer;

public final class RTMKeyHandlerServer
{
	public static final RTMKeyHandlerServer INSTANCE = new RTMKeyHandlerServer();

	private RTMKeyHandlerServer(){}

	public void onKeyDown(EntityPlayer player, byte keyCode, String sound)
	{
		switch(keyCode)
		{
		case RTMCore.KEY_JUMP:
			this.setVehicleState(player, 1);
			break;
		case RTMCore.KEY_SNEAK:
			this.setVehicleState(player, -1);
			break;
		case RTMCore.KEY_Horn:
			this.playSound(player, sound, 10000.0F);
			break;
		case RTMCore.KEY_Chime:
			this.playSound(player, sound, 1.0F);
			break;
		case RTMCore.KEY_ControlPanel:
			player.openGui(RTMCore.instance, RTMCore.instance.guiIdTrainControlPanel, player.world, player.getRidingEntity().getEntityId(), 0, 0);
			break;
		case RTMCore.KEY_Fire:
			if(player.isRiding() && player.getRidingEntity() instanceof EntityArtillery)
			{
				((EntityArtillery)player.getRidingEntity()).onFireKeyDown(player);
			}
			break;
		case RTMCore.KEY_ATS:
			this.setATS(player);
			break;
		}
	}

	private void setVehicleState(EntityPlayer player, int updown)
	{
		if(player.isRiding() && player.getRidingEntity() instanceof EntityVehicle)
		{
			EntityVehicle vehicle = (EntityVehicle)player.getRidingEntity();
			vehicle.setUpDown(updown);
		}
	}

	private void playSound(EntityPlayer player, String sound, float vol)
	{
		EntityTrainBase train = this.getRidingTrain(player);
		if(train != null && sound != null)
		{
			RTMCore.proxy.playSound(train, sound, vol, 1.0F);
		}
	}

	private void setATS(EntityPlayer player)
	{
		EntityTrainBase train = this.getRidingTrain(player);
		if(train != null)
		{
			int signal = train.getSignal();
			if(signal == 1)
			{
				train.setSignal2(-1);
			}
			else if(signal == -1 && train.getNotch() == -8)
			{
				train.setSignal2(0);
			}
		}
	}

	private EntityTrainBase getRidingTrain(EntityPlayer player)
	{
		if(player.isRiding() && player.getRidingEntity() instanceof EntityTrainBase)
		{
			return (EntityTrainBase)player.getRidingEntity();
		}
		return null;
	}
}