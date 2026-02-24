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

package jp.ngt.rtm.entity;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.electric.SignalLevel;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EntityTrainDetector extends EntityElectricalWiring
{
	private boolean findTrain;

	public EntityTrainDetector(World world)
	{
		super(world);
		this.setSize(1.0F, 0.0625F);
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit()
    {
		super.entityInit();
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
    }

	@Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
    }

	@Override
    public void onUpdate()
    {
    	if(!this.world.isRemote)
    	{
    		this.findTrain = false;
    		for(int i = 0; i < 8; ++i)
    		{
    			TileEntity tile = BlockUtil.getTileEntity(this.world,
    					this.tileEW.getPos().getX(), this.tileEW.getPos().getY() - i, this.tileEW.getPos().getZ());
        		if(tile != null && tile instanceof TileEntityLargeRailBase)
        		{
        			this.findTrain = ((TileEntityLargeRailBase)tile).isTrainOnRail();
        			break;
        		}
    		}
    	}

    	super.onUpdate();
    }

	@Override
	public int getElectricity()
	{
		return this.findTrain ? SignalLevel.STOP.level : SignalLevel.PROCEED.level;
	}

	@Override
	public void setElectricity(int par1){}

	@Override
    protected void dropItems()
    {
    	this.entityDropItem(new ItemStack(RTMItem.installedObject, 1, IstlObjType.TRAIN_DETECTOR.id), 0.0F);
    }

	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_ANTENNA_RECEIVE;
	}
}