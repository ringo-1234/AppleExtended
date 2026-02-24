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

package jp.ngt.mcte.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustom;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import jp.apple.log.AppleLogger;

public class BlockMinesweeper extends BlockContainerCustom
{
	public static final PropertyEnum<MinesweeperType> TYPE = PropertyEnum.create("type", MinesweeperType.class, MinesweeperType.values());

	public BlockMinesweeper()
	{
		super(Material.ROCK);
		this.setHardness(0.0F);
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
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, MinesweeperType.getType(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((MinesweeperType)state.getValue(TYPE)).id;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{TYPE});
    }

	@Override
	public float getBlockHardness(IBlockState blockState, World world, BlockPos pos)
    {
		IBlockState state = world.getBlockState(pos);
        return state.getValue(TYPE).isBreakable ? 0.1F : 10.0F;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int par2)
    {
		return new TileEntityMinesweeper();
    }

	@Override
	public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		MinesweeperType type = holder.getBlockState().getValue(TYPE);

		if(!world.isRemote)
		{
			if(type == MinesweeperType.NONE)
			{
				this.setMinesweeper(world, pos, MinesweeperType.NONE_FLAG);
			}
			else if(type == MinesweeperType.MINE)
			{
				this.setMinesweeper(world, pos, MinesweeperType.MINE_FLAG);
			}
			else if(type == MinesweeperType.NONE_FLAG)
			{
				this.setMinesweeper(world, pos, MinesweeperType.NONE);
			}
			else if(type == MinesweeperType.MINE_FLAG)
			{
				this.setMinesweeper(world, pos, MinesweeperType.MINE);
			}

			TileEntityMinesweeper tile2 = (TileEntityMinesweeper)world.getTileEntity(pos);
			tile2.check();
		}

		return true;
    }

	@Override
	public boolean removedByPlayer(BlockArgHolder holder, boolean willHarvest)
    {
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		if(!world.isRemote)
		{
			MinesweeperType type = world.getBlockState(pos).getValue(TYPE);
			if(type == MinesweeperType.NONE)
			{
				this.open(world, pos, true);
			}
			else if(type == MinesweeperType.MINE)
			{
				world.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D,
						4.0F, true, world.getGameRules().getBoolean("mobGriefing"));
			}
			else
			{
				this.setMinesweeper(world, pos, type);
			}
		}

		return true;
    }

	private void open(World world, BlockPos pos, boolean byPlayer)
	{
		int bombCount = 0;
		for(int i = -1; i < 2; ++i)
		{
			for(int j = -1; j < 2; ++j)
			{
				MinesweeperType type = world.getBlockState(pos.add(i, 0, j)).getValue(TYPE);
				if(world.getBlockState(pos.add(i, 0, j)).getBlock() == this && (type == MinesweeperType.MINE || type == MinesweeperType.MINE_FLAG))
				{
					++bombCount;
				}
			}
		}

		if(bombCount > 0)
		{
			this.setMinesweeper(world, pos, MinesweeperType.getType(bombCount));
		}
		else
		{
			this.setMinesweeper(world, pos, MinesweeperType.NUM0);
			for(int i = -1; i < 2; ++i)
			{
				for(int j = -1; j < 2; ++j)
				{
					MinesweeperType type = world.getBlockState(pos.add(i, 0, j)).getValue(TYPE);
					if(world.getBlockState(pos.add(i, 0, j)).getBlock() == this && type == MinesweeperType.NONE)
					{
						this.open(world, pos.add(i, 0, j), false);
					}
				}
			}
		}
	}

	private void setMinesweeper(World world, BlockPos pos, MinesweeperType type)
	{
		TileEntityMinesweeper tile = (TileEntityMinesweeper)world.getTileEntity(pos);
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		world.setBlockState(pos, this.getStateFromMeta(type.id), 3);
		TileEntityMinesweeper tile2 = (TileEntityMinesweeper)world.getTileEntity(pos);
		tile2.readFromNBT(nbt);
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
		boolean flag = false;
		if(!world.isRemote)
		{
			MinesweeperType type = world.getBlockState(pos).getValue(TYPE);
			if(type == MinesweeperType.MINE || type == MinesweeperType.MINE_FLAG)
			{
				flag = true;
			}
		}

		super.onBlockExploded(world, pos, explosion);

		if(flag)
		{
			world.newExplosion(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D,
					4.0F, true, world.getGameRules().getBoolean("mobGriefing"));
		}
    }

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune){}

	public enum MinesweeperType implements IStringSerializable
	{
		NUM0(0, false),
		NUM1(1, false),
		NUM2(2, false),
		NUM3(3, false),
		NUM4(4, false),
		NUM5(5, false),
		NUM6(6, false),
		NUM7(7, false),
		NUM8(8, false),
		N(9, false),
		NONE(10, true),//なし(未開封)
		MINE(11, true),//地雷
		NONE_FLAG(12, false),//なし(旗)
		MINE_FLAG(13, false),//地雷(旗)
		;

		public final byte id;
		public final boolean isBreakable;

		private MinesweeperType(int p1, boolean p2)
		{
			this.id = (byte)p1;
			this.isBreakable = p2;
		}

		public static MinesweeperType getType(int meta)
		{
			return values()[meta];
		}

		@Override
		public String getName()
		{
			return this.toString().toLowerCase();//State名大文字不可のため
		}
	}
}