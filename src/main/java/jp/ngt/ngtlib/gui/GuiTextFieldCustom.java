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

package jp.ngt.ngtlib.gui;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class GuiTextFieldCustom extends GuiTextField {
    protected final FontRenderer fontRenderer;
    protected String text = "";
    protected int maxStringLength = 32;
    protected int cursorCounter;
    protected boolean enableBackgroundDrawing = true;
    protected boolean canLoseFocus = true;
    protected boolean isFocused;
    protected boolean isEnabled = true;
    protected int lineScrollOffset;
    protected int cursorPosition;
    protected int selectionEnd;
    protected int enabledColor = 0xE0E0E0;
    protected int disabledColor = 0x707070;
    protected boolean visible = true;
    @Nullable
    private final GuiScreen screen;
    private final List<String> tips = new ArrayList<>();
    protected boolean isDisplayMode;
    private TextFieldListener listener;

    public GuiTextFieldCustom(int id, FontRenderer par1, int x, int y, int w, int h, GuiScreen pScr) {
        super(id, par1, x, y, w, h);
        this.fontRenderer = par1;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        this.screen = pScr;
    }

    public void setListener(TextFieldListener par1) {
        this.listener = par1;
    }

    public GuiTextFieldCustom addTips(String tip) {
        this.tips.add(tip);
        return this;
    }

    public void setDisplayMode(boolean par1) {
        this.isDisplayMode = par1;
        this.setEnabled(!par1);
    }

    @Override
    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    @Override
    public void setText(String par1) {
        if (par1.length() > this.maxStringLength) {
            this.text = par1.substring(0, this.maxStringLength);
        } else {
            this.text = par1;
        }
        this.setCursorPositionEnd();
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getSelectedText() {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    @Override
    public void writeText(String par1) {
        String s1 = "";
        String s2 = ChatAllowedCharacters.filterAllowedCharacters(par1);
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - this.selectionEnd);
        boolean flag = false;

        if (this.text.length() > 0) {
            s1 = s1 + this.text.substring(0, i);
        }

        int l;

        if (k < s2.length()) {
            s1 = s1 + s2.substring(0, k);
            l = k;
        } else {
            s1 = s1 + s2;
            l = s2.length();
        }

        if (this.text.length() > 0 && j < this.text.length()) {
            s1 = s1 + this.text.substring(j);
        }

        this.text = s1;
        this.moveCursorBy(i - this.selectionEnd + l);
    }

    @Override
    public void deleteWords(int p_146177_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    @Override
    public void deleteFromCursor(int p_146175_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = p_146175_1_ < 0;
                int j = flag ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int k = flag ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String s = "";

                if (j >= 0) {
                    s = this.text.substring(0, j);
                }

                if (k < this.text.length()) {
                    s = s + this.text.substring(k);
                }

                this.text = s;

                if (flag) {
                    this.moveCursorBy(p_146175_1_);
                }
            }
        }
    }

    @Override
    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    @Override
    public int getNthWordFromPos(int n, int p_146183_2_) {
        return this.getNthWordFromPosWS(n, this.getCursorPosition(), true);
    }

    @Override
    public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        int k = pos;
        boolean flag1 = n < 0;
        int l = Math.abs(n);

        for (int i1 = 0; i1 < l; ++i1) {
            if (flag1) {
                while (skipWs && k > 0 && this.text.charAt(k - 1) == 32) {
                    --k;
                }

                while (k > 0 && this.text.charAt(k - 1) != 32) {
                    --k;
                }
            } else {
                int j1 = this.text.length();
                k = this.text.indexOf(32, k);

                if (k == -1) {
                    k = j1;
                } else {
                    while (skipWs && k < j1 && this.text.charAt(k) == 32) {
                        ++k;
                    }
                }
            }
        }

        return k;
    }

    @Override
    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    @Override
    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int j = this.text.length();

        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > j) {
            this.cursorPosition = j;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    @Override
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    @Override
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    @Override
    public boolean textboxKeyTyped(char word, int code) {
        if (!this.isFocused) {
            return false;
        }

        switch (word) {
            case 1:
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;

            case 3:
                GuiScreen.setClipboardString(this.getSelectedText());
                return true;

            case 22:
                if (this.isEnabled) {
                    this.writeText(GuiScreen.getClipboardString());
                }
        }
        return super.textboxKeyTyped(word, code);
    }

    @Override
    public void setFocused(boolean par1) {
        if (!this.isDisplayMode) {
            this.setCursorPositionZero();
            this.setSelectionPos(this.getCursorPosition());
            if (par1 && !this.isFocused) {
                this.cursorCounter = 0;
            }

            this.isFocused = par1;
        }
    }

    public interface TextFieldListener {
        void onType(GuiTextFieldCustom var1);
    }
}