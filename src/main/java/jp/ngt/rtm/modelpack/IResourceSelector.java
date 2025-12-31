package jp.ngt.rtm.modelpack;

import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IResourceSelector<T extends ResourceSet>
{
	ResourceState<T> getResourceState();

	/**ResourceState更新必要時に呼び出し*/
	void updateResourceState();

	/**{x,y,z} or {entityId, -1, 0}*/
	int[] getSelectorPos();

	/**trueならState情報を同期*/
	@SideOnly(Side.CLIENT)
	boolean closeGui(ResourceState par1);
}