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

package jp.ngt.rtm.entity.train.util;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.BogieController.MotionState;
import jp.ngt.rtm.network.PacketFormation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public final class Formation {
    public final long id;
    public FormationEntry[] entries;

    private EntityTrainBase controlCar;
    private byte direction;
    private float speed;

    public Formation(long par1, int par2) {
        this.id = par1;
        this.entries = new FormationEntry[par2];
        FormationManager.getInstance().setFormation(par1, this);
    }

    public static Formation readFromNBT(NBTTagCompound nbt, boolean withEntries) {
        long fid = nbt.getLong("FormationId");
        int num = nbt.getInteger("Size");
        Formation formation = new Formation(fid, num);
        if (withEntries) {
            NBTTagList tagList = nbt.getTagList("Entries", 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                FormationEntry entry = FormationEntry.readFromNBT(tagList.getCompoundTagAt(i));
                if (entry != null) {
                    formation.setEntry(entry, i);
                    entry.train.setFormation(formation);
                }
            }
        }
        return formation;
    }

    public void writeToNBT(NBTTagCompound nbt, boolean withEntries) {
        nbt.setLong("FormationId", this.id);
        nbt.setInteger("Size", this.entries.length);

        if (withEntries) {
            NBTTagList tagList = new NBTTagList();
            for (FormationEntry entry : this.entries) {
                if (entry != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    entry.writeToNBT(tag);
                    tagList.appendTag(tag);
                }
            }
            nbt.setTag("Entries", tagList);
        }
    }

    public int size() {
        return this.entries.length;
    }

    public FormationEntry get(int par1) {
        return this.entries[par1];
    }

    private void setEntry(FormationEntry entry, int par2) {
        this.entries[par2] = entry;
    }

    public FormationEntry getEntry(EntityTrainBase par1) {
        for (FormationEntry entry : this.entries) {
            if (entry != null && par1.equals(entry.train)) {
                return entry;
            }
        }
        return null;
    }

    public void setTrain(EntityTrainBase par1, int par3, int par5) {
        this.setEntry(new FormationEntry(par1, par3, par5), par3);
        if (!par1.getEntityWorld().isRemote) {
            this.sendPacket();
        }
    }

    @SideOnly(Side.CLIENT)
    public void setFormationData(EntityTrainBase par1, byte par3, byte par5) {
        FormationEntry entry = this.getEntry(par1);
        if (entry == null) {
            this.setTrain(par1, par3, par5);
        } else {
            entry.entryId = par3;
            entry.dir = par5;
        }
    }

    private void reallocation() {
        int i = 0;
        for (FormationEntry entry : this.entries) {
            if (entry != null) {
                entry.updateFormationData(this, i);
            }
            ++i;
        }
        this.sendPacket();
    }

    private void reverse() {
        NGTUtil.reverse(this.entries);
        for (FormationEntry entry : this.entries) {
            if (entry == null) continue;
            entry.dir ^= 1;
        }
    }

    private void addAll(FormationEntry[] par1) {
        List<FormationEntry> list = new ArrayList<FormationEntry>();
        NGTUtil.addArray(list, this.entries);
        NGTUtil.addArray(list, par1);
        this.entries = list.toArray(new FormationEntry[list.size()]);
    }

    private void trim(int start, int end) {
        FormationEntry[] array = new FormationEntry[end - start + 1];
        int j = 0;
        for (int i = start; i <= end; ++i) {
            array[j] = this.entries[i];
            ++j;
        }
        this.entries = array;
    }

    public void connectTrain(EntityTrainBase par1, EntityTrainBase par2, int par3, int par4, Formation par5) {
        FormationEntry formationentry = this.getEntry(par1);
        FormationEntry formationentry2 = par5.getEntry(par2);
        if (formationentry2 == null) return;
        if (formationentry != null) {
            boolean flag = par3 == formationentry.dir;
            if (flag) {
                this.reverse();
            }

            flag = par4 == formationentry2.dir;
            if (!flag) {
                par5.reverse();
            }

            this.addAll(par5.entries);
            this.reallocation();

            for (FormationEntry formationentry1 : this.entries) {
                if (formationentry1 == null) continue;
                this.setSpeed(0.0F);
                formationentry1.train.setEmergencyBrake();
                formationentry1.train.setVehicleState(TrainState.TrainStateType.Role, TrainState.Role_Center.data);
            }

            FormationManager.getInstance().removeFormation(par5.id);
        }
    }

    public void onRemovedTrain(EntityTrainBase par1) {
        if (this.entries.length <= 1) {
            FormationManager.getInstance().removeFormation(this.id);
            return;
        }

        FormationEntry entry = this.getEntry(par1);
        if (entry == null) {
            return;
        }

        if (entry.entryId == 0) {
            this.trim(1, this.entries.length - 1);
        } else if (entry.entryId == this.entries.length - 1) {
            this.trim(0, this.entries.length - 2);
        } else {
            int i = entry.entryId + 1;
            int j = this.entries.length - i;
            Formation formation = new Formation(FormationManager.getInstance().getNewFormationId(), j);
            int k = 0;

            for (int l = i; l < this.entries.length; ++l, k++) {
                formation.setEntry(this.entries[l], k);
            }

            formation.reallocation();
            this.trim(0, entry.entryId - 1);
        }

        this.reallocation();
    }

    public void onDisconnectedTrain(EntityTrainBase par1, int par2) {
        FormationEntry entry = this.getEntry(par1);
        if (entry == null) {
            return;
        }

        boolean flag = (par2 == entry.dir);
        int i = flag ? entry.entryId : entry.entryId + 1;
        int j = this.entries.length - i;
        Formation formation = new Formation(FormationManager.getInstance().getNewFormationId(), j);
        int k = 0;

        for (int l = i; l < this.entries.length; ++l, k++) {
            formation.setEntry(this.entries[l], k);
        }

        formation.reallocation();
        this.trim(0, i - 1);
        this.reallocation();
    }

    private EntityTrainBase getControlCar() {
        if (this.controlCar == null || !this.controlCar.isControlCar()) {
            for (FormationEntry formationentry : this.entries) {
                if (formationentry == null) continue;
                if (formationentry.train.isControlCar()) {
                    this.controlCar = formationentry.train;
                    break;
                }
            }
        }
        return this.controlCar;
    }

    public int getNotch() {
        return this.getControlCar() == null ? 0 : this.getControlCar().getNotch();
    }

    public void setSpeed(float par1) {
        if (par1 != this.speed) {
            for (FormationEntry formationentry : this.entries) {
                if (formationentry == null) continue;
                formationentry.train.setSpeed_NoSync(par1);
            }

            this.speed = par1;
        }
    }

    public void setTrainDirection(byte par1, EntityTrainBase par2) {
        if (par2.getTrainDirection() != par1) {
            this.setSpeed(-par2.getSpeed());
        }

        FormationEntry formationentry = this.getEntry(par2);
        if (formationentry != null) {
            this.direction = (byte) (par1 ^ formationentry.dir);
            byte b0 = 0;

            for (FormationEntry formationentry2 : this.entries) {
                if (formationentry2 != null) {
                    b0 = (byte) (this.direction ^ formationentry2.dir);
                    formationentry2.train.setTrainDirection_NoSync(b0);
                }
            }

        }
    }

    public void setTrainStateData(TrainState.TrainStateType type, byte data, EntityTrainBase par2) {
        if (type == TrainState.TrainStateType.Role) {
            for (FormationEntry formationentry : this.entries) {
                if (formationentry == null) continue;
                if (data == TrainState.Role_Front.data || data == TrainState.Role_Back.data) {
                    this.controlCar = par2;
                    par2.setTrainStateData_NoSync(type, (par2.getCabDirection() == par2.getTrainDirection()) ? data : (byte) (data ^ 2));
                    par2.setTrainDirection(par2.getCabDirection());
                }

                if (par2.equals(formationentry.train)) {
                    formationentry.train.setTrainStateData_NoSync(type, data);
                } else if (formationentry.train.getVehicleState(TrainState.TrainStateType.Role) != TrainState.Role_Center.data) {
                    formationentry.train.setTrainStateData_NoSync(type, TrainState.Role_Center.data);
                }
            }
        } else if (type == TrainState.TrainStateType.Door) {
            data &= 3;
            byte swapped = (byte) (((data & 1) << 1) | ((data >> 1) & 1));
            for (FormationEntry formationentry : this.entries) {
                if (formationentry == null) continue;
                byte value = par2.getTrainDirection() != formationentry.train.getTrainDirection() ? swapped : data;
                formationentry.train.setTrainStateData_NoSync(type, value);
            }
        } else {
            for (FormationEntry formationentry : this.entries) {
                if (formationentry == null) continue;
                formationentry.train.setTrainStateData_NoSync(type, data);
            }
        }
    }

    public boolean containBogie(EntityBogie bogie) {
        for (FormationEntry formationentry : this.entries) {
            if (formationentry == null) continue;
            EntityTrainBase entitytrainbase = formationentry.train;
            if (entitytrainbase.getBogie(0) == bogie || entitytrainbase.getBogie(1) == bogie) {
                return true;
            }
        }
        return false;
    }

    private void sendPacket() {
        RTMCore.NETWORK_WRAPPER.sendToAll(new PacketFormation(this));
    }

    public boolean isFrontCar(EntityTrainBase train) {
        int index = this.direction == 0 ? 0 : this.entries.length - 1;
        if (this.entries[index] != null) {
            EntityTrainBase front = this.entries[index].train;
            return train.equals(front);
        }
        return false;
    }

    public void updateTrainMovement() {
        EntityTrainBase prevTrain = null;
        for (int i = 0; i < this.entries.length; ++i) {
            int index = this.direction == 0 ? i : this.entries.length - i - 1;
            if (this.entries[index] == null) {
                continue;
            }

            EntityTrainBase train = this.entries[index].train;
            MotionState state = MotionState.STOP;
            if (train.existBogies()) {
                state = train.bogieController.moveTrainWithBogie(train, prevTrain, this.speed, false);
            }
            train.updateTrainMovement(state);
            prevTrain = train;
        }
    }
}