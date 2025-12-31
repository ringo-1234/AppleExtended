package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.electric.Connection.ConnectionType;

public abstract class TileEntityConnector extends TileEntityConnectorBase
{
	private int prevOutputSignal = -1;

	@Override
	public void onGetElectricity(int x, int y, int z, int level, int counter)
    {
		if(this.getSubType() == RTMResource.CONNECTOR_INPUT)
		{
			super.onGetElectricity(x, y, z, level, counter);
		}
    }

	@Override
	protected void sendElectricity(Connection connection, int level, int counter)
    {
		if(this.getSubType() == RTMResource.CONNECTOR_INPUT && connection.type == ConnectionType.DIRECT)//in
		{
			IProvideElectricity provider = connection.getIProvideElectricity(this.world);
			if(provider != null)
			{
				provider.setElectricity(this.getX(), this.getY(), this.getZ(), level);
			}
		}
		else
		{
			super.sendElectricity(connection, level, counter);
		}
    }

	@Override
	public void update()
    {
    	super.update();

    	if(!this.world.isRemote)
    	{
    		if(this.getSubType() == RTMResource.CONNECTOR_OUTPUT)
    		{
    			this.checkSignalOutput();
    		}
    	}
    }

	private void checkSignalOutput()
	{
		Connection connection = this.getBlockConnection();
		if(connection == null){return;}

		IProvideElectricity provider = connection.getIProvideElectricity(this.world);
		if(provider != null)
		{
			int level = provider.getElectricity();
			if(level != this.prevOutputSignal)
			{
				//this.onGetElectricity(connection.x, connection.y, connection.z, level, 0);
				this.sendElectricityToAll(level);
				this.prevOutputSignal = level;
			}
		}
	}

	/**接続タイプ3(TileEntity直付)のを返す*/
	private Connection getBlockConnection()
	{
		for(Connection connection : this.connections)
    	{
			if(connection.type == ConnectionType.DIRECT)
			{
				return connection;
			}
    	}
		return null;
	}
}