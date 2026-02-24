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

import java.util.ArrayList;
import java.util.List;

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.world.MCTEWorld;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**ミニチュアブロックのRS入出力を担う*/
public class RSPortSet
{
	private List<PortObj>[] ports;

	private void initPort(MCTEWorld world)
	{
		this.ports = new List[4];
		for(int i = 0; i < this.ports.length; ++i)
		{
			this.ports[i] = new ArrayList<>();
		}

		NGTObject ngto = world.blockObject;

		for(int x = 0; x < ngto.xSize; ++x)
		{
			for(int y = 0; y < ngto.ySize; ++y)
			{
				for(int z = 0; z < ngto.zSize; ++z)
				{
					BlockSet set = world.getBlockSet(x, y, z);
					if(set.block instanceof BlockPort)
					{
						PortType type = ((BlockPort)set.block).type;
						if(z == 0)
						{
							this.ports[2].add(new PortObj(type, x, y, z, (ngto.xSize - 1 - x), y));//west
						}
						else if(z == ngto.zSize - 1)
						{
							this.ports[0].add(new PortObj(type, x, y, z, x, y));//east
						}
						else if(x == 0)
						{
							this.ports[1].add(new PortObj(type, x, y, z, z, y));//north
						}
						else if(x == ngto.xSize - 1)
						{
							this.ports[3].add(new PortObj(type, x, y, z, (ngto.zSize - 1 - z), y));//south
						}
					}
				}
			}
		}
	}

	public List<PortObj> getPort(MCTEWorld world, int index)
	{
		if(this.ports == null)
		{
			this.initPort(world);
		}
		return this.ports[index];
	}

	public int getPower(MCTEWorld world, int index)
	{
		List<PortObj> list = this.getPort(world, index);
		int power = 0;
		for(PortObj port : list)
		{
			if(power < port.getPower(world))
			{
				power = port.getPower(world);
			}
		}
		return power;
	}

	/**隣接ブロックからRS入力時*/
	public void onNeighborBlockChange(TileEntityMiniature miniature)
	{
		//下に設置してるもの以外は除外
		if(EnumFacing.getFront(miniature.attachSide) != EnumFacing.UP){return;}

		int dir = this.getMiniatureDir(miniature);
		for(int i = 0; i < 4; ++i)
		{
			this.checkInput(miniature, i, dir);
		}
	}

	private void checkInput(TileEntityMiniature miniature, int portIndex, int miniatureDir)
	{
		MCTEWorld mw = miniature.getDummyWorld();
		int fixDir = (portIndex + miniatureDir) & 3;
		EnumFacing side = EnumFacing.getHorizontal(fixDir);
		BlockPos pos = miniature.getPos();
		int blockX = pos.getX() + side.getDirectionVec().getX();
		int blockZ = pos.getZ() + side.getDirectionVec().getZ();
		BlockPos targetPos = new BlockPos(blockX, pos.getY(), blockZ);

		List<PortObj> list = this.getPort(mw, portIndex);
		for(PortObj port : list)
		{
			if(port.type == PortType.IN)
			{
				int meta = BlockUtil.getMetadata(mw, port.x, port.y, port.z);

				if(miniature.getWorld().getBlockState(targetPos).getBlock() instanceof BlockMiniature)
				{
					TileEntityMiniature target = (TileEntityMiniature)miniature.getWorld().getTileEntity(targetPos);
					if(target.blocksObject.xSize == miniature.blocksObject.xSize && target.blocksObject.zSize == miniature.blocksObject.zSize)
					{
						int targetDir = this.getMiniatureDir(target);
						int targetFixDir = (portIndex + targetDir) & 3;
						List<PortObj> targetPorts = target.port.getPort(target.getDummyWorld(), targetFixDir);
						PortObj tPort = this.matchPort(port, targetPorts, target.blocksObject.xSize);
						if(tPort != null)
						{
							int power = tPort.getPower(target.getDummyWorld());
							if(power != meta)
							{
								BlockUtil.setBlock(mw, port.x, port.y, port.z, MCTE.portIn, power, 3);
							}
							continue;
						}
					}
				}

				int power = miniature.getWorld().getRedstonePower(targetPos, side);
				if(power != meta)
				{
					BlockUtil.setBlock(mw, port.x, port.y, port.z, MCTE.portIn, power, 3);
				}
			}
		}
	}

	private PortObj matchPort(PortObj port, List<PortObj> targetPorts, int size)
	{
		for(PortObj tPort : targetPorts)
		{
			if(port.v == tPort.v && port.u == (size - tPort.u - 1))
			{
				return tPort;
			}
		}
		return null;
	}

	/**面ごとのRS出力レベル*/
	public int isProvidingPower(TileEntityMiniature miniature, EnumFacing side)
    {
		MCTEWorld mw = miniature.getDummyWorld();
		if(mw != null)
		{
			if(side == null){return 0;}

			int dirOutput = side.getHorizontalIndex();
			if(dirOutput >= 0)
			{
				int dirM = this.getMiniatureDir(miniature);
				int portIndex = (dirOutput - dirM + 2) & 3;
				return this.getPower(mw, portIndex);
			}
		}
		return 0;
    }

	private int getMiniatureDir(TileEntityMiniature miniature)
	{
		//5:なんか90度ずれてるので補正
		return (NGTMath.floor(-(miniature.getRotation() + 45.0F) / 90.0F) + 5) % 4;
	}

	private class PortObj
	{
		public final PortType type;
		public final int x, y, z, u, v;

		public PortObj(PortType p1, int p2, int p3, int p4, int p5, int p6)
		{
			this.type = p1;
			this.x = p2;
			this.y = p3;
			this.z = p4;
			this.u = p5;//xPos側のz軸基準
			this.v = p6;
		}

		public int getPower(World world)
		{
			int meta = BlockUtil.getMetadata(world, this.x, this.y, this.z);
			return this.type == PortType.OUT ? meta : 0;
		}
	}

	public enum PortType
	{
		IN,
		OUT;
	}
}