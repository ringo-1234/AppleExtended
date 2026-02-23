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

package jp.ngt.rtm.render;

import org.lwjgl.util.vector.Vector3f;

import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.block.tileentity.TileEntityCrossingGate;
import jp.ngt.rtm.block.tileentity.TileEntityLight;
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase;
import jp.ngt.rtm.block.tileentity.TileEntityPoint;
import jp.ngt.rtm.block.tileentity.TileEntityTurnstile;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MachinePartsRenderer extends TileEntityPartsRenderer<ModelSetMachine>
{
	public MachinePartsRenderer(String... par1)
	{
		super(par1);
	}

	public float getMovingCount(TileEntity par1)
	{
		if(par1 != null)
		{
			if(par1.getBlockType() == RTMBlock.crossingGate)
			{
				TileEntityCrossingGate gate = (TileEntityCrossingGate)par1;
				return (float)gate.barMoveCount / 90.0F;
			}
			else if(par1.getBlockType() == RTMBlock.turnstile)
			{
				TileEntityTurnstile turnstile = (TileEntityTurnstile)par1;
				return turnstile.canThrough() ? 0.0F : 1.0F;
			}
			else if(par1.getBlockType() == RTMBlock.point)
			{
				TileEntityPoint point = (TileEntityPoint)par1;
				return point.isActivated() ? 1.0F : 0.0F;
			}
		}
		return 0.0F;
	}

	/**-1:OFF, 0 or 1:ON*/
	public int getLightState(TileEntity par1)
	{
		if(par1 != null)
		{
			if(par1.getBlockType() == RTMBlock.crossingGate)
			{
				TileEntityCrossingGate gate = (TileEntityCrossingGate)par1;
				return gate.lightCount;
			}
			else if(par1.getBlockType() == RTMBlock.light)
			{
				TileEntityLight light = (TileEntityLight)par1;
				return light.isGettingPower ? 1 : -1;
			}
		}
		return -1;
	}

	public int getLodState(TileEntity par1)
	{
		if(par1 != null)
		{
			if(par1.getBlockType() == RTMBlock.point)
			{
				TileEntityPoint point = (TileEntityPoint)par1;
				return point.getMove() > 0.0F ? 1 : -1;
			}
		}
		return 0;
	}

	public int getTick(TileEntity par1)
	{
		return par1 == null ? 0 : ((TileEntityMachineBase)par1).tick;
	}

	public float getPitch(TileEntity par1)
	{
		if(par1 != null)
		{
			TileEntityMachineBase machine = (TileEntityMachineBase)par1;
			return machine.getPitch();
		}
		return 0.0F;
	}

	public float getYaw(TileEntity par1)
	{
		if(par1 != null)
		{
			TileEntityMachineBase machine = (TileEntityMachineBase)par1;
			return machine.getRotation();
		}
		return 0.0F;
	}

	private static Vector3f VEC3F_TMP = new Vector3f();

	public Vector3f getNormal(TileEntity par1, float x, float y, float z, float pitch, float yaw)
	{
		if(par1 != null)
		{
			TileEntityMachineBase machine = (TileEntityMachineBase)par1;
			Vec3 vec = machine.getNormal(x, y, z, pitch, yaw);
			vec = vec.rotateAroundX(pitch).rotateAroundY(yaw);
			VEC3F_TMP.set((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());
		}
		return VEC3F_TMP;
	}

	private static final double[] BUF = new double[3];

	public double[] getLightPos(TileEntity par1, float x, float y, float z, float pitch, float yaw)
	{
		double posX = 0.0D;
		double posY = 0.0D;
		double posZ = 0.0D;
		if(par1 != null)
		{
			TileEntityMachineBase machine = (TileEntityMachineBase)par1;
			Vec3 vec = PooledVec3.create(x, y, z);
			vec = rotateVec(vec, machine.getBlockMetadata(), pitch, yaw);
			BlockPos pos = par1.getPos();
			posX = (double)pos.getX() + 0.5D + vec.getX();
			posY = (double)pos.getY() + 0.5D + vec.getY();
			posZ = (double)pos.getZ() + 0.5D + vec.getZ();
		}
		BUF[0] = posX;
		BUF[1] = posY;
		BUF[2] = posZ;
		return BUF;
	}

	public static Vec3 rotateVec(Vec3 vec, int dir, float pitch, float yaw)
	{
		switch(dir)
		{
		case 0://-y
			vec = vec.rotateAroundX(pitch);
			vec = vec.rotateAroundY(yaw);
			break;
		case 1://+y
			vec = vec.rotateAroundX(-pitch);
			vec = vec.rotateAroundY(yaw);
			break;
		case 2://z
			vec = vec.rotateAroundX(-pitch + 90.0F);
			vec = vec.rotateAroundY(yaw);
			vec = vec.rotateAroundX(90.0F);
			break;
		case 3://z
			vec = vec.rotateAroundX(-pitch + 90.0F);
			vec = vec.rotateAroundY(yaw);
			vec = vec.rotateAroundX(-90.0F);
			break;
		case 4://x
			vec = vec.rotateAroundX(-pitch + 90.0F);
			vec = vec.rotateAroundY(yaw);
			vec = vec.rotateAroundZ(-90.0F);
			break;
		case 5://x
			vec = vec.rotateAroundX(-pitch + 90.0F);
			vec = vec.rotateAroundY(yaw);
			vec = vec.rotateAroundZ(90.0F);
			break;
		}
		return vec;
	}
}