package jp.ngt.rtm.entity.train;

import jp.ngt.ngtlib.renderer.model.MCModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelTrainBase extends MCModel
{
	public ModelTrainBase()
	{
		this(1024, 1024);
	}

	public ModelTrainBase(int width, int height)
	{
		this.init();
	}

	public abstract void init();
}