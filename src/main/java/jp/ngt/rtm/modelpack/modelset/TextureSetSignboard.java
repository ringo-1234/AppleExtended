package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.modelpack.cfg.SignboardConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TextureSetSignboard extends TextureSetBase<SignboardConfig>
{
	public TextureSetSignboard()
	{
		super();
	}

	public TextureSetSignboard(SignboardConfig par1)
	{
		super(par1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		SignboardConfig cfg = this.getConfig();
		if(cfg.texture.contains("textures"))
		{
			this.texture = new ResourceLocation(cfg.texture);
		}
		else
		{
			//1.7.10.31互換性
			this.texture = new ResourceLocation("textures/signboard/" + cfg.texture + ".png");
		}
	}

	@Override
	public SignboardConfig getDummyConfig()
	{
		SignboardConfig dummy = new SignboardConfig();
		dummy.texture = "textures/signboard/default.png";
		dummy.width = 1.0F;
		dummy.height = 1.0F;
		return dummy;
	}
}