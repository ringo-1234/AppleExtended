package jp.ngt.ngtlib.renderer;

public class GLObject
{
	public int value;
	public boolean delFlag;

	protected GLObject(int par1)
	{
		this.value = par1;
	}

	public void setDelFlag(boolean par1)
	{
		this.delFlag = par1;
	}
}