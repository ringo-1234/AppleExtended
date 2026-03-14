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

package jp.apple.gui;

import jp.ngt.ngtlib.block.TileEntityPlaceable;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AppleGuiHelper {
    public static void openOffsetGui(TileEntity te) {
        if (te.getWorld().isRemote) {
            ClientProxy.open(te);
        }
    }

    @SideOnly(Side.CLIENT)
    private static class ClientProxy {
        private static void open(TileEntity te) {
            if (te instanceof TileEntityPlaceable) {

                net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(
                        new jp.apple.gui.GuiAppleChangeOffset((TileEntityPlaceable) te)
                );
            }
        }
    }
}