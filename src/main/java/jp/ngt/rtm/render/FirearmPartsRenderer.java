package jp.ngt.rtm.render;

import jp.ngt.rtm.modelpack.modelset.ModelSetFirearm;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FirearmPartsRenderer extends EntityPartsRenderer<ModelSetFirearm>
{
	public FirearmPartsRenderer(String... par1)
	{
		super(par1);
	}
}