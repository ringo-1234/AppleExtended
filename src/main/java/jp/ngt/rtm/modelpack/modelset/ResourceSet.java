package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.cfg.ResourceConfig;
import jp.ngt.rtm.modelpack.state.DataFormatter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**コンフィグから生成したモデルデータ等の保持に使用*/
public abstract class ResourceSet<T extends ResourceConfig>
{
	protected final T cfg;
	public final DataFormatter dataFormatter;
	private boolean isDummySet;

	public byte[] md5;

	/**ダミー用*/
	public ResourceSet()
	{
		this.cfg = this.getDummyConfig();
		this.dataFormatter = new DataFormatter(this.cfg);
		this.isDummySet = true;
	}

	public ResourceSet(T par1)
	{
		this.cfg = par1;
		this.dataFormatter = new DataFormatter(this.cfg);
		this.isDummySet = false;
	}

	/**ここでモデルデータの生成等を行う*/
	public void constructOnServer(){}

	/**ここでモデルデータの生成等を行う*/
	@SideOnly(Side.CLIENT)
	public void constructOnClient(){}

	/**当たり判定生成用*/
	@SideOnly(Side.CLIENT)
	public void finishConstruct(){}

	public T getConfig()
	{
		return this.cfg;
	}

	/**ダミー用ResourceSet生成時に使用*/
	public abstract T getDummyConfig();

	public boolean isDummy()
	{
		return this.isDummySet;
	}
}