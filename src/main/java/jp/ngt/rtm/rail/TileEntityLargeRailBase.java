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

package jp.ngt.rtm.rail;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.protection.Lockable;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.network.PacketLargeRailBase;
import jp.ngt.rtm.rail.util.RailMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityLargeRailBase extends TileEntityCustom implements ILargeRail, Lockable, ITickable {
    private static final int SPLIT = 128;

    protected int[] startPoint = new int[3];
    private boolean finishSetupBlockBounds;
    private float[] blockHeights;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        int x = nbt.getInteger("spX");
        int y = nbt.getInteger("spY");
        int z = nbt.getInteger("spZ");
        this.setStartPoint(x, y, z);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("spX", this.startPoint[0]);
        nbt.setInteger("spY", this.startPoint[1]);
        nbt.setInteger("spZ", this.startPoint[2]);
        return nbt;
    }

    public int[] getStartPoint() {
        return this.startPoint;
    }

    public void setStartPoint(int x, int y, int z) {
        this.startPoint[0] = x;
        this.startPoint[1] = y;
        this.startPoint[2] = z;
        if (this.getWorld() == null || !this.getWorld().isRemote) {
            this.markDirty();
            this.sendPacket();
        }
    }

    public boolean isTrainOnRail() {
        TileEntityLargeRailCore tile = this.getRailCore();
        if (tile != null) {
            return tile.isCollidedTrain;
        }
        return false;
    }

    @Override
    protected void sendPacket() {
        RTMCore.NETWORK_WRAPPER.sendToAll(new PacketLargeRailBase(this));
    }

    @Override
    public void update() {
        if (this.world.isRemote) {
            if (!this.finishSetupBlockBounds && this.getRailCore() != null) {
                BlockUtil.markBlockForUpdate(this.getWorld(), this.getPos());
                this.finishSetupBlockBounds = true;
            }
        }
    }

    @Override
    public RailMap getRailMap(Entity entity) {
        TileEntityLargeRailCore tile = this.getRailCore();
        if (tile != null) {
            return ((ILargeRail) tile).getRailMap(entity);
        }
        return null;
    }

    public TileEntityLargeRailCore getRailCore() {
        TileEntity tile = BlockUtil.getTileEntity(this.getWorld(), this.startPoint[0], this.startPoint[1], this.startPoint[2]);
        if (tile != null && tile instanceof TileEntityLargeRailCore) {
            return (TileEntityLargeRailCore) tile;
        }
        return null;
    }

    public static TileEntityLargeRailBase getRailFromCoordinates(World world, double px, double py, double pz, int minY) {
        int x = NGTMath.floor(px);
        int y = NGTMath.floor(py);
        int z = NGTMath.floor(pz);
        while (y > minY) {
            Block block = BlockUtil.getBlock(world, x, y, z);
            if (block instanceof BlockLargeRailBase) {
                break;
            }
            --y;
        }

        TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
        if (tile != null && tile instanceof TileEntityLargeRailBase) {
            return (TileEntityLargeRailBase) tile;
        }
        return null;
    }

    public static RailMap getRailMapFromCoordinates(World world, Entity entity, double px, double py, double pz) {
        TileEntityLargeRailBase rail = TileEntityLargeRailBase.getRailFromCoordinates(world, px, py, pz, 0);
        if (rail != null) {
            return rail.getRailMap(entity);
        }
        return null;
    }

    @Override
    public Block getBlockType() {
        if (this.blockType == null) {
            Block block = this.world.getBlockState(this.getPos()).getBlock();
            if (block instanceof BlockLargeRailBase) {
                this.blockType = block;
            }
        }

        return this.blockType;
    }

    public float[] getBlockHeights(int x, int y, int z, float defaultHeight, boolean useCache) {
        if (useCache && this.blockHeights != null) {
            return this.blockHeights;
        }

        if (this.finishSetupBlockBounds || !useCache) {
            float[] fa = this.getBlockHeights(x, y, z, defaultHeight);
            if (fa != null) {
                if (useCache) {
                    this.blockHeights = fa;
                    if (!this.world.isRemote) {
                        this.finishSetupBlockBounds = true;
                    }
                }
                return fa;
            }
        }

        float f0 = BlockLargeRailBase.THICKNESS;
        return new float[]{f0, f0, f0, f0};
    }

    private float[] getBlockHeights(int x, int y, int z, float defaultHeight) {
        TileEntityLargeRailCore core = BlockLargeRailBase.getCore(world, new BlockPos(x, y, z));
        if (core == null) {
            return null;
        }

        RailMap[] rms = core.getAllRailMaps();
        if (rms == null) {
            return null;
        }

        float[] fa = new float[]{defaultHeight, defaultHeight, defaultHeight, defaultHeight};
        for (int i = 0; i < fa.length; ++i) {
            int x0 = x + ((i == 1 || i == 2) ? 1 : 0);
            int z0 = z + ((i == 0 || i == 1) ? 1 : 0);
            double distanceSq = Double.MAX_VALUE;

            for (RailMap rm : rms) {
                if (rm == null) return null;
                int index = rm.getNearlestPoint(SPLIT, x0, z0);
                if (index < 0) {
                    index = 0;
                }

                double[] rpos = rm.getRailPos(SPLIT, index);
                double dSq2 = NGTMath.getDistanceSq(x0, z0, rpos[1], rpos[0]);
                if (dSq2 < distanceSq) {
                    distanceSq = dSq2;

                    double height = rm.getRailHeight(SPLIT, index);
                    float yaw = rm.getRailRotation(SPLIT, index);
                    float cant = rm.getCant(SPLIT, index);
                    float yaw2 = (float) NGTMath.toDegrees(Math.atan2(rpos[1] - x0, rpos[0] - z0));
                    double len = Math.sqrt((rpos[1] - x0) * (rpos[1] - x0) + (rpos[0] - z0) * (rpos[0] - z0));
                    boolean dirFlag = NGTMath.wrapAngle(yaw2 - yaw) > 0.0F;
                    double h2 = NGTMath.sin(cant) * len * (dirFlag ? -1.0F : 1.0F);
                    fa[i] = (float) (height - (double) y + h2);
                }
            }
        }
        return fa;
    }

    public boolean isReberbSound() {
        TileEntityLargeRailCore core = this.getRailCore();
        if (core != null) {
            ResourceStateRail property = core.getResourceState();
            if (!property.getBlockState().isOpaqueCube()) {
                IBlockState state = this.getWorld().getBlockState(this.getPos().down());
                if (!state.isOpaqueCube()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setPos(int x, int y, int z, int prevX, int prevY, int prevZ) {
        int difX = x - prevX;
        int difY = y - prevY;
        int difZ = z - prevZ;
        this.startPoint[0] += difX;
        this.startPoint[1] += difY;
        this.startPoint[2] += difZ;
        super.setPos(x, y, z, prevX, prevY, prevZ);
    }

    @Override
    public Object getTarget(World world, int x, int y, int z) {
        return this.getRailCore();
    }

    @Override
    public boolean lock(EntityPlayer player, String code) {
        return true;
    }

    @Override
    public boolean unlock(EntityPlayer player, String code) {
        return true;
    }

    @Override
    public int getProhibitedAction() {
        return 1;
    }
}