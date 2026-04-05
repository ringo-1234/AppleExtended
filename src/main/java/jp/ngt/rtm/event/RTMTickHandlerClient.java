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
import org.lwjgl.opengl.Display;

/**
 * FMLのイベント
 */
@SideOnly(Side.CLIENT)
public final class RTMTickHandlerClient {
    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event)//Minecraft.runGameLoop()
    {
        if (event.phase == Phase.END) {
            if (NGTUtilClient.getMinecraft().inGameHasFocus && Display.isActive()) {
                EntityPlayer player = NGTUtilClient.getMinecraft().player;
                if (player.isRiding() && player.getRidingEntity() instanceof EntityArtillery) {
                    ((EntityArtillery) player.getRidingEntity()).updateYawAndPitch(player);
                }
            }

            //RenderMirror.INSTANCE.onRenderTickEnd();
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)//runGameLoop()内で複数回呼ばれる
    {
        World world = NGTUtilClient.getMinecraft().world;
        if (!NGTUtilClient.getMinecraft().isGamePaused() && world != null) {
            if (event.phase == Phase.START) {
				/*if(!RenderMirror.INSTANCE.finishRender)
				{
					RenderMirror.INSTANCE.update();
				}*/
                //RTMCore.proxy.getFormationManager().updateFormations(world);

                RTMKeyHandlerClient.INSTANCE.onTickStart();
            } else if (event.phase == Phase.END) {
                RTMKeyHandlerClient.INSTANCE.onTickEnd();
            }
        }
    }
}