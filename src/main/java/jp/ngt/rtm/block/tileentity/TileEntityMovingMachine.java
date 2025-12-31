package jp.ngt.rtm.block.tileentity;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.NGTOUtil;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.math.AABBInt;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.world.NGTWorld;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.EntityMMBoundingBox;
import jp.ngt.rtm.entity.vehicle.EntityCar;
import jp.ngt.rtm.entity.vehicle.EntityPlane;
import jp.ngt.rtm.entity.vehicle.EntityShip;
import jp.ngt.rtm.entity.vehicle.EntityVehicle;
import jp.ngt.rtm.entity.vehicle.VehicleNGTO;
import jp.ngt.rtm.network.PacketMoveMM;
import jp.ngt.rtm.network.PacketNotice;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMovingMachine extends TileEntityCustom implements ITickable
{
	public boolean guideVisibility = true;
	public int width, height, depth;
	public int offsetX, offsetY, offsetZ;
	public VehicleNGTO vngto = new VehicleNGTO(null, 0.5F, 0.5F, 0.5F, 1.0F);
	public float speed = 0.0625F;

	/**相対座標*/
	public int pairBlockX, pairBlockY, pairBlockZ;
	public boolean isCore;

	/**移動してるオブジェクトの相対座標*/
	public double posX, posY, posZ;
	public double prevPosX, prevPosY, prevPosZ;
	/**1:終点方向, 0:停止, -1:始点方向, -2:ブロック再設置*/
	public byte moveDir;

	/**移動速度*/
	private double motionX, motionY, motionZ;
	private List<EntityMMBoundingBox> bbList = new ArrayList<EntityMMBoundingBox>();
	private int[] bbIds;

	public NGTObject blocksObject;
	@SideOnly(Side.CLIENT)
	public World dummyWorld;
	@SideOnly(Side.CLIENT)
	public GLObject[] glLists;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.isCore = nbt.getBoolean("IsCore");
        int[] ia0 = nbt.getIntArray("Range");
        this.width = ia0[0];
        this.height = ia0[1];
        this.depth = ia0[2];
        int[] ia2 = nbt.getIntArray("Offset");
        this.offsetX = ia2[0];
        this.offsetY = ia2[1];
        this.offsetZ = ia2[2];
        this.vngto = VehicleNGTO.readFromNBT(nbt.getCompoundTag("VNGTO"), true);
        int[] ia1 = nbt.getIntArray("PairBlock");
        this.pairBlockX = ia1[0];
        this.pairBlockY = ia1[1];
        this.pairBlockZ = ia1[2];
        this.speed = nbt.getFloat("Speed");
        this.posX = nbt.getDouble("PosX");
        this.posY = nbt.getDouble("PosY");
        this.posZ = nbt.getDouble("PosZ");
        this.moveDir = nbt.getByte("MoveDir");
        this.guideVisibility = nbt.getBoolean("GuideVisibility");

        if(this.world == null && this.moveDir != 0 && nbt.hasKey("NGTO"))
        {
        	this.blocksObject = NGTObject.readFromNBT(nbt.getCompoundTag("NGTO"));
        	this.moveDir = -2;
        }

        this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.getWorld() == null || !this.getWorld().isRemote)
		{
			this.sendPacket();
			this.markDirty();
		}
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("IsCore", this.isCore);
        nbt.setIntArray("Range", new int[]{this.width, this.height, this.depth});
        nbt.setIntArray("Offset", new int[]{this.offsetX, this.offsetY, this.offsetZ});
        nbt.setTag("VNGTO", this.vngto.writeToNBT());
        nbt.setIntArray("PairBlock", new int[]{this.pairBlockX, this.pairBlockY, this.pairBlockZ});
        nbt.setFloat("Speed", this.speed);
        nbt.setDouble("PosX", this.posX);
        nbt.setDouble("PosY", this.posY);
        nbt.setDouble("PosZ", this.posZ);
        nbt.setByte("MoveDir", this.moveDir);
        nbt.setBoolean("GuideVisibility", this.guideVisibility);
        if(this.moveDir != 0 && this.blocksObject != null)
        {
        	nbt.setTag("NGTO", this.blocksObject.writeToNBT());
        }
        return nbt;
    }

	@Override
	public void update()
    {
		if(!this.isCore){return;}

		if(this.moveDir == 1 || this.moveDir == -1)
		{
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;

			boolean flag = false;
			if(this.moveDir == 1)
			{
				if(Math.abs(this.posX) > (double)Math.abs(this.pairBlockX))
				{
					this.posX = this.pairBlockX;
					flag = true;
				}

				if(Math.abs(this.posY) > (double)Math.abs(this.pairBlockY))
				{
					this.posY = this.pairBlockY;
					flag = true;
				}

				if(Math.abs(this.posZ) > (double)Math.abs(this.pairBlockZ))
				{
					this.posZ = this.pairBlockZ;
					flag = true;
				}
			}
			else
			{
				if(Math.abs((double)this.pairBlockX - this.posX) > (double)Math.abs(this.pairBlockX))
				{
					this.posX = 0.0D;
					flag = true;
				}

				if(Math.abs((double)this.pairBlockY - this.posY) > (double)Math.abs(this.pairBlockY))
				{
					this.posY = 0.0D;
					flag = true;
				}

				if(Math.abs((double)this.pairBlockZ - this.posZ) > (double)Math.abs(this.pairBlockZ))
				{
					this.posZ = 0.0D;
					flag = true;
				}
			}

			this.moveEntities();

			if(flag && !this.world.isRemote)
			{
				this.setMovement((byte)0);
			}
		}
		else if(this.moveDir == -2)
		{
			this.moveDir = 0;
			this.editBlock(1);
			this.onBlockChanged();
		}
    }

	private void moveEntities()
	{
		if(!this.world.isRemote)
		{
			double mX = this.posX - this.prevPosX;
			double mY = this.posY - this.prevPosY;
			double mZ = this.posZ - this.prevPosZ;

			//MMBBを全て動かしてから各Entityの当たり判定処理

			for(int pass = 0; pass < 2; ++pass)
			{
				for(int i = 0; i < this.bbList.size(); ++i)
				{
					EntityMMBoundingBox entity = this.bbList.get(i);
					switch(pass)
					{
					case 0: entity.setPosition(entity.posX + mX, entity.posY + mY, entity.posZ + mZ);break;
					case 1: entity.moveMM(mX, mY, mZ);break;
					}
				}
			}

			//同期は後で
			if(this.bbIds != null)
			{
				RTMCore.NETWORK_WRAPPER.sendToAll(new PacketMoveMM(this.bbIds, mX, mY, mZ));
			}
		}
	}

	/**Server Only*/
	public void onBlockChanged()
	{
		if(!this.hasPair()){return;}

		if(!this.isCore){this.getCore().onBlockChanged();}

		int md = 0;
		boolean bs = this.world.isBlockIndirectlyGettingPowered(this.getPos()) > 0;
		boolean be = this.world.isBlockIndirectlyGettingPowered(this.getPos().add(this.pairBlockX, this.pairBlockY, this.pairBlockZ)) > 0;
		if(be && !bs)
		{
			md = 1;
		}
		else if(bs && !be)
		{
			md = -1;
		}
		else
		{
			md = 0;
		}

		if(this.moveDir != md)
		{
			this.setMovement((byte)md);
		}
	}

	public void setMovement(byte par1)
	{
		this.moveDir = par1;
		if(par1 == 0)
		{
			this.motionX = this.motionY = this.motionZ = 0.0D;
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.editBlock(1);
		}
		else
		{
			Vec3 vec = PooledVec3.create(this.pairBlockX, this.pairBlockY, this.pairBlockZ).normalize();
			double d0 = (par1 == 1) ? 1.0D : -1.0D;
			this.motionX = vec.getX() * (double)this.speed * d0;
			this.motionY = vec.getY() * (double)this.speed * d0;
			this.motionZ = vec.getZ() * (double)this.speed * d0;
			this.editBlock(0);
		}

		if(!this.world.isRemote)
		{
			this.sendPacket();
			this.markDirty();

			String s = "MM," + par1;
			RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, s, this));
		}
	}

	/**
	 * @param mode 0:DelBlocks, 1:PutBlocks
	 */
	private void editBlock(int mode)
	{
		if(mode == 1 && this.blocksObject == null){return;}

		List<BlockSet> list = new ArrayList<>();
		List<AxisAlignedBB> aabbList = new ArrayList<>();
		int x0 = this.getPos().getX() + (int)this.posX + 1;
		int y0 = this.getPos().getY() + (int)this.posY + 1;
		int z0 = this.getPos().getZ() + (int)this.posZ + 1;
		AABBInt box = new AABBInt(this.width, this.height, this.depth);
		for(int pass = 0; pass < 2; ++pass)
		{
			final int passF = pass;
			box.repeat((i, j, k, count)->{
				int x = x0 + i;
				int y = y0 + j;
				int z = z0 + k;
				if(mode == 0)
				{
					if(passF == 0)
					{
						list.add(this.getBlockSet(x, y, z));

						if(!this.world.isRemote)
    					{
							BlockPos pos = new BlockPos(x, y, z);
							IBlockState state = this.world.getBlockState(pos);
							Block block = state.getBlock();
							int meta = block.getMetaFromState(state);
							AxisAlignedBB aabb = null;
							if(block instanceof BlockLargeRailBase)
							{
								aabbList.clear();
								block.addCollisionBoxToList(state, this.world, pos,
										new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), aabbList, null, false);
								if(!aabbList.isEmpty())
								{
									aabb = aabbList.get(0);
								}
							}
							else
							{
								aabb = block.getBoundingBox(state, this.world, pos);
							}

							if(block.canCollideCheck(state, false) && aabb != null)
							{
								aabb = aabb.offset(-x, -y, -z);
								boolean b = BlockUtil.getBlock(this.getWorld(), x, y + 1, z) == Blocks.AIR;
    							EntityMMBoundingBox entity = new EntityMMBoundingBox(this.world, this, b);
    							entity.setPositionAndRotation((double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);
    							entity.setAABB(aabb);
	    						this.world.spawnEntity(entity);
	    						this.bbList.add(entity);
							}
    					}
					}
					else
					{
						if(!this.world.isRemote)
    					{
							BlockUtil.setAir(this.getWorld(), x, y, z);
    					}
					}
				}
				else
				{
					if(passF == 0)
					{
						if(!this.world.isRemote)
    					{
							BlockSet set = this.blocksObject.blockList.get(count);
							this.setBlockSet(set, x, y, z);
    					}
					}
				}
			});
		}

		if(!this.world.isRemote && mode == 1)
		{
			for(EntityMMBoundingBox entity : this.bbList)
			{
				entity.setDead();
			}
			this.bbList.clear();
			this.bbIds = null;
		}

		if(mode == 0)
		{
			this.blocksObject = NGTObject.createNGTO(list, this.width, this.height, this.depth, x0, y0, z0);
			if(this.world.isRemote)
			{
				this.setNewDummyWorld();
			}
			else
			{
				int size = this.bbList.size();
				this.bbIds = new int[size];
				for(int i = 0; i < size; ++i)
				{
					this.bbIds[i] = this.bbList.get(i).getEntityId();
				}
			}
		}
		else
		{
			this.blocksObject = null;
			if(this.world.isRemote && this.glLists != null)
			{
				GLHelper.deleteGLList(this.glLists[0]);
				GLHelper.deleteGLList(this.glLists[1]);
				this.glLists = null;
				this.dummyWorld = null;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void setNewDummyWorld()
	{
		int x = this.getPos().getX() + 1 + (int)this.posX;
		int y = this.getPos().getY() + 1 + (int)this.posY;
		int z = this.getPos().getZ() + 1 + (int)this.posZ;
		this.dummyWorld = new NGTWorld(NGTUtil.getClientWorld(), this.blocksObject, x, y, z);
	}

	private BlockSet getBlockSet(int x, int y, int z)
	{
		return BlockSet.getBlockSet(this.getWorld(), x, y, z, false);
	}

	private void setBlockSet(BlockSet set, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		BlockUtil.setBlock(this.getWorld(), pos, set.block, set.metadata, 3);

		if(set.block.hasTileEntity(this.getWorld().getBlockState(pos)))
		{
			TileEntity tile = this.world.getTileEntity(pos);
			if(tile != null)
			{
				int prevX = 0;
				int prevY = 0;
				int prevZ = 0;

				if(set.nbt != null)
				{
					NBTTagCompound nbt0 = (NBTTagCompound)set.nbt.copy();
					prevX = nbt0.getInteger("x");
					prevY = nbt0.getInteger("y");
					prevZ = nbt0.getInteger("z");
					nbt0.setInteger("x", x);
					nbt0.setInteger("y", y);
					nbt0.setInteger("z", z);
					tile.readFromNBT(nbt0);
				}

				if(tile instanceof TileEntityCustom)
				{
					((TileEntityCustom)tile).setPos(x, y, z, prevX, prevY, prevZ);
				}
				else
				{
					tile.setPos(pos);
				}
			}
		}
	}

	public void setData(int p1, int p2, int p3, int ox, int oy, int oz, float p4, boolean p5)
	{
		this.width = p1;
		this.height = p2;
		this.depth = p3;
		this.offsetX = ox;
		this.offsetY = oy;
		this.offsetZ = oz;
		this.speed = p4;
		this.guideVisibility = p5;
		this.sendPacket();
		this.markDirty();
	}

	public boolean hasPair()
	{
		return !(this.pairBlockX == 0 && this.pairBlockY == 0 && this.pairBlockZ == 0);
	}

	public TileEntityMovingMachine getPair()
	{
		BlockPos pos = this.getPos().add(this.pairBlockX, this.pairBlockY, this.pairBlockZ);
		return (TileEntityMovingMachine)this.world.getTileEntity(pos);
	}

	public void setPair(TileEntityMovingMachine par1)
	{
		this.pairBlockX = par1.getPos().getX() - this.getPos().getX();
		this.pairBlockY = par1.getPos().getY() - this.getPos().getY();
		this.pairBlockZ = par1.getPos().getZ() - this.getPos().getZ();
		this.sendPacket();
		this.markDirty();
	}

	public void searchMM(int x, int y, int z)
	{
		int range = 128;
		for(int i = -range; i < range; ++i)
		{
			for(int j = 0; j < 256; ++j)
			{
				for(int k = -range; k < range; ++k)
				{
					if(i == 0 && j == y && k == 0){continue;}

					BlockPos pos = new BlockPos(x + i, j, z + k);
					if(this.world.getBlockState(pos).getBlock() == RTMBlock.movingMachine)
					{
						TileEntityMovingMachine tile = (TileEntityMovingMachine)this.world.getTileEntity(pos);
						if(!tile.hasPair())
						{
							this.isCore = true;
							this.setPair(tile);
							tile.setPair(this);
							return;
						}
					}
				}
			}
		}
	}

	public TileEntityMovingMachine getCore()
	{
		return (this.isCore || !this.hasPair()) ? this : this.getPair();
	}

	public void reset(boolean par1)
	{
		if(par1 && this.hasPair())
		{
			TileEntityMovingMachine tile = this.getPair();
			if(tile != null)
			{
				tile.reset(false);
			}
		}

		this.isCore = false;
		this.pairBlockX = this.pairBlockY = this.pairBlockZ = 0;
		this.posX = this.posY = this.posZ = 0.0D;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.sendPacket();
		this.markDirty();
	}

	public void generateVehicle(EntityPlayer player)
	{
		if(this.getWorld().isRemote){return;}
		int startX = this.getX() + 1 + this.offsetX;
		int startY = this.getY() + 1 + this.offsetY;
		int startZ = this.getZ() + 1 + this.offsetZ;
		NGTObject ngto = NGTOUtil.copyBlocks(this.getWorld(), startX, startY, startZ, this.width, this.height, this.depth);

		AABBInt box = new AABBInt(this.width, this.height, this.depth);
		box.repeat((i, j, k, c)->{
			this.getWorld().setBlockToAir(new BlockPos(startX + i, startY + j, startZ + k));
		});

		EntityVehicle vehicle = null;
		switch(this.vngto.type)
		{
		case 0:
			vehicle = new EntityCar(this.getWorld());
			break;
		case 1:
			vehicle = new EntityShip(this.getWorld());
			break;
		case 2:
			vehicle = new EntityPlane(this.getWorld());
			break;
		default:
			vehicle = new EntityCar(this.getWorld());
		}
		vehicle.setPositionAndUpdate(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D);
		this.getWorld().spawnEntity(vehicle);
		VehicleNGTO obj = new VehicleNGTO(ngto, this.vngto.offsetX, this.vngto.offsetY, this.vngto.offsetZ, this.vngto.scale);
		obj.riderPosX = this.vngto.riderPosX;
		obj.riderPosY = this.vngto.riderPosY;
		obj.riderPosZ = this.vngto.riderPosZ;
		vehicle.setNGTO(obj);
	}

    @Override
	public boolean shouldRenderInPass(int pass)
    {
        return pass >= 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
    	return Double.MAX_VALUE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
    	return INFINITE_EXTENT_AABB;
    }
}