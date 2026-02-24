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

package jp.ngt.rtm.modelpack.state;

import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class ResourceStateRail extends ResourceStateWithBlock<ModelSetRail>
{
	public static final float INIT_HEIGHT = 0.0625F;

	public float blockHeight = INIT_HEIGHT;

	public ResourceStateRail(ResourceType type, Object entity)
	{
		super(type, entity);
	}

	public void setHeight(float par1)
	{
		this.blockHeight = (par1 <= 0.0F) ? 0.0625F : par1;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.setHeight(nbt.getFloat("BlockHeight"));
	}

	@Override
	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = super.writeToNBT();
		nbt.setFloat("BlockHeight", this.blockHeight);
		return nbt;
	}

	@Override
	public void setResourceToDefault()
	{
		super.setResourceToDefault();
		this.setBlock(Blocks.GRAVEL, 0);
		this.setHeight(0.0625F);
	}
}