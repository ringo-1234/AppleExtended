package jp.ngt.rtm.rail;

import java.util.List;

import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.BogieController.UpdateFlag;
import jp.ngt.rtm.network.PacketNotice;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailMapTurntable;
import jp.ngt.rtm.rail.util.RailPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTurnTableCore extends TileEntityLargeRailCore
{
	public static final float ROTATION_INC = 0.5F;
	public static final float ROTATION_STEP = 15.0F;

	private boolean isGettingPower;
	private float rotation;

	@Override
	protected void readRailData(NBTTagCompound nbt)
	{
		super.readRailData(nbt);
		this.rotation = nbt.getFloat("Rotation");

	}

	@Override
	protected void writeRailData(NBTTagCompound nbt)
	{
		super.writeRailData(nbt);
		nbt.setFloat("Rotation", this.rotation);
	}

	@Override
    public void update()
    {
		super.update();

		boolean b = this.world.isBlockIndirectlyGettingPowered(this.getPos()) > 0;
    	if(this.isGettingPower ^ b)
    	{
    		this.isGettingPower = b;
    	}

    	if(!this.getWorld().isRemote)
		{
    		float f0 = this.rotation % ROTATION_STEP;
        	if(this.isGettingPower || (f0 != 0.0F))
        	{
        		this.rotation += ROTATION_INC;
        		if(this.rotation >= 360.0F)
        		{
        			this.rotation = 0.0F;
        		}
        		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, "TT:" + this.getRotation(), this));

        		((RailMapTurntable)this.getRailMap(null)).setRotation(this.rotation);
        		this.updateTrainYaw();
        	}
		}
    }

	//ServerOnly
	private void updateTrainYaw()
	{
		List<EntityTrainBase> list = this.getWorld().getEntitiesWithinAABB(EntityTrainBase.class,
				new AxisAlignedBB(this.getX() - 5, this.getY(), this.getZ() - 5, this.getX() + 5, this.getY() + 3, this.getZ() + 5));
		for(EntityTrainBase train : list)
		{
			Vec3 vec = PooledVec3.create(train.posX - (this.getX() + 0.5D), 0.0D, train.posZ - (this.getZ() + 0.5D));
			vec = vec.rotateAroundY(ROTATION_INC);
			train.setPositionAndRotation((this.getX() + 0.5D) + vec.getX(), train.posY, (this.getZ() + 0.5D) + vec.getZ(), train.rotationYaw + ROTATION_INC, train.rotationPitch);
			train.bogieController.updateBogiePos(train, 0, UpdateFlag.YAW);
			train.bogieController.updateBogiePos(train, 1, UpdateFlag.YAW);
		}
	}

	@Override
	public void createRailMap()
    {
    	if(this.isLoaded())//同期ができてない状態でのRailMapの生成を防ぐ
    	{
    		RailPosition start = this.railPositions[0];
    		RailPosition end = this.railPositions[1];
    		int r = 0;
    		if(start.blockX == end.blockX)
    		{
    			r = Math.abs(start.blockZ - end.blockZ) / 2;
    		}
    		else if(start.blockZ == end.blockZ)
    		{
    			r = Math.abs(start.blockX - end.blockX) / 2;
    		}
    		this.railmap  = new RailMapTurntable(start, end, this.getX(), this.getY(), this.getZ(), r);
    	}
    }

	public float getRotation()
	{
		return this.rotation;
	}

	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected AxisAlignedBB getRenderAABB()
	{
		if(this.isLoaded())
		{
			int startX = this.railPositions[0].blockX;
	    	int startZ = this.railPositions[0].blockZ;
	    	int endX = this.railPositions[1].blockX;
	    	int endZ = this.railPositions[1].blockZ;
	    	int lenHalf = (startX == endX) ? Math.abs(endZ - startZ) / 2 : Math.abs(endX - startX) / 2;
	    	AxisAlignedBB aabb = new AxisAlignedBB(this.getX() - lenHalf, this.getY(), this.getZ() - lenHalf,
	    			this.getX() + lenHalf +1, this.getY() + 3, this.getZ() + lenHalf + 1);
	    	return aabb;
		}
		return null;
	}

	@Override
    public void sendPacket()
    {
		super.sendPacket();

    	if(this.world == null || !this.world.isRemote)
    	{
    		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, "TT:" + this.getRotation(), this));
    	}
    }

	@Override
	public String getRailShapeName()
	{
		RailMap map = this.getRailMap(null);
		StringBuilder sb = new StringBuilder();
		sb.append("Type:TurnTable, ");
		sb.append("X:").append(map.getEndRP().blockX - map.getStartRP().blockX).append(", ");
		sb.append("Y:").append(map.getEndRP().blockY - map.getStartRP().blockY).append(", ");
		sb.append("Z:").append(map.getEndRP().blockZ - map.getStartRP().blockZ);
		return sb.toString();
	}
}