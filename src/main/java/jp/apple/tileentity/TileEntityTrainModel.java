package jp.apple.tileentity;

import jp.ngt.ngtlib.block.TileEntityPlaceable;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityTrainModel extends TileEntityPlaceable implements IResourceSelector<ModelSetTrain> {

    private final ResourceState<ModelSetTrain> resourceState;
    public float rotationYaw;

    public TileEntityTrainModel() {
        this.resourceState = new ResourceState<>(
                jp.ngt.rtm.RTMResource.TRAIN_EC,
                this
        );
        if (this.resourceState.getResourceName() == null) {
            this.resourceState.setResourceName("Dummy");
        }
    }

    @Override
    public ResourceState<ModelSetTrain> getResourceState() {
        return this.resourceState;
    }

    @Override
    public void updateResourceState() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos,
                    this.world.getBlockState(this.pos),
                    this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public int[] getSelectorPos() {
        BlockPos pos = this.getPos();
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    @Override
    public boolean closeGui(ResourceState par1) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound); 
        if (compound.hasKey("State")) {
            this.resourceState.readFromNBT(compound.getCompoundTag("State"));
        } else {
            this.resourceState.readFromNBT(compound);
        }
        this.rotationYaw = compound.getFloat("RotationYaw");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound); 
        compound.setTag("State", this.resourceState.writeToNBT());
        compound.setFloat("RotationYaw", this.rotationYaw);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(this.pos, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }
}