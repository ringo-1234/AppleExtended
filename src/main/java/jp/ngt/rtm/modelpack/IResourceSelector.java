/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

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