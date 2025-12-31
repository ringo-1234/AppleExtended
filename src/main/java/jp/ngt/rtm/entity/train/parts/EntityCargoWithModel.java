package jp.ngt.rtm.entity.train.parts;

import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class EntityCargoWithModel<T extends ModelSetBase> extends EntityCargo implements IResourceSelector
{
	private final ResourceState<T> state = new ResourceState<>(this.getSubType(), this);

	public EntityCargoWithModel(World par1)
	{
		super(par1);
	}

	public EntityCargoWithModel(World par1, ItemStack itemStack, int x, int y, int z)
	{
		super(par1, itemStack, x, y, z);
	}

	public EntityCargoWithModel(World par1, EntityVehicleBase par2, ItemStack par3, float[] par4Pos, byte id)
    {
		super(par1, par2, par3, par4Pos, id);
    }

	@Override
	protected void entityInit()
	{
		super.entityInit();
	}

	@Override
	public void syncData()
	{
		this.updateResourceState();
	}

	@Override
	protected void readCargoFromNBT(NBTTagCompound nbt)
	{
		this.state.readFromNBT(nbt.getCompoundTag("State"));
	}

	@Override
	protected void writeCargoToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("State", this.state.writeToNBT());
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
		if(player.isSneaking())
    	{
			if(this.world.isRemote)
	        {
				player.openGui(RTMCore.instance, RTMCore.guiIdSelectEntityModel, player.world, this.getEntityId(), 0, 0);
	        }
			return true;
		}
		return false;
    }

	@Override
    public ResourceState<T> getResourceState()
    {
    	return this.state;
    }

	@Override
	public void updateResourceState()
	{
		if(this.world == null || !this.world.isRemote)
		{
			this.writeCargoToItem();
			PacketNBT.sendToClient(this);

			if(this.getVehicle() != null)
			{
				this.updatePartPos(this.getVehicle());
			}
		}
	}

	@Override
	public int[] getSelectorPos()
	{
		return new int[]{this.getEntityId(), -1, 0};
	}

	@Override
	public boolean closeGui(ResourceState par1)
	{
		this.updateResourceState();
		return true;
	}

	protected abstract ResourceType getSubType();
}