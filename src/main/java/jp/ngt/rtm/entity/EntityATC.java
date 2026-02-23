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

package jp.ngt.rtm.entity;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityATC extends EntityElectricalWiring
{
	private int signalLevel;

	public EntityATC(World world)
	{
		super(world);
		this.setSize(1.0F, 0.0625F);
		this.ignoreFrustumCheck = true;
	}

    @Override
    public boolean attackEntityFrom(DamageSource par1, float par2)
    {
    	if(!this.world.isRemote)
    	{
    		this.setSignalToRail(0);
    	}
    	return super.attackEntityFrom(par1, par2);
    }

    private void setSignalToRail(int signalLevel)
    {
    	int x = NGTMath.floor(this.posX);
		int y = NGTMath.floor(this.posY);
		int z = NGTMath.floor(this.posZ);
		for(int i = 0; i < 8; ++i)
		{
			TileEntity tile0 = BlockUtil.getTileEntity(this.world, x, y - i, z);
    		if(tile0 instanceof TileEntityLargeRailBase)
    		{
    			TileEntityLargeRailCore tile = ((TileEntityLargeRailBase)tile0).getRailCore();
    			tile.setSignal(signalLevel);
    			break;
    		}
		}
    }

	@Override
	public int getElectricity()
	{
		return -1;
	}

	@Override
	public void setElectricity(int par1)
	{
		this.signalLevel = par1;
		this.setSignalToRail(this.signalLevel);
	}

	@Override
    protected void dropItems()
    {
    	this.entityDropItem(new ItemStack(RTMItem.installedObject, 1, IstlObjType.ATC.id), 0.0F);
    }

	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_ANTENNA_SEND;
	}
}