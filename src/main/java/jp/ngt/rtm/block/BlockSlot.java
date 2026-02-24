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

package jp.ngt.rtm.block;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.block.BlockContainerCustom;
import jp.ngt.ngtlib.block.BlockLiquidBase;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMMaterial;
import jp.ngt.rtm.block.tileentity.TileEntitySlot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSlot extends BlockContainerCustom implements IPipeConnectable
{
	//BlockRotatedPillar
	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.<EnumFacing>create("facing", EnumFacing.class);

	public BlockSlot()
	{
		super(RTMMaterial.fireproof);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		this.setTickRandomly(true);
		this.setHardness(2.0F);
		this.setResistance(10.0F);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	public TileEntity createNewTileEntity(World par1, int par2)
	{
		return new TileEntitySlot();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(FACING, facing.getOpposite());
    }

	//TileEntityからTick毎呼び出し
	public void inhaleLiquid(World world, int x, int y, int z)
	{
		int m0 = BlockUtil.getMetadata(world, x, y, z);
		int x0 = x + BlockUtil.facing[m0][0];
		int y0 = y + BlockUtil.facing[m0][1];
		int z0 = z + BlockUtil.facing[m0][2];
		Block target = BlockUtil.getBlock(world, x0, y0, z0);
		int meta = BlockUtil.getMetadata(world, x0, y0, z0);
		if(target instanceof BlockLiquid)
		{
			if(this.addLiquid(world, x, y, z, target, meta))
			{
				world.setBlockToAir(new BlockPos(x0, y0, z0));
			}
		}
	}

	public boolean addLiquid(World world, int x, int y, int z, Block block, int metadata)
	{
		int m0 = BlockUtil.getMetadata(world, x, y, z);
		int x1 = x - BlockUtil.facing[m0][0];
		int y1 = y - BlockUtil.facing[m0][1];
		int z1 = z - BlockUtil.facing[m0][2];
		Block b0 = BlockUtil.getBlock(world, x1, y1, z1);

		if(b0 == RTMBlock.pipe)
		{
			List<BlockSet> list = ((BlockPipe)RTMBlock.pipe).setLiquid(world, x1, y1, z1, x, y, z, new ArrayList<BlockSet>(), 0);
			while(!list.isEmpty())
			{
				BlockSet bs = list.get(world.rand.nextInt(list.size()));
				if(this.setLiquid(world, bs.x, bs.y, bs.z, block, metadata))
				{
					return true;
				}
				list.remove(bs);
			}
			return false;
		}
		else
		{
			return this.setLiquid(world, x1, y1, z1, block, metadata);
		}
	}

	protected boolean setLiquid(World world, int x, int y, int z, Block block, int metadata)
	{
		int m0 = BlockUtil.getMetadata(world, x, y, z);
		int x0 = x - BlockUtil.facing[m0][0];
		int y0 = y - BlockUtil.facing[m0][1];
		int z0 = z - BlockUtil.facing[m0][2];
		if(BlockUtil.isAir(world, x0, y0, z0))
		{
			BlockUtil.setBlock(world, x0, y0, z0, block, metadata, 3);
			return true;
		}
		else if(block instanceof BlockLiquidBase)
		{
			for(int y1 = 1; y1 < 16; ++y1)
			{
				if(BlockUtil.getBlock(world, x0, y0 + y1 - 1, z0) == block && BlockUtil.isAir(world, x0, y0 + y1, z0))
				{
					BlockUtil.setBlock(world, x0, y0 + y1, z0, block, metadata, 3);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState();

        switch (meta)
        {
            case 1:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.EAST);
                break;
            case 2:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.WEST);
                break;
            case 3:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.SOUTH);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.NORTH);
                break;
            case 5:
            	iblockstate = iblockstate.withProperty(FACING, EnumFacing.DOWN);
            	break;
            default:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP);
        }

        return iblockstate;
    }

	@Override
	public int getMetaFromState(IBlockState state)
    {
		return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

	@Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }

    @Override
	public String getHarvestTool(IBlockState state)//Material != rockのとき必須？
    {
		return "pickaxe";
    }

	@Override
	public int getHarvestLevel(IBlockState state)
    {
		return 0;
    }

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);
        for (IProperty<?> prop : state.getProperties().keySet())
        {
            if (prop.getName().equals("facing"))
            {
                world.setBlockState(pos, state.cycleProperty(prop));
                return true;
            }
        }
        return false;
    }
}