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

package jp.ngt.rtm.rail.util;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class RailMap
{
	protected final List<int[]> rails = new ArrayList<int[]>();

	public RailMap()
	{
		;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof RailMap)
		{
			RailMap rm = (RailMap)obj;
			return this.getStartRP().equals(rm.getStartRP());
		}
        return false;
    }

	/*======================================================================================*/

	public abstract RailPosition getStartRP();

	public abstract RailPosition getEndRP();

	public abstract double getLength();

	public abstract int getNearlestPoint(int split, double x, double z);

	/**
	 * @param split 分割数
	 * @param index 位置
	 * @return {z, x}
	 */
	public abstract double[] getRailPos(int split, int index);

	/**
	 * @param split 分割数
	 * @param index 位置
	 * @return y
	 */
	public abstract double getRailHeight(int split, int index);

	/**
	 * @param split 分割数
	 * @param index 位置
	 * @return 0~360
	 */
	public abstract float getRailYaw(int split, int index);

	/*Script互換性*/
	@Deprecated
	public final float getRailRotation(int split, int index)
	{
		return this.getRailYaw(split, index);
	}

	/**
	 * @param split 分割数
	 * @param index 位置
	 * @return 0~360
	 */
	public abstract float getRailPitch(int split, int index);

	public abstract float getRailRoll(int split, int index);

	/*Script互換性*/
	@Deprecated
	public final float getCant(int split, int index)
	{
		return this.getRailRoll(split, index);
	}

	/*======================================================================================*/

	/**
	 * RailMapの端同士が繋げられるかどうか(=連続した曲線になるか)<br>
	 * 同一RailMapの場合はtrue
	 * @param railMap null可
	 */
	public boolean canConnect(RailMap railMap)
	{
		if(railMap == null){return false;}

		if(this.equals(railMap)){return true;}

		for(int i = 0; i < 2; ++i)
		{
			for(int j = 0; j < 2; ++j)
			{
				double[] p0 = this.getRailPos(10, i * 10);
				double[] p1 = railMap.getRailPos(10, j * 10);
				if(NGTMath.compare(p0[0], p1[0], 5) && NGTMath.compare(p0[1], p1[1], 5))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 道床ブロックのリストを作成<br>
	 * レールの生成時と破壊時に呼ばれる
	 */
	protected void createRailList(ResourceStateRail prop)
	{
		ModelSetRail modelSet = prop.getResourceSet();
		int halfWidth = modelSet.getConfig().ballastWidth >> 1;

		this.rails.clear();
		int split = (int)(this.getLength() * 4.0D);
		double halfPi = Math.PI * 0.5D;
		for(int j = 1; j < split - 1; ++j)//端に余分なブロックが生成されないよう±1縮める
		{
			double[] point = this.getRailPos(split, j);
			double x = point[1];
			double z = point[0];
			double slope = NGTMath.toRadians(this.getRailYaw(split, j));
			double height = this.getRailHeight(split, j);
			int y = (int)height;

			for(int i = 0; i <= halfWidth; ++i)
			{
				double d0 = (double)i + 0.25D;//1m幅でブロックが欠けないように
				int x1 = NGTMath.floor(x + Math.sin(slope + halfPi) * d0);
				int z1 = NGTMath.floor(z + Math.cos(slope + halfPi) * d0);
				this.addRailBlock(x1, y, z1);
				int x2 = NGTMath.floor(x + Math.sin(slope - halfPi) * d0);
				int z2 = NGTMath.floor(z + Math.cos(slope - halfPi) * d0);
				this.addRailBlock(x2, y, z2);
			}

			int x0 = NGTMath.floor(x);
			int z0 = NGTMath.floor(z);
			this.addRailBlock(x0, y, z0);
		}
		//NGTLog.debug("[RM] Gen Rail %d blocks", this.rails.size());
	}

	protected void addRailBlock(int x, int y, int z)
	{
		for(int i = 0; i < this.rails.size(); ++i)
		{
			int[] ia = this.rails.get(i);
			if(ia[0] == x && ia[2] == z)
			{
				if(ia[1] <= y)
				{
					return;
				}
				else
				{
					this.rails.remove(i);
					--i;
				}
			}
		}

		BlockPos pos = new BlockPos(x, y, z);
		if(!pos.equals(this.getStartRP().getNeighborBlockPos()) && !pos.equals(this.getEndRP().getNeighborBlockPos()))
		{
			this.rails.add(new int[]{x, y, z});//始点と終点に接する位置にはブロック生成しないように
		}
	}

	/**ブロックの設置*/
	public void setRail(World world, Block block, int x0, int y0, int z0, ResourceStateRail prop)
	{
		this.createRailList(prop);
		this.setBaseBlock(world, x0, y0, z0);

		for(int i = 0; i < this.rails.size(); ++i)
		{
			int x = this.rails.get(i)[0];
			int y = this.rails.get(i)[1];
			int z = this.rails.get(i)[2];
			Block block2 = BlockUtil.getBlock(world, x, y, z);
			if(!(block2 instanceof BlockLargeRailBase) || block2 == block)//異なる種類のレールを上書きしない
			{
				BlockUtil.setBlock(world, x, y, z, block, 0, 2);
				TileEntityLargeRailBase tile = (TileEntityLargeRailBase)BlockUtil.getTileEntity(world, x, y, z);
				tile.setStartPoint(x0, y0, z0);
			}
		}
		this.rails.clear();
	}

	private void setBaseBlock(World world, int x0, int y0, int z0)
	{
		int split = (int)(this.getLength() * 4.0D);
		RailPosition rp = this.getStartRP();
		int minWidth = NGTMath.floor(rp.constLimitWN + 0.5F);
		int maxWidth = NGTMath.floor(rp.constLimitWP + 0.5F);
		int minHeight = NGTMath.floor(rp.constLimitHN);
		int maxHeight = NGTMath.floor(rp.constLimitHP);
		IBlockState[][] template = new IBlockState[maxHeight - minHeight + 1][maxWidth - minWidth + 1];

		for(int k = 0; k < split - 1; ++k)//端まで設置するとはみ出る
		{
			double[] point = this.getRailPos(split, k);
			double x = point[1];
			double z = point[0];
			double y = this.getRailHeight(split, k);
			float yaw = NGTMath.wrapAngle(this.getRailYaw(split, k));

			for(int i = 0; i < template.length; ++i)
			{
				int h = minHeight + i;

				for(int j = 0; j < template[i].length; ++j)
				{
					int w = minWidth + j;
					Vec3 vec = new Vec3(w, h, 0.0D);
					vec = vec.rotateAroundY(yaw);
					BlockPos pos = new BlockPos(x + vec.getX(), y + vec.getY(), z + vec.getZ());

					if(k == 0)
					{
						//始点のXY範囲のブロックを取得
						IBlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						if(!(block instanceof BlockMarker) && !(block instanceof BlockLargeRailBase))
						{
							template[i][j] = state;
						}
					}
					else
					{
						//始点で取得したブロックを設置
						if(template[i][j] != null)
						{
							//レールは上書きしない
							if(!(world.getBlockState(pos).getBlock() instanceof BlockLargeRailBase))
							{
								world.setBlockState(pos, template[i][j]);
							}
						}
					}
				}
			}
		}
	}

	/**レールの破壊*/
	public void breakRail(World world, ResourceStateRail prop, TileEntityLargeRailCore core)
	{
		this.createRailList(prop);
		for(int i = 0; i < this.rails.size(); ++i)
		{
			int x = this.rails.get(i)[0];
			int y = this.rails.get(i)[1];
			int z = this.rails.get(i)[2];
			if(BlockUtil.getBlock(world, x, y, z) instanceof BlockLargeRailBase)
			{
				TileEntityLargeRailBase rail = (TileEntityLargeRailBase)BlockUtil.getTileEntity(world, x, y, z);
				if(rail == core)
				{
					continue;//coreの破壊は最後に行う
				}

				//重なっている他レールを破壊しないように
				//coreが既に破壊さている場合は続行
				TileEntityLargeRailCore core2 = rail.getRailCore();
				if(core2 == null || core2 == core)
				{
					world.setBlockToAir(new BlockPos(x, y, z));
				}
			}
		}
		world.setBlockToAir(core.getPos());
		this.rails.clear();
	}

	public boolean canPlaceRail(World world, boolean isCreative, ResourceStateRail prop)
	{
		this.createRailList(prop);
		boolean flag = true;
		for(int i = 0; i < this.rails.size(); ++i)
		{
			int x = this.rails.get(i)[0];
			int y = this.rails.get(i)[1];
			int z = this.rails.get(i)[2];
			IBlockState state = world.getBlockState(new BlockPos(x, y, z));
			Block block = state.getBlock();
			boolean b0 = block.getMaterial(state) == Material.AIR || block == RTMBlock.marker || block == RTMBlock.markerSwitch || (block instanceof BlockLargeRailBase && !((BlockLargeRailBase)block).isCore());
			if(!isCreative && !b0)
			{
				NGTLog.sendChatMessageToAll("message.rail.obstacle", new Object[]{":" + x + "," + y + "," + z});
				return false;
			}
			flag = b0 && flag;
		}
		return isCreative || flag;
	}

	public List<int[]> getRailBlockList(ResourceStateRail prop, boolean regenerate)
	{
		if(this.rails.isEmpty() || regenerate)
		{
			this.createRailList(prop);
		}
		return new ArrayList<>(this.rails);
	}

	public void showRailProp()
	{
		NGTLog.sendChatMessageToAll(String.format("SP X%5.1f Z%5.1f", this.getStartRP().posX, this.getStartRP().posZ));
		NGTLog.sendChatMessageToAll(String.format("SA L%5.1f Y%5.1f", this.getStartRP().anchorLengthHorizontal, this.getStartRP().anchorYaw));
		NGTLog.sendChatMessageToAll(String.format("EP X%5.1f Z%5.1f", this.getEndRP().posX, this.getEndRP().posZ));
		NGTLog.sendChatMessageToAll(String.format("EA L%5.1f Y%5.1f", this.getEndRP().anchorLengthHorizontal, this.getEndRP().anchorYaw));
	}
}
