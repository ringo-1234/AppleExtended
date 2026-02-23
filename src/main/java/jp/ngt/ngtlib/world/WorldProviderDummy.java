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

package jp.ngt.ngtlib.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

/**NGTWorld用*/
public class WorldProviderDummy extends WorldProvider
{
	@Override
	public DimensionType getDimensionType()
	{
		return DimensionType.OVERWORLD;
	}
}