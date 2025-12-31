package jp.ngt.rtm.rail.util;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**レールの曲線を構成する点*/
public final class RailPosition
{
	/**補正値、デフォルト長=これ*マーカー間距離最小値*/
	protected static final float Anchor_Correction_Value = 0.55228475F;//(√(2)-1)*4/3
	/**向きごとのベジェ曲線開始位置への補正値*/
	public static final float[][] REVISION = new float[][]{
		{0.0F, -0.5F},{-0.5F, -0.5F},
		{-0.5F, 0.0F},{-0.5F, 0.499999F},
		{0.0F, 0.499999F},{0.499999F, 0.499999F},
		{0.499999F, 0.0F},{0.499999F, -0.5F}};

	public int blockX, blockY, blockZ;
	/**0:通常, 1:分岐*/
	public final byte switchType;
	/**ブロックとしての向き 0~7*/
	public byte direction;
	/**0~15*/
	public byte height;
	/**水平ベジェ曲線用アンカー角度*/
	public float anchorYaw;
	/**垂直ベジェ曲線用アンカー角度*/
	public float anchorPitch;
	/**水平ベジェ曲線用アンカー長, 長さ0で直線扱い*/
	public float anchorLengthHorizontal;
	/**垂直ベジェ曲線用アンカー長, 長さ0で直線扱い*/
	public float anchorLengthVertical;
	/**中央のカント、線路で共通*/
	public float cantCenter;
	/**端のカント、RPごと*/
	public float cantEdge;
	/**カントのランダム性、大きいほど周期短い*/
	public float cantRandom;
	/**建築限界(高さ)*/
	public float constLimitHP, constLimitHN;
	/**建築限界(幅)*/
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

	public static RailPosition readFromNBT(NBTTagCompound nbt)
    {
		int[] pos = nbt.getIntArray("BlockPos");
		byte b0 = nbt.getByte("Direction");
		byte b2 = nbt.getByte("SwitchType");
		RailPosition rp = new RailPosition(pos[0], pos[1], pos[2], b0, b2);
		rp.setHeight(nbt.getByte("Height"));

		rp.anchorYaw = nbt.getFloat("A_Direction");
		rp.anchorPitch = nbt.getFloat("A_Pitch");
		rp.anchorLengthHorizontal = nbt.getFloat("A_Length");
		rp.anchorLengthVertical = nbt.getFloat("A_LenV");
		rp.cantCenter = nbt.getFloat("C_Center");
		rp.cantEdge = nbt.getFloat("C_Edge");
		rp.cantRandom = nbt.getFloat("C_Random");
		rp.constLimitHP = nbt.getFloat("Const_Limit_HP");
		rp.constLimitHN = nbt.getFloat("Const_Limit_HN");
		rp.constLimitWP = nbt.getFloat("Const_Limit_WP");
		rp.constLimitWN = nbt.getFloat("Const_Limit_WN");

		if(nbt.hasKey("Script"))
		{
			rp.scriptName = nbt.getString("Script");
			rp.scriptArgs = nbt.getString("Args");
		}

		return rp;
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

	/**@param par1 0~15*/
	public void setHeight(byte par1)
	{
		this.height = par1;
		this.posY = (double)this.blockY + (double)(par1 + 1) * BlockLargeRailBase.THICKNESS;
	}

	/**与えられた距離だけ平行移動*/
	public void movePos(int x, int y, int z)
	{
		this.blockX += x;
		this.blockY += y;
		this.blockZ += z;
		this.posX += (double)x;
		this.posY += (double)y;
		this.posZ += (double)z;
	}

	/**p1に対してp2がどっちの向きか*/
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