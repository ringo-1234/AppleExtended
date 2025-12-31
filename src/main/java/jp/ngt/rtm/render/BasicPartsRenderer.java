package jp.ngt.rtm.render;

import java.util.List;

import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**独自のPartsRendererがない場合に使用*/
@SideOnly(Side.CLIENT)
public class BasicPartsRenderer<T, MS extends ModelSetBase> extends PartsRenderer<T, MS>
{
	private Parts main;

	public BasicPartsRenderer(String... par1)
	{
		super(par1);
	}

	@Override
	public void init(MS par1, ModelObject par2)
	{
		List<GroupObject> goList = par2.model.getGroupObjects();
		String[] array = new String[goList.size()];
		for(int i = 0; i < array.length; ++i)
		{
			array[i] = goList.get(i).name;
		}
		this.main = this.registerParts(new Parts(array));

		super.init(par1, par2);
	}

	@Override
	public void render(T entity, RenderPass pass, float par3)
	{
		//発光パーツ描画のためpass指定しない
		if(this.main != null)//マルチでnullになる場合あり
		{
			this.main.render(this);
		}
	}

	@Override
	public World getWorld(T entity)
	{
		return null;
	}
}