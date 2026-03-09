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