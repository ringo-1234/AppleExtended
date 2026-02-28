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

package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.modelset.ModelSetWire;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Connection {
    private final ResourceState<ModelSetWire> state = new ResourceState<>(RTMResource.WIRE, null);
    public boolean isRoot;
    public ConnectionType type;
    public int x;
    public int y;
    public int z;

    private Object connectedObject;

    public Connection() {
    }

    public Connection(boolean b, int x2, int y2, int z2, ConnectionType type2, ResourceState par6) {
        this();
        this.isRoot = b;
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.type = type2;
        if (par6 != null) {
            this.state.readFromNBT(par6.writeToNBT());
        }
    }

    public static List<Connection> readListFromNBT(NBTTagCompound nbt) {
        List<Connection> list = new ArrayList<Connection>();
        NBTTagList tagList = nbt.getTagList("connections", 10);
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbt0 = tagList.getCompoundTagAt(i);
            Connection connection = new Connection();
            connection.readFromNBT(nbt0);
            list.add(connection);
        }
        return list;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.state.readFromNBT(nbt.getCompoundTag("State"));
        this.isRoot = nbt.getBoolean("IsRoot");
        if (!nbt.hasKey("IsRoot"))//v34互換
        {
            this.isRoot = true;
        }
        this.x = nbt.getInteger("x");
        this.y = nbt.getInteger("y");
        this.z = nbt.getInteger("z");

        int typeId = nbt.getInteger("type");
        if (this.state.version <= 0) {
            String name = nbt.getString("ModelName");
            if (name.isEmpty()) {
                //v34互換
                switch (typeId) {
                    case 1:
                        name = "BasicWireBlack";
                        break;
                    case 2:
                        name = "SimpleCatenary";
                        typeId = 1;
                        break;
                    case 50:
                        name = "BasicWireBlack";
                        break;
                    case 51:
                        name = "SimpleCatenary";
                        typeId = 50;
                        break;
                }
            }
            this.state.setResourceName(name);
        }
        this.type = ConnectionType.getType(typeId);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("State", this.state.writeToNBT());
        nbt.setBoolean("IsRoot", this.isRoot);
        nbt.setInteger("x", this.x);
        nbt.setInteger("y", this.y);
        nbt.setInteger("z", this.z);
        nbt.setInteger("type", this.type.id);
    }

    public static void writeListToNBT(NBTTagCompound nbt, List<Connection> list) {
        NBTTagList tagList = new NBTTagList();
        for (Connection connection : list) {
            NBTTagCompound nbt0 = new NBTTagCompound();
            connection.writeToNBT(nbt0);
            tagList.appendTag(nbt0);
        }
        nbt.setTag("connections", tagList);
    }

    public ResourceState<ModelSetWire> getResourceState() {
        return this.state;
    }

    public TileEntityElectricalWiring getElectricalWiring(World world) {
        if (this.type == ConnectionType.WIRE || this.type == ConnectionType.TO_ENTITY) {
            if (this.connectedObject != null && this.connectedObject instanceof TileEntityElectricalWiring) {
                return (TileEntityElectricalWiring) this.connectedObject;
            } else {
                if (this.type == ConnectionType.TO_ENTITY) {
                    this.connectedObject = TileEntityElectricalWiring.getWireEntity(world, x, y, z);
                    return (TileEntityElectricalWiring) this.connectedObject;
                } else {
                    TileEntity te = BlockUtil.getTileEntity(world, this.x, this.y, this.z);
                    if (te instanceof TileEntityElectricalWiring) {
                        this.connectedObject = (TileEntityElectricalWiring) te;
                        return (TileEntityElectricalWiring) this.connectedObject;
                    }
                }
            }
        }

        return null;
    }

    public IProvideElectricity getIProvideElectricity(World world) {
        if (this.type == ConnectionType.DIRECT) {
            TileEntity te = BlockUtil.getTileEntity(world, this.x, this.y, this.z);
            if (te instanceof IProvideElectricity) {
                return (IProvideElectricity) te;
            }
        }
        return null;
    }

    public EntityPlayer getPlayer(World world) {
        if (type == ConnectionType.TO_PLAYER) {
            if (this.isAvailable(world)) {
                return (EntityPlayer) this.connectedObject;
            }
        }
        return null;
    }

    /**
     * 接続が有効かどうか
     */
    public boolean isAvailable(World world) {
        if (this.type == ConnectionType.DIRECT) {
            return true;
        }

        if (this.type == ConnectionType.TO_PLAYER) {
            if (this.connectedObject instanceof EntityPlayer) {
                return TileEntityElectricalWiring.getWireType((EntityPlayer) this.connectedObject) != null;
            } else {
                Entity entity = world.getEntityByID(this.x);
                if (entity instanceof EntityPlayer) {
                    this.connectedObject = entity;
                    return true;
                }
            }
            return false;
        }

        if (this.connectedObject == null) {
            this.getElectricalWiring(world);
        }

        if (this.connectedObject instanceof TileEntityDummyEW) {
            TileEntityDummyEW tile = (TileEntityDummyEW) this.connectedObject;
            if (tile.entityEW == null || tile.entityEW.isDead) {
                return false;
            }
        } else if (this.connectedObject instanceof TileEntityElectricalWiring) {
            return !((TileEntityElectricalWiring) this.connectedObject).isInvalid();
        }

        return this.connectedObject != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Connection) {
            Connection con = (Connection) obj;
            return this.type == con.type && this.x == con.x && this.y == con.y && this.z == con.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((this.x & 0xFF) << 16) | ((this.y & 0xFF) << 8) | ((this.z & 0xFF));
    }

    /**
     * 0:接続なし,
     * 1:普通の電線,
     * 3:直接接続,
     * 4:直接接続(ATCなど),
     * 50:Player{eID, -1, 0} (電線),
     */
    public enum ConnectionType {
        NONE(0, false),
        WIRE(1, true),
        DIRECT(3, false),
        TO_ENTITY(4, true),
        TO_PLAYER(50, true);

        public final byte id;
        public final boolean isVisible;

        private ConnectionType(int par1, boolean par2) {
            this.id = (byte) par1;
            this.isVisible = par2;
        }

        public static ConnectionType getType(int par1) {
            for (ConnectionType type : ConnectionType.values()) {
                if (type.id == par1) {
                    return type;
                }
            }
            return NONE;
        }
    }
}