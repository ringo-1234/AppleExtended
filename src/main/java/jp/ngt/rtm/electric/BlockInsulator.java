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

package jp.ngt.rtm.electric;

import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.item.ItemInstalledObject;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockInsulator extends BlockConnector {
    public BlockInsulator() {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TileEntityInsulator();
    }

    @Override
    protected ItemStack getItem(int damage) {
        return new ItemStack(RTMItem.installedObject, 1, 3);
    }

    @Override
    protected ItemInstalledObject.IstlObjType istlObjType(int damage) {
        return ItemInstalledObject.IstlObjType.INSULATOR;
    }
}