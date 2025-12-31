package jp.ngt.rtm.rail;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.math.AABBInt;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.network.PacketLargeRailCore;
import jp.ngt.rtm.rail.util.RailMaker;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailMapSwitch;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.rail.util.SwitchType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLargeRailSwitchCore extends TileEntityLargeRailCore
{
	private SwitchType switchObj;

	private List<RailMapSwitch> openedRails = new ArrayList<RailMapSwitch>();

	public TileEntityLargeRailSwitchCore()
	{
		super();
	}

	@Override
	protected void readRailData(NBTTagCompound nbt)
	{
		byte size = nbt.getByte("Size");
		this.railPositions = new RailPosition[size];

    	for(int i = 0; i < size; ++i)
    	{
    		this.railPositions[i] = RailPosition.readFromNBT(nbt.getCompoundTag("RP" + i));
    	}
	}

	private RailPosition getRP(int x, int y, int z, byte dir, boolean b)
	{
		RailPosition rp = new RailPosition(x, y, z, dir, (byte)(b ? 1 : 0));
		rp.anchorYaw = NGTMath.wrapAngle((byte)dir * 45.0F);
		return rp;
	}

	@Override
	protected void writeRailData(NBTTagCompound nbt)
	{
		nbt.setByte("Size", (byte)this.railPositions.length);

		for(int i = 0; i < this.railPositions.length; ++i)
    	{
        	nbt.setTag("RP" + i, this.railPositions[i].writeToNBT());
    	}
	}

	@Override
	public void setRailPositions(RailPosition[] par1)
    {
		super.setRailPositions(par1);
		this.onBlockChanged();
    }

	@Override
	public void createRailMap()
    {
		if(this.isLoaded() && this.switchObj == null)
		{
			this.switchObj = (new RailMaker(this.getWorld(), this.railPositions).getSwitch());
		}
    }

	public SwitchType getSwitch()
	{
		if(this.switchObj == null)
    	{
    		this.createRailMap();
    	}
		return this.switchObj;
	}

    @Override
    public byte getPacketType()
    {
    	return PacketLargeRailCore.TYPE_SWITCH;
    }

    @Override
    public void update()
    {
    	super.update();

    	if(this.getSwitch() != null)
    	{
    		this.getSwitch().onUpdate(this.getWorld());
    	}
    }

    /**ブロック更新時*/
    public void onBlockChanged()
    {
    	this.getSwitch().onBlockChanged(this.getWorld());
    	if(!this.getWorld().isRemote)
    	{
    		this.sendPacket();//Clientへ更新を通知
    	}
    }

    @Override
	public RailMap getRailMap(Entity entity)
	{
    	SwitchType st = this.getSwitch();
    	if(st == null){return null;}

    	if(entity == null)
    	{
    		return this.getAllRailMaps()[0];
    	}

    	return st.getRailMap(entity);
	}

    @Override
    public RailMapSwitch[] getAllRailMaps()
    {
    	if(this.getSwitch() != null)
    	{
    		return this.getSwitch().getAllRailMap();
    	}
    	return null;
    }

	@Override
	@SideOnly(Side.CLIENT)
	protected AxisAlignedBB getRenderAABB()
	{
		AABBInt box = this.getRailSize();
    	AxisAlignedBB aabb = new AxisAlignedBB(box.minX - 1, box.minY, box.minZ - 1, box.maxX + 2, box.maxY + 2, box.maxZ + 2);
    	boolean flag = (aabb.maxX - aabb.minX <= 3 && aabb.maxZ - aabb.minZ <= 3);
    	return flag ? null : aabb;
	}

	@Override
	public AABBInt getRailSize()
	{
		int minX = this.startPoint[0];
    	int maxX = this.startPoint[0];
    	int minY = this.getPos().getY();
    	int maxY = this.getPos().getY();
    	int minZ = this.startPoint[2];
    	int maxZ = this.startPoint[2];
    	for(RailPosition rp : this.railPositions)
    	{
    		minX = minX <= rp.blockX ? minX : rp.blockX;
    		maxX = maxX >= rp.blockX ? maxX : rp.blockX;
    		minZ = minZ <= rp.blockZ ? minZ : rp.blockZ;
    		maxZ = maxZ >= rp.blockZ ? maxZ : rp.blockZ;
    	}
		return new AABBInt(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public String getRailShapeName()
	{
		SwitchType st = this.getSwitch();
		AABBInt box = this.getRailSize();
		StringBuilder sb = new StringBuilder();
		sb.append("Type:Switch ").append(st.getName()).append(", ");
		sb.append("X:").append(box.sizeX()).append(", ");
		sb.append("Y:").append(box.sizeY()).append(", ");
		sb.append("Z:").append(box.sizeZ());
		return sb.toString();
	}
}