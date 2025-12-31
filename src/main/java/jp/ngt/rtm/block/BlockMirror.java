package jp.ngt.rtm.block;

import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.block.tileentity.TileEntityMirror;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMirror extends BlockContainerCustomWithMeta
{
	public final MirrorType mirrorType;

	public BlockMirror(MirrorType par1)
	{
		super(Material.GLASS);
		this.mirrorType = par1;
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
	{
		return new TileEntityMirror();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		if(this.mirrorType == MirrorType.Mono_Panel)
		{
			int meta = BlockUtil.getMetadata(world, pos);
			switch(meta)
			{
			case 0: return new AxisAlignedBB(0.0F, 0.9375F, 0.0F, 1.0F, 1.0F, 1.0F);//定数化すべし
			case 1: return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
			case 2: return new AxisAlignedBB(0.0F, 0.0F, 0.9375F, 1.0F, 1.0F, 1.0F);
			case 3: return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
			case 4: return new AxisAlignedBB(0.9375F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			case 5: return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.0625F, 1.0F, 1.0F);
			}
		}
		return FULL_BLOCK_AABB;
    }

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
		;
    }

	public enum MirrorType
	{
		Mono_Panel,
		Hexa_Cube;
	}
}