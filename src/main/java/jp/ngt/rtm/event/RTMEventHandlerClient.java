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

import jp.apple.fix.model.CachedModelUtil;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.ClientProxy;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMSound;
import jp.ngt.rtm.block.decoration.DecorationStore;
import jp.ngt.rtm.entity.RenderBullet;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.entity.train.parts.EntityFloor;
import jp.ngt.rtm.gui.GuiIngameCustom;
import jp.ngt.rtm.gui.camera.Camera;
import jp.ngt.rtm.modelpack.ModelPackManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class RTMEventHandlerClient {
    private final GuiIngameCustom guiInGame;
    private boolean compactedAfterModelConstruct;

    public RTMEventHandlerClient(Minecraft par1) {
        this.guiInGame = new GuiIngameCustom(par1);
    }

	/*@SubscribeEvent
	public void onTick(TickEvent.RenderTickEvent event)
	{
		;
	}*/

    @SubscribeEvent
    public void onTick(RenderWorldLastEvent event)//entityRenderer.renderWorldPassの最後
    {
        if (!this.compactedAfterModelConstruct && ModelPackManager.INSTANCE.modelConstructed) {
            CachedModelUtil.compactAll();
            this.compactedAfterModelConstruct = true;
        }

        byte viewMode = ClientProxy.getViewMode(NGTUtilClient.getMinecraft().player);
        if (viewMode == 4) {
            Camera.INSTANCE.onRenderWorldPost();
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Pre event)//GuiIngame, GuiIngameForge
    {
        byte viewMode = ClientProxy.getViewMode(NGTUtilClient.getMinecraft().player);
        if (viewMode == 4) {
            Camera.INSTANCE.onRenderGameOverlayPre();
        }
        this.guiInGame.onRenderGui(event);
    }

    @SubscribeEvent
    public void onRenderDebugText(RenderGameOverlayEvent.Text event) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        event.getLeft().addAll(CachedModelUtil.getDebugLines());
    }

    @SubscribeEvent
    public void onUpdateFOV(FOVUpdateEvent event)//EntityRenderer.updateFovModifierHand()
    {
        event.setNewfov(RTMCore.proxy.getFov(event.getEntity(), event.getFov()));
    }

    @SubscribeEvent
    public void onLoadSound(SoundLoadEvent event) {
        RTMSound.registerSoundDomains();
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        DecorationStore.INSTANCE.onTextureStitch(event.getMap());
    }

    private boolean isPlayerSittingSeat(EntityPlayer player, byte type) {
        if (player.isRiding() && player.getRidingEntity() instanceof EntityFloor) {
            if (((EntityFloor) player.getRidingEntity()).getSeatType() == type) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (this.isPlayerSittingSeat(event.getEntityPlayer(), (byte) 3))//寝台
        {
            GL11.glPushMatrix();
            event.getRenderer().getMainModel().isRiding = false;
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (this.isPlayerSittingSeat(event.getEntityPlayer(), (byte) 3))//寝台
        {
            event.getRenderer().getMainModel().isRiding = true;
            GL11.glPopMatrix();
        }

        //RenderEventHandler.INSTANCE.renderEntity(event.entity, event.renderer, event.x, event.y, event.z);

        RenderBullet.onPlayerRender(event.getEntityPlayer(), false);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre event) {
        this.renderEntityPre(event.getEntity(), event.getRenderer(), event.getX(), event.getY(), event.getZ());
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post event) {
        EntityLivingBase entity = event.getEntity();
        this.renderEntityPost(entity, event.getRenderer(), event.getX(), event.getY(), event.getZ());

        if (entity instanceof EntityNPC) {
            RenderBullet.onNPCRender((EntityNPC) entity, event.getX(), event.getY(), event.getZ());
        }
    }

    @SubscribeEvent
    public void onRenderPlayerHand(RenderHandEvent event) {
        EntityPlayer player = NGTUtilClient.getMinecraft().player;
        byte viewMode = ClientProxy.getViewMode(player);
        if (viewMode >= 0 && viewMode < 3) {
            event.setCanceled(true);
        }

        RenderBullet.onPlayerRender(player, true);
    }

    //NVD////////////////////////////////////////////////////////////////////////

    public void renderEntityPre(EntityLivingBase entity, RenderLivingBase renderer, double x, double y, double z) {
        if (!this.isPlayerWearedNVD(entity)) {
            return;
        }

        GLHelper.disableLighting();
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 0.5F, 0.4F, 1.0F);
        GLHelper.setLightmapMaxBrightness();
    }

    public void renderEntityPost(EntityLivingBase entity, RenderLivingBase renderer, double x, double y, double z) {
        if (!this.isPlayerWearedNVD(entity)) {
            return;
        }

        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GLHelper.enableLighting();
    }

    private boolean isPlayerWearedNVD(EntityLivingBase entity) {
        EntityPlayer viewer = NGTUtilClient.getMinecraft().player;
        return entity != viewer && ClientProxy.getViewMode(viewer) == 3;
    }

    /////////////////////////////////////////////////////////////////////////////
}
