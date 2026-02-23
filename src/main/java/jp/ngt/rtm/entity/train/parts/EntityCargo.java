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

package jp.ngt.rtm.entity.train.parts;

import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityCargo extends EntityVehiclePart
{
	private static final DataParameter<Byte> CARGO_ID = EntityDataManager.<Byte>createKey(EntityCargo.class, DataSerializers.BYTE);

	protected ItemStack itemCargo;

	public EntityCargo(World par1)
	{
		super(par1);
		this.ignoreFrustumCheck = true;
		this.preventEntitySpawning = true;
	}

	public EntityCargo(World par1, ItemStack itemStack, int x, int y, int z)
	{
		this(par1);
		this.setItem(itemStack);
		this.isIndependent = true;
	}

	public EntityCargo(World par1, EntityVehicleBase par2, ItemStack itemStack, float[] par4Pos, byte id)
	{
		super(par1, par2, par4Pos);
		this.setItem(itemStack);
		this.setCargoId(id);
		this.isIndependent = false;
	}

	private void setItem(ItemStack stack)
	{
		if(stack.getCount() > 1)
		{
			stack.setCount(1);
		}
		this.itemCargo = stack;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(CARGO_ID, Byte.valueOf((byte)0));
	}

	protected byte getCargoId()
	{
		return this.getDataManager().get(CARGO_ID);
	}

	protected void setCargoId(byte id)
	{
		this.getDataManager().set(CARGO_ID, Byte.valueOf((byte)id));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		NBTTagCompound itemNBT = nbt.getCompoundTag("ContainerItem");
		this.itemCargo = new ItemStack(itemNBT);
		this.setCargoId(nbt.getByte("cargoId"));
		this.readCargoFromItem();
		super.readEntityFromNBT(nbt);
	}

	protected abstract void readCargoFromNBT(NBTTagCompound nbt);

	public void readCargoFromItem()
	{
		NBTTagCompound itemNBT = this.itemCargo.getTagCompound();
		if(itemNBT != null)
		{
			this.readCargoFromNBT(itemNBT);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		this.writeCargoToItem();
		NBTTagCompound itemNBT = new NBTTagCompound();
		this.itemCargo.writeToNBT(itemNBT);
		nbt.setTag("ContainerItem", itemNBT);
		nbt.setByte("cargoId", this.getCargoId());
		super.writeEntityToNBT(nbt);
	}

	protected abstract void writeCargoToNBT(NBTTagCompound nbt);

	public void writeCargoToItem()
	{
		if(!this.itemCargo.hasTagCompound())
		{
			this.itemCargo.setTagCompound(new NBTTagCompound());
		}
		this.writeCargoToNBT(this.itemCargo.getTagCompound());
	}

	@Override
	public void onLoadVehicle()
	{
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1, float par2)
	{
		if(this.isEntityInvulnerable(par1) || this.isDead)
		{
			return false;
		}
		else
		{
			if(!par1.isExplosion() && par1.getTrueSource() instanceof EntityPlayer)
			{
				if(!this.world.isRemote)
				{
					if(this.isIndependent || this.getVehicle() == null)
					{
						this.setDead();
						if (!((EntityPlayer) par1.getTrueSource()).capabilities.isCreativeMode) {
							this.dropCargoItem();
						}
					}
				}
				return true;
			}
			return false;
		}
	}

	protected void dropCargoItem()
	{
		this.entityDropItem(this.itemCargo, 1.0F);
	}
}