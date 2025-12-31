package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.ngtlib.io.ResourceLocationCustom;
import jp.ngt.rtm.modelpack.cfg.TextureConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TextureSetBase<T extends TextureConfig> extends ResourceSet<T>
{
	@SideOnly(Side.CLIENT)
	public ResourceLocation texture;

	public TextureSetBase()
	{
		super();
	}

	public TextureSetBase(T par1)
	{
		super(par1);
	}

	@Override
	public T getConfig()
	{
		return this.cfg;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		this.texture = new ResourceLocationCustom("minecraft", this.getConfig().texture);
	}
}