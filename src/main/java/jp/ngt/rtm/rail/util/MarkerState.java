package jp.ngt.rtm.rail.util;

public enum MarkerState
{
	DISTANCE,
	GRID,
	LINE1,
	LINE2,
	ANCHOR21;

	private MarkerState()
	{
		;
	}

	private int bitMask()
	{
		return 1 << this.ordinal();
	}

	public boolean get(int data)
	{
		int mask = this.bitMask();
		return (data & mask) > 0;
	}

	public int set(int data, boolean state)
	{
		int mask = this.bitMask();
		if(state)
		{
			return data | mask;
		}
		else
		{
			return (data | mask) - mask;
		}
	}

	public int flip(int data)
	{
		int mask = this.bitMask();
		return data ^ mask;
	}
}
