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

package jp.ngt.mcte.block;

import jp.ngt.mcte.item.ItemMiniature;
import jp.ngt.mcte.item.ItemMiniature.MiniatureMode;
import jp.ngt.mcte.world.MCTEWorld;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.block.TileEntityPlaceable;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMiniature extends TileEntityPlaceable implements ITickable {
    public NGTObject blocksObject;
    public float scale;
    public float offsetX, offsetY, offsetZ;
    public MiniatureMode mode;
    public byte attachSide;
    private MiniatureBlockState state;
    public final RSPortSet port = new RSPortSet();

    private AxisAlignedBB selectBox;
    private List<AxisAlignedBB> collisionBoxes;

    private MCTEWorld dummyWorld;
    @SideOnly(Side.CLIENT)
    public GLObject[] glLists;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.blocksObject = ItemMiniature.getNGTObject(nbt);
        this.scale = ItemMiniature.getScale(nbt);
        this.offsetX = nbt.getFloat("OffsetX");
        this.offsetY = nbt.getFloat("OffsetY");
        this.offsetZ = nbt.getFloat("OffsetZ");
        this.mode = MiniatureMode.values()[nbt.getByte("Mode")];
        if (nbt.hasKey("AttachSide")) {
            this.attachSide = nbt.getByte("AttachSide");
        } else {
            this.attachSide = 1;
        }

        if (nbt.hasKey("MBState")) {
            this.state = MiniatureBlockState.readFromNBT(nbt.getCompoundTag("MBState"), this);
        } else {
            this.state = MiniatureBlockState.create(this);
            this.state.lightValue = nbt.getByte("LightValue");
        }

        if (this.getWorld() != null && this.getWorld().isRemote) {
            this.getWorld().checkLight(this.getPos());//明るさの更新
        }

        this.dummyWorld = null;
        this.updateAABB();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        ItemMiniature.setScale(this.scale, nbt);
        if (this.blocksObject != null) {
            ItemMiniature.setNGTObject(this.blocksObject, nbt);
        }
        nbt.setFloat("OffsetX", this.offsetX);
        nbt.setFloat("OffsetY", this.offsetY);
        nbt.setFloat("OffsetZ", this.offsetZ);
        nbt.setByte("Mode", (byte) this.mode.id);
        nbt.setByte("AttachSide", this.attachSide);
        nbt.setTag("MBState", this.state.writeToNBT());
        return nbt;
    }

    @Override
    public void update() {
        if (!this.getWorld().isRemote) {
            this.getDummyWorld().tick();

            if (this.getDummyWorld().needsSync()) {
                this.sendPacket();
            }
        }
    }

    public MCTEWorld getDummyWorld() {
        if (this.dummyWorld == null && this.blocksObject != null) {
            BlockPos pos = this.getPos();
            this.dummyWorld = new MCTEWorld(this.getWorld(), this.blocksObject, pos.getX(), pos.getY(), pos.getZ());
        }
        return this.dummyWorld;
    }

    public void setBlockState(NGTObject par1, float par2, float x, float y, float z, MiniatureMode par6) {
        this.blocksObject = par1;
        this.scale = par2;
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        this.mode = par6;
        this.sendPacket();
        this.markDirty();
    }

    public MiniatureBlockState getMBState() {
        return this.state != null ? this.state : MiniatureBlockState.create(this);
    }

    public void setMBState(MiniatureBlockState par1) {
        this.state = par1;
    }

	/*public int getLightValue()
	{
		return this.lightValue;
		//return (this.blocksObject != null) ? this.blocksObject.getLightValue() : 0;
	}*/

    public AxisAlignedBB getSelectBox(int x, int y, int z) {
        if (this.selectBox == null) {
            if (this.state == null) {
                AxisAlignedBB aabb = new AxisAlignedBB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
                aabb.offset((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D);
                return aabb;
            }

            AxisAlignedBB aabb = this.state.getSelectBox();
            this.rotateAABB(aabb);
            aabb.offset((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D);
            this.selectBox = aabb;
        }
        return this.selectBox;
    }

    public List<AxisAlignedBB> getCollisionBoxes(int x, int y, int z) {
        if (this.collisionBoxes == null) {
            if (this.state == null) {
                List<AxisAlignedBB> list = new ArrayList<>();
                AxisAlignedBB aabb = new AxisAlignedBB(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);
                aabb.offset((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D);
                list.add(aabb);
                return list;
            }

            List<AxisAlignedBB> list = this.state.getCollisionBoxes();
            this.collisionBoxes = new ArrayList<AxisAlignedBB>();
            for (AxisAlignedBB aabb : list) {
                AxisAlignedBB aabb2 = this.rotateAABB(aabb);
                aabb2 = aabb2.offset((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D);
                this.collisionBoxes.add(aabb2);
            }
        }
        return this.collisionBoxes;
    }

    private AxisAlignedBB rotateAABB(AxisAlignedBB aabb) {
        Vec3 vecMin = new Vec3(aabb.minX, aabb.minY, aabb.minZ);
        Vec3 vecMax = new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
        //東西南北方向のみに固定
        float f0 = this.getRotation();
        switch (this.attachSide) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                f0 *= -1.0F;
                break;
            case 3:
                f0 *= -1.0F;
                break;
            case 4:
                f0 *= -1.0F;
                break;
            case 5:
                f0 *= -1.0F;
                break;
        }
        f0 = (float) NGTMath.floor((f0 + 45.0F) / 90.0F) * 90.0F;
        float yaw = NGTMath.toRadians(f0);
        vecMin = vecMin.rotateAroundY(yaw);
        vecMax = vecMax.rotateAroundY(yaw);
        float ro;
        switch (this.attachSide) {
            case 0://yNeg
                ro = NGTMath.toRadians(180.0F);
                vecMin = vecMin.rotateAroundZ(ro);
                vecMax = vecMax.rotateAroundZ(ro);
                break;
            case 1://yPos
                break;
            case 2://z
                ro = NGTMath.toRadians(-90.0F);
                vecMin = vecMin.rotateAroundX(ro);
                vecMax = vecMax.rotateAroundX(ro);
                break;
            case 3://z
                ro = NGTMath.toRadians(90.0F);
                vecMin = vecMin.rotateAroundX(ro);
                vecMax = vecMax.rotateAroundX(ro);
                break;
            case 4://x
                ro = NGTMath.toRadians(90.0F);
                vecMin = vecMin.rotateAroundZ(ro);
                vecMax = vecMax.rotateAroundZ(ro);
                break;
            case 5://x
                ro = NGTMath.toRadians(-90.0F);
                vecMin = vecMin.rotateAroundZ(ro);
                vecMax = vecMax.rotateAroundZ(ro);
                break;
        }
        double minX = vecMin.getX() < vecMax.getX() ? vecMin.getX() : vecMax.getX();
        double minY = vecMin.getY() < vecMax.getY() ? vecMin.getY() : vecMax.getY();
        double minZ = vecMin.getZ() < vecMax.getZ() ? vecMin.getZ() : vecMax.getZ();
        double maxX = vecMin.getX() > vecMax.getX() ? vecMin.getX() : vecMax.getX();
        double maxY = vecMin.getY() > vecMax.getY() ? vecMin.getY() : vecMax.getY();
        double maxZ = vecMin.getZ() > vecMax.getZ() ? vecMin.getZ() : vecMax.getZ();
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void setRotation(float par1, boolean synch) {
        super.setRotation(par1, synch);
        this.updateAABB();
    }

    /**
     * 当たり判定再生成
     */
    private void updateAABB() {
        this.selectBox = null;
        this.collisionBoxes = null;
    }

    @Override
    public void onChunkUnload() {
        if (this.getWorld().isRemote) {
            this.deleteGLList();
        }
    }

    @Override
    public void invalidate() {
        if (this.getWorld().isRemote) {
            this.deleteGLList();
        }
    }

    @SideOnly(Side.CLIENT)
    private void deleteGLList() {
        if (this.glLists != null) {
            GLHelper.deleteGLList(this.glLists[0]);
            GLHelper.deleteGLList(this.glLists[1]);
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass >= 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        //return 16384.0D;//128^2
        double d0 = NGTUtil.getChunkLoadDistanceSq() * 0.5D;
        if (this.blocksObject != null) {
            if (d0 < (double) this.blocksObject.xSize) {
                d0 = (double) this.blocksObject.xSize;
            }

            if (d0 < (double) this.blocksObject.ySize) {
                d0 = (double) this.blocksObject.ySize;
            }

            if (d0 < (double) this.blocksObject.zSize) {
                d0 = (double) this.blocksObject.zSize;
            }
        }
        return d0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        if (this.blocksObject == null) {
            return super.getRenderBoundingBox();
        }

        //回転してる時はoffsetを単純に計算出来ない
        if (this.offsetX != 0.0F || this.offsetY != 0.0F || this.offsetZ != 0.0F) {
            return INFINITE_EXTENT_AABB;
        }

        double sc = (double) this.scale;
        double x0 = (double) this.blocksObject.xSize * sc * 0.5D;
        double y0 = (double) this.blocksObject.ySize * sc;
        double z0 = (double) this.blocksObject.zSize * sc * 0.5D;
		/*double px = (double)this.getX() + 0.5D + (double)this.offsetX * sc;
		double py = (double)this.getY() + 0.5D + (double)this.offsetY * sc;
		double pz = (double)this.getZ() + 0.5D + (double)this.offsetZ * sc;*/
        BlockPos pos = this.getPos();
        double px = (double) pos.getX() + 0.5D;
        double py = (double) pos.getY();
        double pz = (double) pos.getZ() + 0.5D;
        return new AxisAlignedBB(px - x0, py, pz - z0, px + x0, py + y0, pz + z0);
    }
}