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

package jp.ngt.rtm.rail.util;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class RailPosition
{
	protected static final float Anchor_Correction_Value = 0.55228475F;
	public static final float[][] REVISION = new float[][]{
			{0.0F, -0.5F},{-0.5F, -0.5F},
			{-0.5F, 0.0F},{-0.5F, 0.499999F},
			{0.0F, 0.499999F},{0.499999F, 0.499999F},
			{0.499999F, 0.0F},{0.499999F, -0.5F}};

	public int blockX, blockY, blockZ;
	public final byte switchType;
	public byte direction;
	public byte height;
	public float anchorYaw;
	public float anchorPitch;
	public float anchorLengthHorizontal;
	public float anchorLengthVertical;
	public float cantCenter;
	public float cantEdge;
	public float cantRandom;
	public float constLimitHP, constLimitHN;
	public float constLimitWP, constLimitWN;

	public double posX, posY, posZ;

	public String scriptName;
	public String scriptArgs;

	public RailPosition(int x, int y, int z, int dir, int type)
	{
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
		this.direction = (byte)dir;
		this.switchType = (byte)type;

		this.height = (byte)0;
		this.anchorYaw = NGTMath.wrapAngle((float)dir * 45.0F);
		this.anchorLengthHorizontal = -1.0F;

		this.constLimitHP = 3.99F;
		this.constLimitHN = 0.0F;
		this.constLimitWP = 1.49F;
		this.constLimitWN = -1.49F;

		this.init();
	}

	public void init()
	{
		this.posX = (double)this.blockX + 0.5D + (double)REVISION[this.direction][0];
		this.posY = (double)this.blockY + (double)(this.height + 1) * BlockLargeRailBase.THICKNESS;
		this.posZ = (double)this.blockZ + 0.5D + (double)REVISION[this.direction][1];
	}

	public void addHeight(double par1)
	{
		int h2 = (int)(par1 / BlockLargeRailBase.THICKNESS);
		this.height = (byte)(this.height + h2);
	}

	public static RailPosition readFromNBT(NBTTagCompound nbt, RailPosition base) {
		if (base == null)
			return readFromNBT(nbt);
		if (base.switchType != nbt.getByte("SwitchType"))
			return readFromNBT(nbt);

		int[] pos = nbt.getIntArray("BlockPos");
		base.blockX = pos[0];
		base.blockY = pos[1];
		base.blockZ = pos[2];
		base.direction = nbt.getByte("Direction");
		base.init();
		return readDataFromNBT(nbt, base);
	}

	public static RailPosition readFromNBT(NBTTagCompound nbt) {
		int[] aint = nbt.getIntArray("BlockPos");
		byte b0 = nbt.getByte("Direction");
		byte b1 = nbt.getByte("SwitchType");
		return readDataFromNBT(nbt, new RailPosition(aint[0], aint[1], aint[2], b0, b1));
	}

	private static RailPosition readDataFromNBT(NBTTagCompound nbt, RailPosition railposition) {
		railposition.setHeight(nbt.getByte("Height"));
		railposition.anchorYaw = nbt.getFloat("A_Direction");
		railposition.anchorPitch = nbt.getFloat("A_Pitch");
		railposition.anchorLengthHorizontal = nbt.getFloat("A_Length");
		railposition.anchorLengthVertical = nbt.getFloat("A_LenV");
		railposition.cantCenter = nbt.getFloat("C_Center");
		railposition.cantEdge = nbt.getFloat("C_Edge");
		railposition.cantRandom = nbt.getFloat("C_Random");
		railposition.constLimitHP = nbt.getFloat("Const_Limit_HP");
		railposition.constLimitHN = nbt.getFloat("Const_Limit_HN");
		railposition.constLimitWP = nbt.getFloat("Const_Limit_WP");
		railposition.constLimitWN = nbt.getFloat("Const_Limit_WN");
		if (nbt.hasKey("Script")) {
			railposition.scriptName = nbt.getString("Script");
			railposition.scriptArgs = nbt.getString("Args");
		}
		return railposition;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setIntArray("BlockPos", new int[]{this.blockX, this.blockY, this.blockZ});
		nbt.setByte("SwitchType", this.switchType);
		nbt.setByte("Direction", this.direction);
		nbt.setByte("Height", this.height);

		nbt.setFloat("A_Direction", this.anchorYaw);
		nbt.setFloat("A_Pitch", this.anchorPitch);
		nbt.setFloat("A_Length", this.anchorLengthHorizontal);
		nbt.setFloat("A_LenV", this.anchorLengthVertical);
		nbt.setFloat("C_Center", this.cantCenter);
		nbt.setFloat("C_Edge", this.cantEdge);
		nbt.setFloat("C_Random", this.cantRandom);
		nbt.setFloat("Const_Limit_HP", this.constLimitHP);
		nbt.setFloat("Const_Limit_HN", this.constLimitHN);
		nbt.setFloat("Const_Limit_WP", this.constLimitWP);
		nbt.setFloat("Const_Limit_WN", this.constLimitWN);

		if(this.hasScript())
		{
			nbt.setString("Script", this.scriptName);
			nbt.setString("Args", this.scriptArgs);
		}

		return nbt;
	}

	public void setHeight(byte par1)
	{
		this.height = par1;
		this.posY = (double)this.blockY + (double)(par1 + 1) * BlockLargeRailBase.THICKNESS;
	}

	public void movePos(int x, int y, int z)
	{
		this.blockX += x;
		this.blockY += y;
		this.blockZ += z;
		this.posX += (double)x;
		this.posY += (double)y;
		this.posZ += (double)z;
	}

	public RailDir getDir(RailPosition p1, RailPosition p2)
	{
		double dif1x = p1.posX - this.posX;
		double dif1z = p1.posZ - this.posZ;
		double dif2x = p2.posX - this.posX;
		double dif2z = p2.posZ - this.posZ;
		double d0 = dif1z * dif2x - dif1x * dif2z;
		return d0 > 0.0D ? RailDir.LEFT : (d0 < 0.0D ? RailDir.RIGHT : RailDir.NONE);
	}

	public boolean checkRSInput(World world)
	{
		return world.isBlockIndirectlyGettingPowered(new BlockPos(this.blockX, this.blockY, this.blockZ)) > 0;
	}

	public BlockPos getNeighborBlockPos()
	{
		int x2 = NGTMath.floor(this.posX + REVISION[this.direction][0]);
		int y2 = this.blockY;
		int z2 = NGTMath.floor(this.posZ + REVISION[this.direction][1]);
		return new BlockPos(x2, y2, z2);
	}

	public boolean hasScript()
	{
		return this.scriptName != null && this.scriptName.length() > 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof RailPosition)
		{
			RailPosition rp = (RailPosition)obj;
			return rp.blockX == this.blockX && rp.blockY == this.blockY && rp.blockZ == this.blockZ;
		}
		return false;
	}
}