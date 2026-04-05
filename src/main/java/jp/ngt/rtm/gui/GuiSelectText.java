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

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiSelectText extends GuiScreenCustom {
    private static final int BI_OFFSET = 100;

    private final GuiScreen parent;
    private final SelectListener listener;
    private final List<String> list;
    private int currentScroll;
    private int prevScroll;
    private int vCount;

    public GuiSelectText(GuiScreen pScreen, List<String> pList, SelectListener pListener) {
        this.parent = pScreen;
        this.listener = pListener;
        this.list = pList;
    }

    @Override
    public void initGui() {
        this.vCount = this.list.size();
        this.buttonList.clear();

        int yCount = this.vCount + 1;

        for (int v = 0; v < yCount; ++v) {
            if (v >= this.list.size()) {
                break;
            }
            String s = this.list.get(v);
            int xPos = 10;
            int yPos = v * 20;
            this.buttonList.add(new GuiButton(BI_OFFSET + v, xPos, yPos, 200, 20, s));
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 256) {
            this.mc.displayGuiScreen(this.parent);
        } else if (button.id >= BI_OFFSET && button.id < BI_OFFSET + this.list.size()) {

            this.listener.select(this.list.get(button.id - BI_OFFSET));
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();

        if (scroll != 0) {
            this.prevScroll = this.currentScroll;
            scroll = scroll > 0 ? 1 : (scroll < 0 ? -1 : 0);
            this.currentScroll -= scroll;

            if (this.currentScroll < 0) {
                this.currentScroll = 0;
            }

            this.renewButton(this.currentScroll);
        }
    }

    protected void renewButton(int scroll) {
        if (this.currentScroll != this.prevScroll) {
            int y = this.height / this.vCount;
            if (this.prevScroll > this.currentScroll) {
                y = -y;
            }

            for (int i = 0; i < this.buttonList.size(); ++i) {
                ((GuiButtonSelectTexture) this.buttonList.get(i)).moveButton(y);
            }
        }
    }

    public interface SelectListener {
        void select(String s);
    }
}
