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

package jp.apple.artpe;

import jp.apple.artpe.gui.ContainerTrainPlacer;
import jp.apple.artpe.gui.GuiTrainPlacer;
import jp.apple.artpe.tileentity.TileEntityTrainPlacer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ARTPEGuiHandler implements IGuiHandler {
    public static final int GUI_ID_TRAIN_PLACER = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ID_TRAIN_PLACER) {
            return new ContainerTrainPlacer(player.inventory,
                    (TileEntityTrainPlacer) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ID_TRAIN_PLACER) {
            return new GuiTrainPlacer(new ContainerTrainPlacer(player.inventory,
                    (TileEntityTrainPlacer) world.getTileEntity(new BlockPos(x, y, z))));
        }
        return null;
    }
}