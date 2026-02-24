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

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.decoration.DecorationStore;
import jp.ngt.rtm.entity.train.util.FormationManager;
import jp.ngt.rtm.entity.util.CollisionHelper;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.sound.SpeakerSounds;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public final class RTMEventHandler
{
	@SubscribeEvent
	public void onServerConnectionFromClient(ServerConnectionFromClientEvent event)
	{
		if (NGTUtil.isSMP() || NGTUtil.openedLANWorld())
		{
			ModelPackManager.INSTANCE.sendModelSetsToClient(((NetHandlerPlayServer)event.getHandler()).player);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		DecorationStore.INSTANCE.loadModels(event.player.getEntityWorld());
		SpeakerSounds.getInstance(true).syncSoundList();
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityBat)
		{
			if(RTMCore.deleteBat)
			{
				event.setCanceled(true);
				return;
			}
		}
	}

	@SubscribeEvent
	public void onLoadWorld(WorldEvent.Load event)
	{
		FormationManager.getInstance().loadData(event.getWorld());
	}

	@SubscribeEvent
	public void onCollison(GetCollisionBoxesEvent event)
	{
		CollisionHelper.INSTANCE.onCollison(event);
	}
}