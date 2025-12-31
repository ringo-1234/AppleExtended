package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.block.tileentity.TileEntityPole;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLinePole extends BlockContainerCustomWithMeta
{
	public BlockLinePole()
	{
		super(Material.ROCK);
		this.setHardness(2.0F);
		this.setResistance(10.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
    {
		return new TileEntityPole();
    }

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
		return true;
    }

	@Override
	protected ItemStack getItem(int damage)
    {
		if(this == RTMBlock.linePole)
		{
			return new ItemStack(RTMItem.installedObject, 1, IstlObjType.LINEPOLE.id);
		}
		else
		{
			return new ItemStack(RTMBlock.framework, 1, damage);
		}
    }

	/*@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs tab, List list)
    {
		if(this == RTMBlock.linePole)
		{
			;
		}
		else
		{
			for(int i = 0; i < 16; ++i)
			{
				list.add(new ItemStack(par1, 1, i));
			}
		}
    }*/

	/**
	 * 接続先の座標を指定
	 * @param type 0:同一ブロックのみ, 1:不透明ブロック, 2:全ブロック
	 * */
	public static boolean isConnected(IBlockAccess world, int x, int y, int z, int type)
	{
		IBlockState state = BlockUtil.getBlockState(world, x, y, z);
		Block block = state.getBlock();
		boolean isPole = (block == RTMBlock.linePole || block == RTMBlock.framework || block == RTMBlock.signal);
		if(type == 0)
		{
			return isPole;
		}
		else if(type == 1)
		{
			return isPole || state.isOpaqueCube();
		}
		else if(type == 2)
		{
			Material material = state.getMaterial();
			return !(material == Material.AIR || material.isLiquid());
		}
		return true;
	}
}