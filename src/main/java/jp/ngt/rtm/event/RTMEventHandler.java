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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class RTMEventHandler
{
	//PlayerEvent.StartTracking
	//CanUpdate

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)//ServerConfigurationManager.initializeConnectionToPlayer()
	{
		if(NGTUtil.isSMP() || NGTUtil.openedLANWorld())
		{
			ModelPackManager.INSTANCE.sendModelSetsToClient((EntityPlayerMP)event.player);
		}
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
		//StationManager.INSTANCE.loadData(event.world);
		FormationManager.getInstance().loadData(event.getWorld());
	}

	//呼ばれねぇ
	/*@SubscribeEvent
	public void canUpdateEntity(CanUpdate event)
	{
		if(event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityVehicleBase)
		{
			event.setCanUpdate(false);
		}
	}*/

	/*@SubscribeEvent
	public void onFillBucket(FillBucketEvent event)
	{
		if(!event.getWorld().isRemote)
		{
			ItemStack itemstack = ItemBucketLiquid.useBucket(event.getWorld(), event.getEntityPlayer(), event.getTarget(), null, 0, null, false);
			if(itemstack != null)
			{
				event.setFilledBucket(itemstack);
				event.setResult(Result.ALLOW);
			}
		}
	}*/

	@SubscribeEvent
	public void onCollison(GetCollisionBoxesEvent event)
	{
		CollisionHelper.INSTANCE.onCollison(event);
	}
}