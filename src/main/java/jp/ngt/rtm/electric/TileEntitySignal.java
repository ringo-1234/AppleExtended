package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityPlaceable;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ScriptExecuter;
import jp.ngt.rtm.modelpack.modelset.ModelSetSignal;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.modelpack.state.ResourceStateWithBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySignal extends TileEntityPlaceable implements IProvideElectricity, IResourceSelector, ITickable
{
	private ResourceStateWithBlock<ModelSetSignal> state = new ResourceStateWithBlock<>(RTMResource.SIGNAL, this);
	private ScriptExecuter executer = new ScriptExecuter();

	private TileEntity origTileEntity;
	public int blockDirection;
	private int signalLevel = 0;
	public int tick;

	public TileEntitySignal()
	{
		this.state.setBlock(Blocks.AIR, 0);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.state.readFromNBT(nbt.getCompoundTag("State"));
        this.blockDirection = nbt.getInteger("blockDir");
        this.signalLevel = nbt.getInteger("Signal");

        if(this.world != null && this.world.isRemote)
        {
        	BlockUtil.markBlockForUpdate(this.getWorld(), this.getPos());//描画の更新
        }

        if(nbt.hasKey("BaseBlockData"))
        {
        	//readFromNBTも行う, Worldはnullでもいけそう(最悪catchされる)
        	this.origTileEntity = TileEntity.create(null, nbt.getCompoundTag("BaseBlockData"));
        }
    }

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setTag("State", this.state.writeToNBT());
        nbt.setInteger("blockDir", this.blockDirection);
        nbt.setInteger("Signal", this.signalLevel);

        if(this.origTileEntity != null)
        {
        	NBTTagCompound nbt2 = new NBTTagCompound();
            this.origTileEntity.writeToNBT(nbt2);
            nbt.setTag("BaseBlockData", nbt2);
        }

        return nbt;
    }

	@Override
	public void update()
    {
    	++this.tick;
		if(this.tick == Integer.MAX_VALUE)
		{
			this.tick = 0;
		}

		if(!this.getWorld().isRemote)
		{
			this.executer.execScript(this);
		}
    }

	@Override
	public int getElectricity()
	{
		return 0;
	}

	@Override
	public void setElectricity(int x, int y, int z, int level)
	{
		if(!this.world.isRemote)
		{
			ModelSetSignal modelSet = this.getResourceState().getResourceSet();
			if(level > modelSet.getConfig().maxSignalLevel)
			{
				level = modelSet.getConfig().maxSignalLevel;
			}
			this.signalLevel = level;
			this.markDirty();
			this.sendPacket();
		}
	}

	/**
	 * @param par1 元のブロック
	 * @param par2 信号機の設置されてる面
	 */
	public void setSignalProperty(String name, Block par1, int par2, EntityPlayer player, TileEntity tile)
	{
		this.state.setResourceName(name);
		this.state.setBlock(par1, this.getBlockMetadata());
		this.origTileEntity = tile;
		this.blockDirection = par2;
		this.setRotation(player, 15.0F, false);
		this.sendPacket();
		this.markDirty();
	}

	public TileEntity getOrigTileEntity()
	{
		return this.origTileEntity;
	}

	@SideOnly(Side.CLIENT)
	public float getBlockDirection()
	{
		return (float)this.blockDirection * 90.0F;
	}

	@SideOnly(Side.CLIENT)
	public int getSignal()
	{
		return this.signalLevel;
	}

	@SideOnly(Side.CLIENT)
	public void setSignal(int par1)
	{
		this.signalLevel = par1;
	}

	public Block getRenderBlock()
	{
		return this.getResourceState().block;
	}

	/**Block破壊時呼び出し*/
	public void setOrigBlock()
	{
		Block block = this.getRenderBlock();
		int meta = BlockUtil.getMetadata(this.getWorld(), this.getPos());
		BlockUtil.setBlock(this.getWorld(), this.getPos(), block, meta, 3);

		TileEntity tile = this.getWorld().getTileEntity(this.getPos());
		if(this.origTileEntity != null && tile != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			this.origTileEntity.writeToNBT(nbt);
			tile.readFromNBT(nbt);
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass)
    {
		return pass >= 0;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		int x = this.getPos().getX();
		int y = this.getPos().getY();
		int z = this.getPos().getZ();
    	AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1, y + 2, z + 1);
    	return bb;
    }

	@Override
	public void updateResourceState()
	{
		if(this.world == null || !this.world.isRemote)
		{
			this.markDirty();
			this.sendPacket();
		}
	}

	@Override
	public int[] getSelectorPos()
	{
		return new int[]{this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()};
	}

	@Override
	public boolean closeGui(ResourceState par1)
	{
		return true;
	}

	@Override
	public ResourceStateWithBlock<ModelSetSignal> getResourceState()
	{
		return this.state;
	}
}