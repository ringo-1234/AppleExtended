package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockConnector extends BlockElectricalWiring
{
	public BlockConnector()
	{
		super(Material.ROCK);
		this.setLightOpacity(0);
		this.setAABB(FULL_BLOCK_AABB);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return par2 >= 6 ? new TileEntityConnectorOut() : new TileEntityConnectorIn();
	}

	@SideOnly(Side.CLIENT)
	@Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return this.getBoundingBox(state, world, pos).offset(pos);
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		int meta = state.getBlock().getMetaFromState(state);
        return this.getBlockBounds(meta % 6);
    }

	protected AxisAlignedBB getBlockBounds(int par1)
    {
		float range = 0.25F;
		float minX = 0.5F - range;
		float minY = 0.5F - range;
		float minZ = 0.5F - range;
		float maxX = 0.5F + range;
		float maxY = 0.5F + range;
		float maxZ = 0.5F + range;
		switch(par1)
		{
		case 0:
			maxY = 1.0F;
			break;
		case 1:
			minY = 0.0F;
			break;
		case 2:
			maxZ = 1.0F;
			break;
		case 3:
			minZ = 0.0F;
			break;
		case 4:
			maxX = 1.0F;
			break;
		case 5:
			minX = 0.0F;
			break;
		}
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
		if(!world.isRemote)
        {
			int meta = BlockUtil.getMetadata(world, pos);
			spawnAsEntity(world, pos, this.getItem(meta));
        }
    }

	@Override
	protected ItemStack getItem(int damage)
    {
		damage = damage < 6 ? IstlObjType.CONNECTOR_IN.id : IstlObjType.CONNECTOR_OUT.id;
		return new ItemStack(RTMItem.installedObject, 1, damage);
    }

	@Override
	public boolean canConnect(World world, int x, int y, int z)
	{
		return true;
	}
}