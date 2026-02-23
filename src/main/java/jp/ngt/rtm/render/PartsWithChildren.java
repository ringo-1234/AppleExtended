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

package jp.ngt.rtm.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PartsWithChildren extends Parts
{
	public final List<Parts> childParts = new ArrayList<Parts>();

	public PartsWithChildren(String... par1)
	{
		super(par1);
	}

	public void addParts(Parts par1)
	{
		this.childParts.add(par1);
	}

	@Override
	public void init(PartsRenderer renderer)
	{
		super.init(renderer);

		for(Parts parts : this.childParts)
		{
			parts.init(renderer);
		}
	}
}