package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSignalConverter extends BlockContainerCustomWithMeta implements IBlockConnective
{
	public BlockSignalConverter()
	{
		super(Material.ROCK);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	public TileEntity createNewTileEntity(World par1, int par2)
	{
		return TileEntitySignalConverter.createTileEntity(par2);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		int meta = stack.getItemDamage();
		BlockUtil.setBlock(world, pos, this, meta, 3);
	}

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		int meta = BlockUtil.getMetadata(world, pos);
		if(meta == SignalConverterType.Increment.id || meta == SignalConverterType.Decrement.id)
		{
			return true;
		}
		else
		{
			if(world.isRemote)
			{
				holder.getPlayer().openGui(RTMCore.instance, RTMCore.instance.guiIdSignalConverter, world, x, y, z);
			}
			return true;
		}
    }

	@Override
	public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

	@Override
	protected int getWeakPower(BlockArgHolder holder)
    {
        return this.getStrongPower(holder);
    }

    @Override
    protected int getStrongPower(BlockArgHolder holder)
    {
    	TileEntitySignalConverter tile = (TileEntitySignalConverter)BlockUtil.getTileEntity(holder.getBlockAccess(), holder.getBlockPos());
    	return tile.getRSOutput();
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
    {
    	list.add(new ItemStack(this, 1, SignalConverterType.RSIn.id));
    	list.add(new ItemStack(this, 1, SignalConverterType.RSOut.id));
    	list.add(new ItemStack(this, 1, SignalConverterType.Increment.id));
    	list.add(new ItemStack(this, 1, SignalConverterType.Decrement.id));
    	list.add(new ItemStack(this, 1, SignalConverterType.Wireless.id));
	}

    @Override
	public boolean canConnect(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	protected ItemStack getItem(int damage)
    {
		return new ItemStack(RTMBlock.signalConverter, 1, damage);
    }
}