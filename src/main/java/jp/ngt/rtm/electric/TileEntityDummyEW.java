package jp.ngt.rtm.electric;

import jp.ngt.rtm.entity.EntityElectricalWiring;

public class TileEntityDummyEW extends TileEntityElectricalWiring
{
	public final EntityElectricalWiring entityEW;
	private int prevSignal;

	public TileEntityDummyEW(EntityElectricalWiring par1Entity)
	{
		super();
		this.entityEW = par1Entity;
	}

	@Override
	public void onGetElectricity(int x, int y, int z, int level, int counter)
    {
		super.onGetElectricity(x, y, z, level, counter);

		if(!(x == this.getX() && y == this.getY() && z == this.getZ()))
		{
			this.entityEW.setElectricity(level);
		}
    }

	@Override
	public void update()
    {
    	super.update();

    	if(!this.world.isRemote)
    	{
    		int level = this.entityEW.getElectricity();
			if(level >= 0 && level != this.prevSignal)
			{
				this.onGetElectricity(this.getX(), this.getY(), this.getZ(), level, 0);
				this.prevSignal = level;
			}
    	}
    }

	@Override
	public boolean isBlockTile()
    {
    	return false;
    }
}