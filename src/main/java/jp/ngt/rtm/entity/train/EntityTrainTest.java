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

package jp.ngt.rtm.entity.train;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.world.World;

public final class EntityTrainTest extends EntityTrainBase
{
	public EntityTrainTest(World world)
	{
		super(world);
	}

	public EntityTrainTest(World world, String s)
	{
		super(world, s);
	}

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.TRAIN_TEST;
	}
}