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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityPlaceable;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.modelset.TextureSetFlag;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityFlag extends TileEntityPlaceable implements IResourceSelector, ITickable {
    private ResourceState<TextureSetFlag> state = new ResourceState<>(RTMResource.FLAG, this);

    @SideOnly(Side.CLIENT)
    public int tick;
    @SideOnly(Side.CLIENT)
    public float wave;

    public TileEntityFlag() {
        if (!NGTUtil.isServer()) {
            //上で初期化するとNoSuchFieldError
            this.wave = (float) NGTMath.RANDOM.nextInt(360);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.state.readFromNBT(nbt.getCompoundTag("State"));

        if (this.state.version < 1)//互換
        {
            this.getResourceState().setResourceName(nbt.getString("TextureName"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("State", this.state.writeToNBT());
        return nbt;
    }

    @Override
    public void update() {
        if (this.world.isRemote) {
            this.wave += 10.0F;
            if (this.wave >= 360.0F) {
                this.wave = 0.0F;
            }

            ++this.tick;
            if (this.tick >= 36000) {
                this.tick = 0;
            }
        }
    }

    @Override
    public void updateResourceState() {
        if (this.world == null || !this.world.isRemote) {
            this.sendPacket();
            this.markDirty();
            BlockUtil.markBlockForUpdate(this.getWorld(), this.getPos());
        }
    }

    @Override
    public ResourceState<TextureSetFlag> getResourceState() {
        return this.state;
    }

    @Override
    public int[] getSelectorPos() {
        return new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()};
    }

    @Override
    public boolean closeGui(ResourceState par1) {
        return true;
    }
}