package jp.ngt.rtm.modelpack.state;

import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ResourceStateWithBlock<T extends ModelSetBase> extends ResourceState<T>
{
	public Block block = Blocks.AIR;//ぬるぽ防止
	public byte meta;
	public String unlocalizedName;
	private IBlockState state;

	public ResourceStateWithBlock(ResourceType type, Object entity)
	{
		super(type, entity);
	}

	public void setBlock(Block par2, int par3)
	{
		this.block = par2;
		this.meta = (byte)par3;
		this.initBlockName();
	}

	private void initBlockName()
	{
		Item item = Item.getItemFromBlock(this.block);
		//空気ブロックとかでnull
		String s = (item == null) ? this.block.getUnlocalizedName() : (new ItemStack(this.block, 1, this.meta).getUnlocalizedName());
		this.unlocalizedName = s + ".name";
		this.state = null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		String s = nbt.getString("BlockName");
		this.block = Block.getBlockFromName(s);
		if(this.block == null)
		{
			this.block = Blocks.AIR;
		}
		this.meta = nbt.getByte("BlockMetadata");
		this.initBlockName();
	}

	@Override
	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = super.writeToNBT();
		nbt.setString("BlockName", Block.REGISTRY.getNameForObject(this.block).toString());
		nbt.setByte("BlockMetadata", this.meta);
		return nbt;
	}

	public IBlockState getBlockState()
	{
		if(this.state == null)
		{
			this.state = this.block.getStateFromMeta(this.meta);
		}
		return this.state;
	}
}