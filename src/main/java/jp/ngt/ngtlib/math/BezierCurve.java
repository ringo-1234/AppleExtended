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

package jp.ngt.ngtlib.math;


public final class BezierCurve implements ILine
{
	public static final int QUANTIZE = 32;

	public final double[] sp;
	public final double[] cpS;
	public final double[] cpE;
	public final double[] ep;
	private float[] normalizedParameters;
	private final double length;
	private final int split;

	public BezierCurve(double p1, double p2, double p3, double p4, double p5, double p6, double p7, double p8)
	{
		this.sp = new double[]{p1, p2};
		this.cpS = new double[]{p3, p4};
		this.cpE = new double[]{p5, p6};
		this.ep = new double[]{p7, p8};
		this.length = this.calcLength();
		this.split = (int)(this.length * QUANTIZE);
	}

	@Override
	public double[] getPoint(int par1, int par2)
	{
		return this.getPointFromParameter(this.getHomogenizedParameter(par1, par2));
	}

	private double[] getPointFromParameter(double par1)
	{
		double t = par1 < 0 ? 0.0D : (par1 > 1 ? 1.0D : par1);
		double tp = 1.0D - t;

		double d0 = t * t * t;
		double d1 = 3.0D * t * t * tp;
		double d2 = 3.0D * t * tp * tp;
		double d3 = tp * tp * tp;
		double x = d0*ep[0] + d1*cpE[0] + d2*cpS[0] + d3*sp[0];
		double y = d0*ep[1] + d1*cpE[1] + d2*cpS[1] + d3*sp[1];
		return LinePosPool.get(x, y);
	}

	@Override
	public int getNearlestPoint(int par1, double par2, double par3)
	{
		int i = 0;
		double pd = Double.MAX_VALUE;

		for(int j = 0; j < par1; ++j)
		{
			double[] point = this.getPoint(par1, j);
			double dx = par2 - point[1];
			double dy = par3 - point[0];
			double distance = (dx * dx) + (dy * dy);
			if(distance < pd)
			{
				pd = distance;
				i = j;
			}
		}

		return pd < Double.MAX_VALUE ? i : -1;
	}

	@Override
	public double getSlope(int par1, int par2)
	{
		return this.getSlopeFromParameter(this.getHomogenizedParameter(par1, par2));
	}

	private double getSlopeFromParameter(double par1)
	{
		double t = par1 < 0 ? 0.0D : (par1 > 1 ? 1.0D : par1);
		double tp = 1.0D - t;

		double d0 = t*t;
		double d1 = 2.0D*t*tp;
		double d2 = tp*tp;
		double dx = 3.0D*(d0*(ep[0]-cpE[0]) + d1*(cpE[0]-cpS[0]) + d2*(cpS[0]-sp[0]));
		double dy = 3.0D*(d0*(ep[1]-cpE[1]) + d1*(cpE[1]-cpS[1]) + d2*(cpS[1]-sp[1]));
		return Math.atan2(dy, dx);
	}

	private float getHomogenizedParameter(int n, int par2)
	{
		if(n < 4)
		{
			return 0.0F;
		}

		if(par2 <= 0)
		{
			return 0.0F;
		}
		else if(par2 >= n)
		{
			return 1.0F;
		}

		if(this.normalizedParameters == null)
		{
			this.initNP();
		}

		int i0 = NGTMath.floor((float)par2 * (float)this.split / (float)n);

		if (this.normalizedParameters.length == 0) {
			return 0.0f;
		}

		return this.normalizedParameters[i0];
	}

	private void initNP()
	{
		this.normalizedParameters = new float[this.split];

		float ni = 1.0F / (float)split;
		float[] dd = new float[split + 1];

		float tt = 0.0F;
		double[] p = this.sp;
		double[] q = new double[2];

		dd[0] = 0;
		for(int i = 1; i < split + 1; i++)
		{
			tt += ni;
			q = this.getPointFromParameter(tt);
			dd[i] = dd[i-1] + (float)this.getDistance(p[0], q[0], p[1], q[1]);
			p = q;
		}

		for(int i = 1; i < split + 1; i++)
		{
			dd[i] /= dd[split];
		}

		for(int i = 0; i < split; ++i)
		{
			float t = (float)i / (float)split;
			int k = 0;

			int searchMin = 0, searchMax = split - 1;
			int loopTimes = 0;
			while (!(dd[k] <= t && t <= dd[k + 1])) {
				k = (searchMax - searchMin) / 2 + searchMin;
				if (dd[searchMin] <= t && t <= dd[k + 1]){
					searchMax = k;
				} else {
					searchMin = k;
				}
				if (++loopTimes >= split - 1)
					break;
			}

			float x = (t - dd[k]) / (dd[k + 1] - dd[k]);
			x = (k * (1 - x) + (1 + k) * x) * (1.0F / (float)split);
			this.normalizedParameters[i] = x;
		}
	}

	@Override
	public double getLength()
	{
		return this.length;
	}

	private double calcLength()
	{
		double x0 = this.sp[0] - this.ep[0];
		double y0 = this.sp[1] - this.ep[1];
		double l0 = Math.sqrt(x0 * x0 + y0 * y0);

		int n = NGTMath.floor(l0 * 2.0D);
		float ni = 1 / (float)n;
		float tt = 0.0F;
		double[] p = this.sp;
		double[] q = new double[2];
		double[] dd = new double[n + 1];

		dd[0] = 0;
		for(int i = 1; i < n + 1; i++)
		{
			tt += ni;
			q = this.getPointFromParameter(tt);
			dd[i] = dd[i-1] + this.getDistance(p[0], q[0], p[1], q[1]);
			p = q;
		}

		return dd[n];
	}

	private double getDistance(double par1, double par2, double par3, double par4)
	{
		double xDis = Math.abs(par1 - par2);
		double yDis = Math.abs(par3 - par4);
		return Math.sqrt(xDis * xDis + yDis * yDis);
	}
}