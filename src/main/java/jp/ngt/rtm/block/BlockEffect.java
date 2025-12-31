package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.rtm.block.tileentity.TileEntityEffect;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEffect extends BlockContainerCustomWithMeta
{
	public BlockEffect()
	{
		super(Material.GRASS);
		this.setLightOpacity(0);
		this.setHardness(10000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World par1, int par2)
	{
		return new TileEntityEffect();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		return null;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
		items.add(new ItemStack(this, 1, 0));
    }
}