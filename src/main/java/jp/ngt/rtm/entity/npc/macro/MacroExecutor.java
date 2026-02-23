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

package jp.ngt.rtm.entity.npc.macro;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.npc.macro.TrainCommand.CommandType;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.TrainState;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import net.minecraft.world.World;

public class MacroExecutor
{
	private List<TrainCommand> commands = new ArrayList<TrainCommand>();
	private boolean executing;
	private long startTime;

	public MacroExecutor(String[] args)
	{
		for(String s : args)
		{
			TrainCommand command = TrainCommand.parse(s);
			if(command != null)
			{
				this.commands.add(command);
			}
		}
		this.executing = false;
	}

	public boolean start(World world)
	{
		if(this.executing)
		{
			return false;
		}
		else
		{
			this.executing = true;
			this.startTime = world.getWorldTime();
			return true;
		}
	}

	public boolean stop(World world)
	{
		if(this.executing)
		{
			this.executing = false;
			this.startTime = 0L;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean finished()
	{
		return this.commands.isEmpty();
	}

	public void tick(World world, EntityTrainBase train)
	{
		TrainCommand command = this.commands.get(0);
		long time = world.getWorldTime() - this.startTime;
		if(time >= command.time)
		{
			this.execCommand(train, command.type, command.parameter);
			this.commands.remove(0);
		}
	}

	private void execCommand(EntityTrainBase train, CommandType type, Object param)
	{
		try
		{
			switch(type)
			{
			case Notch:
				this.execNotch(train, Integer.valueOf(param.toString()));
				return;
			case Horn:
				this.execHorn(train);
				return;
			case Chime:
				this.execChime(train, null);
				return;
			case Door:
				this.execDoor(train, TrainState.valueOf(param.toString()));
				return;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void execNotch(EntityTrainBase train, int notch)
	{
		train.addNotch(train.getFirstPassenger(), notch);
	}

	private void execHorn(EntityTrainBase train)
	{
		TrainConfig cfg = train.getResourceState().getResourceSet().getConfig();
		RTMCore.proxy.playSound(train, cfg.sound_Horn, 1.0F, 1.0F);
	}

	private void execChime(EntityTrainBase train, String name)
	{
		RTMCore.proxy.playSound(train, name, 1.0F, 1.0F);
	}

	private void execDoor(EntityTrainBase train, TrainState state)
	{
		train.setVehicleState(TrainStateType.Door, state.data);
	}
}