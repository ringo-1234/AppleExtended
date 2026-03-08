package jp.apple.gui;

import jp.ngt.ngtlib.block.TileEntityPlaceable;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AppleGuiHelper {
    public static void openOffsetGui(TileEntity te) {
        if (te instanceof TileEntityPlaceable) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiAppleChangeOffset((TileEntityPlaceable) te));
        }
    }
}