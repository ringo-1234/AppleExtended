package jp.ngt.rtm.event;

import org.lwjgl.opengl.Display;

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**FMLのイベント*/
@SideOnly(Side.CLIENT)
public final class RTMTickHandlerClient
{
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)//Minecraft.runGameLoop()
	{
		if(event.phase == Phase.END)
		{
			if(NGTUtilClient.getMinecraft().inGameHasFocus && Display.isActive())
	        {
				EntityPlayer player = NGTUtilClient.getMinecraft().player;
				if(player.isRiding() && player.getRidingEntity() instanceof EntityArtillery)
				{
					((EntityArtillery)player.getRidingEntity()).updateYawAndPitch(player);
				}
	        }

			//RenderMirror.INSTANCE.onRenderTickEnd();
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)//runGameLoop()内で複数回呼ばれる
	{
		World world = NGTUtilClient.getMinecraft().world;
		if(!NGTUtilClient.getMinecraft().isGamePaused() && world != null)
		{
			if(event.phase == Phase.START)
			{
				/*if(!RenderMirror.INSTANCE.finishRender)
				{
					RenderMirror.INSTANCE.update();
				}*/
				//RTMCore.proxy.getFormationManager().updateFormations(world);

				RTMKeyHandlerClient.INSTANCE.onTickStart();
			}
			else if(event.phase == Phase.END)
			{
				RTMKeyHandlerClient.INSTANCE.onTickEnd();
			}
		}
	}
}