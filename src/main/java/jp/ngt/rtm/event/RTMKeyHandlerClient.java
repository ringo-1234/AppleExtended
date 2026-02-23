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

package jp.ngt.rtm.event;

import org.lwjgl.input.Keyboard;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.npc.macro.MacroRecorder;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.entity.train.util.EnumNotch;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.entity.vehicle.EntityPlane;
import jp.ngt.rtm.entity.vehicle.EntityVehicle;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import jp.ngt.rtm.network.PacketRTMKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RTMKeyHandlerClient
{
	private static final String CATG_RTM = "key.rtm.category";
	public static final RTMKeyHandlerClient INSTANCE = new RTMKeyHandlerClient();
	public static final KeyBinding KEY_HORN =  new KeyBinding("key.rtm.horn",  Keyboard.KEY_P, CATG_RTM);
	public static final KeyBinding KEY_CHIME = new KeyBinding("key.rtm.chime", Keyboard.KEY_I, CATG_RTM);
	public static final KeyBinding KEY_ATS =   new KeyBinding("key.rtm.ats",   Keyboard.KEY_COMMA, CATG_RTM);
	public static final KeyBinding KEY_EB =    new KeyBinding("key.rtm.eb",    Keyboard.KEY_5, CATG_RTM);
	public static final KeyBinding KEY_CHIME_NEXT = new KeyBinding("key.rtm.chime_next", Keyboard.KEY_RIGHT, CATG_RTM);
	public static final KeyBinding KEY_CHIME_PREV = new KeyBinding("key.rtm.chime_prev", Keyboard.KEY_LEFT, CATG_RTM);

	private RTMKeyHandlerClient(){}

	public static void init()
	{
		ClientRegistry.registerKeyBinding(KEY_HORN);
		ClientRegistry.registerKeyBinding(KEY_CHIME);
		ClientRegistry.registerKeyBinding(KEY_ATS);
		ClientRegistry.registerKeyBinding(KEY_EB);
		ClientRegistry.registerKeyBinding(KEY_CHIME_NEXT);
		ClientRegistry.registerKeyBinding(KEY_CHIME_PREV);
	}

	public void onTickStart()
	{
		Minecraft mc = NGTUtilClient.getMinecraft();
		EntityPlayer player = mc.player;
		if(mc.gameSettings.keyBindJump.isKeyDown())
		{
			if(player.isRiding() && player.getRidingEntity() instanceof EntityVehicle)
			{
				this.sendKeyToServer(RTMCore.KEY_JUMP, "");
			}
		}
		else if(mc.gameSettings.keyBindSneak.isKeyDown())
		{
			if(player.isRiding() && player.getRidingEntity() instanceof EntityPlane)
			{
				if(!player.getRidingEntity().onGround)
				{
					this.sendKeyToServer(RTMCore.KEY_SNEAK, "");
				}
			}
		}
	}

	public void onTickEnd()
	{
	}

	@SubscribeEvent
	public void onInputUpdateEvent(InputUpdateEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		Entity ridingEntity = player.getRidingEntity();
		if (ridingEntity instanceof EntityPlane && ((EntityPlane)ridingEntity).disableUnmount())
		{
			event.getMovementInput().sneak = false;
		}
	}

	@SubscribeEvent
	public void keyDown(InputEvent event)
	{
		Minecraft mc = NGTUtilClient.getMinecraft();
		EntityPlayer player = mc.player;
		Entity riding = player.getRidingEntity();

		if(mc.gameSettings.keyBindBack.isPressed())
		{
			if(player.isRiding() && riding instanceof EntityTrainBase)
			{
				((EntityTrainBase)riding).syncNotch(1);
			}
		}
		else if(mc.gameSettings.keyBindForward.isPressed())
		{
			if(player.isRiding() && riding instanceof EntityTrainBase)
			{
				((EntityTrainBase)riding).syncNotch(-1);
			}
		}
		else if(mc.gameSettings.keyBindJump.isKeyDown())
		{
		}
		else if(mc.gameSettings.keyBindSneak.isKeyDown())
		{
		}
		else if(KEY_HORN.isPressed())
		{
			if(player.isRiding())
			{
				if(riding instanceof EntityTrainBase)
				{
					this.playSound(player, RTMCore.KEY_Horn);
				}
				else if(riding instanceof EntityArtillery)
				{
					this.sendKeyToServer(RTMCore.KEY_Fire, "");
				}
			}
		}
		else if(KEY_CHIME.isPressed())
		{
			this.playSound(player, RTMCore.KEY_Chime);
		}
		else if(mc.gameSettings.keyBindInventory.isKeyDown())
		{
			if(player.isRiding() && riding instanceof EntityVehicleBase)
			{
				mc.gameSettings.keyBindInventory.isPressed();
				this.sendKeyToServer(RTMCore.KEY_ControlPanel, "");
			}
		}
		else if(KEY_ATS.isPressed())
		{
			this.sendKeyToServer(RTMCore.KEY_ATS, "");
		}

		if(player.isRiding() && (riding instanceof EntityTrainBase))
		{
			EntityTrainBase train = (EntityTrainBase)riding;
			if(KEY_EB.isPressed())
			{
				train.syncVehicleState(TrainStateType.Notch, (byte)EnumNotch.emergency_brake.id);
				this.playSound(player, RTMCore.KEY_Horn);
				NGTLog.showChatMessage(new TextComponentString("Push EB"));
			}
			else if(KEY_CHIME_NEXT.isPressed())
			{
				TrainStateType type = TrainStateType.Announcement;
				int i0 = train.getVehicleState(type) + 1;
				i0 = i0 < type.min ? type.max : (i0 > type.max ? 0 : i0);
				train.syncVehicleState(type, (byte)i0);
				NGTLog.showChatMessage(new TextComponentString("Next chime"));
			}
			else if(KEY_CHIME_PREV.isPressed())
			{
				TrainStateType type = TrainStateType.Announcement;
				int i0 = train.getVehicleState(type) - 1;
				i0 = i0 < type.min ? type.max : (i0 > type.max ? 0 : i0);
				train.syncVehicleState(type, (byte)i0);
				NGTLog.showChatMessage(new TextComponentString("Prev chime"));
			}
		}
	}

	private void unpressKey(KeyBinding key)
	{
		NGTUtil.getMethod(KeyBinding.class, key, "unpressKey", "func_74505_d", new Class[]{});
	}

	private void sendKeyToServer(byte keyCode, String sound)
	{
		EntityPlayer player = NGTUtilClient.getMinecraft().player;
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketRTMKey(player, keyCode, sound));
	}

	private void playSound(EntityPlayer player, byte key)
	{
		if(player.isRiding() && player.getRidingEntity() instanceof EntityTrainBase)
		{
			EntityTrainBase train = (EntityTrainBase)player.getRidingEntity();
			ModelSetTrain modelset = train.getResourceState().getResourceSet();
			if(modelset != null)
			{
				String sound = "";
				if(key == RTMCore.KEY_Horn)
				{
					sound = modelset.getConfig().sound_Horn;
					MacroRecorder.INSTANCE.recHorn(player.world);
				}
				else if(key == RTMCore.KEY_Chime)
				{
					int index = train.getVehicleState(TrainStateType.Announcement);
					String[][] sa0 = ((TrainConfig)modelset.getConfig()).sound_Announcement;
					if(sa0 != null && index < sa0.length)
					{
						sound = sa0[index][1];
						MacroRecorder.INSTANCE.recChime(player.world, sa0[index][1]);
					}
				}

				if(sound != null && !sound.isEmpty())
				{
					this.sendKeyToServer(key, sound);
				}
			}
		}
	}
}