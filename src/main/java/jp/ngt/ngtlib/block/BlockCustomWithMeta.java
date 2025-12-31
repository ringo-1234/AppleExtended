package jp.ngt.ngtlib.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

/**メタデータ持ちブロック*/
public abstract class BlockCustomWithMeta extends BlockCustom
{
	public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);

	public BlockCustomWithMeta(Material material)
	{
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META, Integer.valueOf(0)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(META, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(META);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{META});
    }
}