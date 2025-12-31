package jp.ngt.rtm.block.tileentity;

public enum MechanismType
{
	POWER(true),
	TRANSMISSION(true),
	GEAR(false),
	PULLEY(false);

	public final boolean useRS;

	private MechanismType(boolean par1)
	{
		this.useRS = par1;
	}
}
