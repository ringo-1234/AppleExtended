package jp.ngt.rtm.rail.util;

import jp.ngt.ngtlib.math.BezierCurve;
import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.StraightLine;
import net.minecraft.world.World;

public class RailMapBasic extends RailMap
{
	protected final RailPosition startRP;
	protected final RailPosition endRP;
	protected double length;

	/**水平方向の線形*/
	protected ILine lineHorizontal;
	/**垂直方向の線形, 高さとPitch取得用のためXZは相対的な長さで計算*/
	protected ILine lineVertical;

	public RailMapBasic(RailPosition par1, RailPosition par2)
	{
		super();
		this.startRP = par1;
		this.endRP = par2;
		this.endRP.cantCenter = this.startRP.cantCenter;//中央カント共通化
		this.createLine();
	}

	/*public void rebuild()
	{
		this.createLine();
	}*/

	protected void createLine()
	{
		double x0 = this.startRP.posX;
		double y0 = this.startRP.posY;
		double z0 = this.startRP.posZ;
		double x1 = this.endRP.posX;
		double y1 = this.endRP.posY;
		double z1 = this.endRP.posZ;

		boolean flag1 = (this.endRP.direction - this.startRP.direction) % 4 == 0;
		boolean flag2 = (z0 == z1 || x0 == x1);//直角
		boolean flag3 = (Math.abs(z0 - z1) == Math.abs(x0 - x1) && this.startRP.direction % 2 != 0 && this.endRP.direction % 2 != 0);//45度
		if(flag1 && (flag2 || flag3))
		{
			this.lineHorizontal = new StraightLine(z0, x0, z1, x1);
		}
		else
		{
			double ddz = Math.abs(z1 - z0);
			double ddx = Math.abs(x1 - x0);
			double max = ddz >= ddx ? ddz : ddx;
			double min = ddz <= ddx ? ddz : ddx;
			if(this.startRP.anchorLengthHorizontal <= 0.0F)
			{
				boolean b0 = this.startRP.direction % 2 == 0;//true:まっすぐ, false:斜め
				double d1 = b0 ? max : min;
				this.startRP.anchorLengthHorizontal = (float)(d1 * RailPosition.Anchor_Correction_Value);
			}

			if(this.endRP.anchorLengthHorizontal <= 0.0F)
			{
				boolean b0 = this.endRP.direction % 2 == 0;
				double d1 = b0 ? max : min;
				this.endRP.anchorLengthHorizontal = (float)(d1 * RailPosition.Anchor_Correction_Value);
			}

			double d1 = NGTMath.cos(this.startRP.anchorYaw) * this.startRP.anchorLengthHorizontal;
			double d2 = NGTMath.sin(this.startRP.anchorYaw) * this.startRP.anchorLengthHorizontal;
			double d3 = NGTMath.cos(this.endRP.anchorYaw) * this.endRP.anchorLengthHorizontal;
			double d4 = NGTMath.sin(this.endRP.anchorYaw) * this.endRP.anchorLengthHorizontal;
			this.lineHorizontal = new BezierCurve(z0, x0, z0 + d1, x0 + d2, z1 + d3, x1 + d4, z1, x1);
		}

		double lenXZ = Math.sqrt(NGTMath.pow(x1 - x0, 2) + NGTMath.pow(z1 - z0, 2));

		boolean flagV1 = (this.startRP.anchorLengthVertical == 0.0F && this.endRP.anchorLengthVertical == 0.0F);
		//boolean flagV2 = (this.startRP.anchorPitch + this.endRP.anchorPitch == 0.0F);
		if(flagV1)
		{
			this.lineVertical = new StraightLine(0.0D, y0, lenXZ, y1);
		}
		else
		{
			double d1 = NGTMath.cos(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical;
			double d2 = NGTMath.sin(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical;
			double d3 = NGTMath.cos(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical;
			double d4 = NGTMath.sin(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical;
			//"lenXZ - d3"のみLineHと異なるので注意
			this.lineVertical = new BezierCurve(0.0D, y0, d1, y0 + d2, lenXZ - d3, y1 + d4, lenXZ, y1);
		}
	}

	/*======================================================================================*/

	@Override
	public RailPosition getStartRP()
	{
		return this.startRP;
	}

	@Override
	public RailPosition getEndRP()
	{
		return this.endRP;
	}

	@Override
	public double getLength()
	{
		if(this.length <= 0.0D)
		{
			double height = this.endRP.posY - this.startRP.posY;
			if(height == 0.0D)
			{
				this.length = this.lineHorizontal.getLength();
			}
			else
			{
				double d0 = this.lineHorizontal.getLength();
				this.length = Math.sqrt(d0 * d0 + height * height);
			}
		}
		return this.length;
	}

	@Override
	public int getNearlestPoint(int par1, double par2, double par3)
	{
		return this.lineHorizontal.getNearlestPoint(par1, par2, par3);
	}

	@Override
	public double[] getRailPos(int par1, int par2)
	{
		return this.lineHorizontal.getPoint(par1, par2);
	}

	@Override
	public double getRailHeight(int par1, int par2)
	{
		float railWidth = 3.0F;//本来はRailConfigから取得すべし
		double height = this.lineVertical.getPoint(par1, par2)[1];
		float cant = this.getCant(par1, par2);
		if(cant != 0.0F)
		{
			double h2 = Math.abs(NGTMath.sin(cant) * railWidth * 0.5F);
			height += h2;
		}
		return height;
	}

	@Override
	public float getRailYaw(int par1, int par2)
	{
		return NGTMath.toDegrees((float)this.lineHorizontal.getSlope(par1, par2));
	}

	@Override
	public float getRailPitch(int par1, int par2)
	{
		return NGTMath.toDegrees((float)this.lineVertical.getSlope(par1, par2));
	}

	@Override
	public float getRailRoll(int split, int t)
	{
		/*float yawStart = NGTMath.toDegrees((float)this.lineHorizontal.getSlope(split, 0));
		float yawEnd = NGTMath.toDegrees((float)this.lineHorizontal.getSlope(split, split));
		float yawT = NGTMath.toDegrees((float)this.lineHorizontal.getSlope(split, t));
		float yawDifA = Math.abs(MathHelper.wrapDegrees(yawEnd - yawStart));
		if(yawDifA == 0.0F)
		{
			return 0.0F;//始点と終点の向きが正反対、S字も含むので下記と別処理を要検討
		}
		else
		{
			float yawT2 = yawT - yawStart;
			float yawT2A = Math.abs(yawT2);
			float yawH = yawDifA * 0.5F;
			if(yawT2A > yawH)
			{
				yawT2A = yawDifA - yawT2A;
			}
			float yawRes = yawT2A / yawH;//0.0~1.0
			if(yawT2 > 0.0F)
			{
				yawRes = -yawRes;
			}
			return yawRes;
		}*/

		float ft = 2.0F * (float)t / (float)split;
		//endのcantは反転させる
		float c1 = (ft <= 1.0F) ? (1.0F - ft) * this.startRP.cantEdge : (ft - 1.0F) * -this.endRP.cantEdge;
		//cuntCenterは共通
		float c2 = (ft <= 1.0F) ? ft * this.startRP.cantCenter : (2.0F - ft) * this.startRP.cantCenter;
		float cunt = (c1 + c2);

		float rand = 0.0F;
		if(this.startRP.cantRandom > 0.0F)
		{
			float x = (float)(this.getLength() * (float)(t) / (float)(split)) * this.startRP.cantRandom;
			float scale = 3.0F;
			rand = NGTMath.getSin(x) + NGTMath.getSin(x * 0.51F) + NGTMath.getSin(x * 0.252F) + NGTMath.getSin(x * 0.1253F) * 0.25F * scale;
		}
		return cunt + rand;
	}

	/*======================================================================================*/

	public boolean hasPoint(int x, int z)
	{
		boolean flag1 = this.startRP.blockX == x && this.startRP.blockZ == z;
		boolean flag2 = this.endRP.blockX == x && this.endRP.blockZ == z;
		return flag1 || flag2;
	}

	/**両端にRS入力されてるか*/
	public boolean isGettingPowered(World world)
	{
		return this.startRP.checkRSInput(world) && this.endRP.checkRSInput(world);
	}
}