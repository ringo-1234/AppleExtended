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

package jp.apple.reloader;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReloadGuiHandler {

    private static final int BUTTON_ID = 9927;

    private GuiButton reloadButton;

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu) {

            int x = event.getGui().width - 105;
            int y = 5;


            this.reloadButton = new GuiButton(BUTTON_ID, x, y, 100, 20, "§bReLoad Packs");

            event.getButtonList().add(this.reloadButton);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu && this.reloadButton != null) {

            boolean busy = PackReloader.isSystemBusy();


            this.reloadButton.enabled = !busy;

            if (busy) {
                this.reloadButton.displayString = "§7Loading...";
            } else {
                this.reloadButton.displayString = "§bReLoad Packs";
            }
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == BUTTON_ID) {

            if (!PackReloader.isSystemBusy()) {
                PackReloader.startReload();
            }
        }
    }
}