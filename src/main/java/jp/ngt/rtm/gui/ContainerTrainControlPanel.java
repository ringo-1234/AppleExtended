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

package jp.ngt.rtm.gui;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;

public class ContainerTrainControlPanel extends ContainerPlayer
{
	public final EntityVehicleBase vehicle;
	public final EntityPlayer player;
	private List slotsList;

	public ContainerTrainControlPanel(EntityVehicleBase par1, EntityPlayer par2)
	{
		super(par2.inventory, !par2.world.isRemote, par2);
		this.vehicle = par1;
		this.player = par2;
		this.slotsList = this.inventorySlots;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.equals(this.player);
	}

	public void setCurrentTab(int tabIndex)//Gui->PacketNotice
    {
		if(tabIndex == TabTrainControlPanel.TAB_Inventory.getTabIndex())
        {
			this.inventorySlots = this.slotsList;
        }
		else
        {
			this.inventorySlots = new ArrayList();
			for(int i = 0; i < 9; ++i)
            {
            	Slot slot = new Slot(this.player.inventory, i, 8 + i * 18, 142);
            	slot.slotNumber = this.inventorySlots.size();
            	this.inventorySlots.add(slot);
            }
        }
    }
}