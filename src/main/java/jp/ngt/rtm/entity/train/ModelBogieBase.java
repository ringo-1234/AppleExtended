package jp.ngt.rtm.entity.train;

import jp.ngt.ngtlib.renderer.model.MCModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelBogieBase extends MCModel
{
	public ModelBogieBase()
	{
		this(256, 256);
	}

	public ModelBogieBase(int width, int height)
	{
		this.init();
	}

	public abstract void init();
}