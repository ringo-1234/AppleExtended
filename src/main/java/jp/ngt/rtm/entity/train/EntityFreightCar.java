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

package jp.ngt.rtm.entity.train;

import jp.ngt.ngtlib.item.ItemUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.entity.train.parts.*;
import jp.ngt.rtm.item.ItemCargo;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public final class EntityFreightCar extends EntityTrainBase implements IInventory {
    private static final float[][] CARGO_POS = new float[][]{
            {0.0F, 0.0F, 8.0F},
            {0.0F, 0.0F, 4.0F},
            {0.0F, 0.0F, 0.0F},
            {0.0F, 0.0F, -4.0F},
            {0.0F, 0.0F, -8.0F}};

    private ItemStack[] cargoSlots = ItemUtil.getEmptyArray(5);
    public EntityCargo[] cargoEntities = new EntityCargo[5];

    public EntityFreightCar(World world) {
        super(world);
    }

    public EntityFreightCar(World world, String s) {
        super(world, s);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        NBTTagList list = nbt.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nbt1 = list.getCompoundTagAt(i);
            if (nbt1.hasKey("Slot", 1)) {
                byte b0 = nbt1.getByte("Slot");
                if (b0 >= 0 && b0 < this.cargoSlots.length) {
                    this.cargoSlots[b0] = new ItemStack(nbt1);
                }
            }
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.cargoSlots.length; ++i) {
            if (this.cargoSlots[i] != null && this.cargoEntities[i] != null) {
                this.cargoEntities[i].writeCargoToItem();
                NBTTagCompound nbt0 = new NBTTagCompound();
                nbt0.setByte("Slot", (byte) i);
                this.cargoSlots[i].writeToNBT(nbt0);
                list.appendTag(nbt0);
            }
        }
        nbt.setTag("Items", list);
    }

    @Override
    public void setDead() {
        super.setDead();

        for (int i = 0; i < this.cargoEntities.length; ++i) {
            if (this.cargoEntities[i] != null) {
                this.cargoEntities[i].setDead();
            }
        }
    }

    @Override
    public void onVehicleUpdate() {
        super.onVehicleUpdate();

        if (!this.world.isRemote) {
            for (int i = 0; i < this.cargoSlots.length; ++i) {
                if (this.hasCargo(i)) {
                    if (this.cargoEntities[i] == null) {
                        EntityCargo entity = this.createCargoEntity((byte) i);
                        entity.updatePartPos(this);
                        this.world.spawnEntity(entity);
                        this.cargoEntities[i] = entity;
                    }
                } else {
                    if (this.cargoEntities[i] != null) {
                        this.cargoEntities[i].setDead();
                        this.cargoEntities[i] = (EntityContainer) null;
                    }
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1, float par2) {
        if (!this.world.isRemote) {
            for (int i = 0; i < this.cargoSlots.length; ++i) {
                if (this.cargoSlots[i] != null) {
                    this.entityDropItem(this.cargoSlots[i], 1.0F);
                }
            }
        }
        return super.attackEntityFrom(par1, par2);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (super.processInitialInteract(player, hand)) {
            return true;
        } else {
            if (!this.world.isRemote) {
                player.openGui(RTMCore.instance, RTMCore.instance.guiIdFreightCar, player.world, this.getEntityId(), 0, 0);
            }
            return true;
        }
    }

    private boolean hasCargo(int par1) {
        ItemStack itemstack = this.cargoSlots[par1];
        return itemstack != null && itemstack.getItem() instanceof ItemCargo;
    }

    private EntityCargo createCargoEntity(byte slot) {
        EntityCargo cargo = null;
        int damage = this.cargoSlots[slot].getItemDamage();
        float[] pos = CARGO_POS[slot].clone();
        pos[1] += this.getVehicleYOffset();
        switch (damage) {
            case 0:
                cargo = new EntityContainer(this.world, this, this.cargoSlots[slot], pos, slot);
                break;
            case 1:
                cargo = new EntityArtillery(this.world, this, this.cargoSlots[slot], pos, slot);
                break;
            case 2:
                cargo = new EntityTie(this.world, this, this.cargoSlots[slot], pos, slot);
                break;
            default:
                cargo = new EntityContainer(this.world, this, this.cargoSlots[slot], pos, slot);
                break;
        }

        cargo.readCargoFromItem();

        if (damage == 0 || damage == 1) {
            EntityCargoWithModel entity = (EntityCargoWithModel) cargo;
            ResourceState state = entity.getResourceState();
            if (state.getResourceSet().isDummy()) {
                state.setResourceName(state.type.defaultName);
                entity.updateResourceState();
            }
        }

        return cargo;
    }

    @Override
    public int getSizeInventory() {
        return this.cargoSlots.length;
    }

    @Override
    public ItemStack getStackInSlot(int par1) {
        return this.cargoSlots[par1];
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (!this.cargoSlots[par1].isEmpty()) {
            ItemStack itemstack;
            if (this.cargoSlots[par1].getCount() <= par2) {
                itemstack = this.cargoSlots[par1];
                this.cargoSlots[par1] = ItemStack.EMPTY;
                return itemstack;
            } else {
                itemstack = this.cargoSlots[par1].splitStack(par2);
                if (this.cargoSlots[par1].getCount() == 0) {
                    this.cargoSlots[par1] = ItemStack.EMPTY;
                }
                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int par1) {
        if (!this.cargoSlots[par1].isEmpty()) {
            ItemStack itemstack = this.cargoSlots[par1];
            this.cargoSlots[par1] = ItemStack.EMPTY;
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack itemStack) {
        this.cargoSlots[par1] = itemStack;
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getInventoryStackLimit()) {
            itemStack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public String getName() {
        return "Inventory_FreightCar";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.getDistanceSq(player) < 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        ;
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        ;
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return true;
    }

    @Override
    public int getField(int id) {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public int getFieldCount() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public void clear() {
        // TODO 自動生成されたメソッド・スタブ
    }

    @Override
    protected ResourceType getSubType() {
        return RTMResource.TRAIN_CC;
    }

    @Override
    public boolean isEmpty() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }
}