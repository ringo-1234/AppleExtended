package jp.ngt.rtm.entity.vehicle;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMEntity;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.network.PacketVehicleMovement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public final class VehicleTrackerEntry extends EntityTrackerEntry
{
	private Entity trackedEntity;
    /** check for sync when ticks % updateFrequency==0 */
    private int updateFrequency;
    private boolean updatedPlayerVisibility;
    private List<Entity> passengers = Collections.<Entity>emptyList();
    //public boolean playerEntitiesUpdated;
    //public Set<EntityPlayerMP> trackingPlayers = Sets.<EntityPlayerMP>newHashSet();

    //以下独自フィールド///////////////////////////////////////////////

    private double posX, posY, posZ;
    private boolean isDataInitialized;
    private Entity rider;
    private boolean isTrain;

    public VehicleTrackerEntry(Entity par2)
    {
    	super(par2, 256, 256, RTMEntity.FREQ_VEHICLE, false);
    	this.trackedEntity = par2;
        this.updateFrequency = RTMEntity.FREQ_VEHICLE;
        //this.trackingPlayers = par1.trackingPlayers;//setPlayers()で行う
        this.isTrain = (this.trackedEntity instanceof EntityTrainBase || this.trackedEntity instanceof EntityBogie);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof VehicleTrackerEntry ? ((VehicleTrackerEntry)obj).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
    }

    @Override
    public int hashCode()
    {
        return this.trackedEntity.getEntityId();
    }

    public void setPlayers(Set<EntityPlayerMP> par1)
    {
    	this.trackingPlayers.clear();
    	this.trackingPlayers.addAll(par1);
    }

    @Override
    public void updatePlayerList(List<EntityPlayer> par1)
    {
        this.playerEntitiesUpdated = false;

        if(!this.isDataInitialized || this.trackedEntity.getDistanceSq(this.posX, this.posY, this.posZ) > 16.0D)
        {
            this.posX = this.trackedEntity.posX;
            this.posY = this.trackedEntity.posY;
            this.posZ = this.trackedEntity.posZ;
            this.isDataInitialized = true;
            this.playerEntitiesUpdated = true;
            this.updatePlayerEntities(par1);
        }

        //乗ってるEntityの同期
        List<Entity> list = this.trackedEntity.getPassengers();
        if(!list.equals(this.passengers))
        {
            this.passengers = list;
            this.sendPacketToTrackedPlayers(new SPacketSetPassengers(this.trackedEntity));
        }

        if(this.updateCounter % this.updateFrequency == 0)// || this.trackedEntity.getDataWatcher().hasChanges())
        {
            if(this.trackedEntity.getRidingEntity() == null)
            {
                RTMCore.NETWORK_WRAPPER.sendToAll(new PacketVehicleMovement(this.trackedEntity, false));

            	if(this.isTrain)
            	{
            		;
            	}
            	else
            	{
            		//不要?
                    /*this.sendPacketToTrackedPlayers(new SPacketEntityVelocity(this.trackedEntity.getEntityId(),
                    		this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ));
                    this.ridingEntity = false;*/
            	}
            }
        }

        this.sendMetadataToAllAssociatedPlayers();

        ++this.updateCounter;
    }

    /**DataWatcherの同期*/
    private void sendMetadataToAllAssociatedPlayers()
    {
    	EntityDataManager entitydatamanager = this.trackedEntity.getDataManager();

        if(entitydatamanager.isDirty())
        {
            this.sendToTrackingAndSelf(new SPacketEntityMetadata(this.trackedEntity.getEntityId(), entitydatamanager, false));
        }
    }

    @Override
    public void sendPacketToTrackedPlayers(Packet<?> packetIn)
    {
        for(EntityPlayerMP entityplayermp : this.trackingPlayers)
        {
            entityplayermp.connection.sendPacket(packetIn);
        }
    }

    @Override
    public void sendToTrackingAndSelf(Packet<?> packetIn)
    {
        this.sendPacketToTrackedPlayers(packetIn);
    }

    //setDead()時に呼び出し
    //されない！独自Tracker追加のため
    @Override
    public void sendDestroyEntityPacketToTrackedPlayers()
    {
        for(EntityPlayerMP entityplayermp : this.trackingPlayers)
        {
            this.trackedEntity.removeTrackingPlayer(entityplayermp);
            entityplayermp.removeEntity(this.trackedEntity);
        }
    }

    @Override
    public void removeFromTrackedPlayers(EntityPlayerMP playerMP)
    {
        if (this.trackingPlayers.contains(playerMP))
        {
            this.trackedEntity.removeTrackingPlayer(playerMP);
            playerMP.removeEntity(this.trackedEntity);
            this.trackingPlayers.remove(playerMP);
        }
    }

    @Override
    public void updatePlayerEntity(EntityPlayerMP par1)
    {
        if(par1 != this.trackedEntity)
        {
            if(this.isVisibleTo(par1))
            {
                if(!this.trackingPlayers.contains(par1) && (this.isPlayerWatchingThisChunk(par1) || this.trackedEntity.forceSpawn))
                {
                    this.trackingPlayers.add(par1);
                    Packet packet = FMLNetworkHandler.getEntitySpawningPacket(this.trackedEntity);
                    par1.connection.sendPacket(packet);

                    if(!this.trackedEntity.getDataManager().isEmpty())
                    {
                        par1.connection.sendPacket(new SPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataManager(), true));
                    }

                    PacketNBT.sendToClient(this.trackedEntity);

                    if(this.trackedEntity.getRidingEntity() != null)
                    {
                        par1.connection.sendPacket(new SPacketEntityAttach(this.trackedEntity, this.trackedEntity.getRidingEntity()));
                    }

                    ForgeEventFactory.onStartEntityTracking(this.trackedEntity, par1);
                }
            }
            else if (this.trackingPlayers.contains(par1))
            {
                this.trackingPlayers.remove(par1);
                par1.removeEntity(this.trackedEntity);
                ForgeEventFactory.onStopEntityTracking(trackedEntity, par1);
            }
        }
    }

    @Override
    public boolean isVisibleTo(EntityPlayerMP playerMP)
    {
        return true;
    }

    private boolean isPlayerWatchingThisChunk(EntityPlayerMP playerMP)
    {
        return playerMP.getServerWorld().getPlayerChunkMap().isPlayerWatchingChunk(playerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
    }

    @Override
    public void updatePlayerEntities(List<EntityPlayer> players)
    {
        for (int i = 0; i < players.size(); ++i)
        {
            this.updatePlayerEntity((EntityPlayerMP)players.get(i));
        }
    }

    //以下独自メソッド///////////////////////////////////////////////

    /**@param par1 EntityVehicleBase or EntityBogie*/
    public static boolean trackingVehicle(Entity par1)
    {
    	if(!(par1 instanceof EntityVehicleBase || par1 instanceof EntityBogie))
    	{
    		throw new IllegalArgumentException("This entity is not Vehicle");
    	}

    	if(par1.world instanceof WorldServer)
    	{
    		EntityTracker tracker = ((WorldServer)par1.world).getEntityTracker();
    		Set<EntityTrackerEntry> trackedEntities = getTrackedEntities(tracker);
    		if(trackedEntities != null)
    		{
    			VehicleTrackerEntry tte = new VehicleTrackerEntry(par1);
    			EntityTrackerEntry trackerEntry = null;
                for(EntityTrackerEntry entry : trackedEntities)
                {
                    if(entry != null && entry.equals(tte))
                    {
                    	if(!(entry instanceof VehicleTrackerEntry))
                    	{
                    		trackerEntry = entry;
                    	}
                    	break;
                    }
                }

                if(trackerEntry != null)
        		{
        			trackedEntities.remove(trackerEntry);
        			tte.setPlayers(trackerEntry.trackingPlayers);
        			trackedEntities.add(tte);
        			return true;
        		}
    		}
    	}

    	NGTLog.debug("Failed to change tracking entry (VTE)");
    	return false;
    }

    protected static Set<EntityTrackerEntry> getTrackedEntities(EntityTracker tracker)
    {
    	return (Set<EntityTrackerEntry>)NGTUtil.getField(EntityTracker.class, tracker, new String[]{"entries", "field_72793_b"});
    }
}