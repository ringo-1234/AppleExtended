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
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tt.SignboardText;
import jp.ngt.rtm.block.tt.SignboardText.AnimeType;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.modelset.TextureSetSignboard;
import jp.ngt.rtm.modelpack.state.ResourceStateSignboard;
import jp.ngt.rtm.network.PacketSelectResource;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiSignboard extends GuiScreenCustom {
    private static final int COLOR_ON = 0xFFFFFF;
    private static final int COLOR_OFF = 0xA0A0A0;
    /**
     * 単位:pixel
     */
    private static final float SELECT_TICKNESS = 3.0F;

    private final IResourceSelector<TextureSetSignboard> selector;
    private ResourceStateSignboard state;
    private SignboardText currentTextObj;

    private GuiTextFieldCustom fieldText;
    private GuiButton buttonFont;
    private GuiTextFieldCustom fieldColor;
    private GuiButton buttonBold;
    private GuiButton buttonItalic;
    private GuiTextFieldCustom fieldTextSize;
    private GuiTextFieldCustom fieldTextWidth;
    private GuiTextFieldCustom fieldTextPosU, fieldTextPosV;
    private GuiButton buttonAnimeType;
    private GuiTextFieldCustom fieldAnimeSpeed;
    private GuiTextFieldCustom fieldStationSetting;
    //private GuiButton buttonTimeTable;
    private GuiButton dummyButton;

    private int style;
    private int animeTypeIndex;

    private float scale;
    private boolean resetState;

    private boolean draging;
    /**
     * bitごとに状態保持<br>
     * 3:NegX, 2:PosX, 1:NegY, 0:PosY
     *
     */
    private int dragState;
    private int prevDragX, prevDragY;

    public GuiSignboard(IResourceSelector par1) {
        this.selector = par1;
        this.resetState = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        if (this.resetState) {
            this.state = (ResourceStateSignboard) this.selector.getResourceState();
            this.resetState = false;
        }

        int hw = this.width / 2;
        int buttonArea = 130;
        int ba1 = this.width - buttonArea;
        int ba2 = ba1 + 5;
        int h2 = this.height - 30;

        this.buttonList.clear();

        //テクスチャ描画のため
        TextureSetSignboard set = this.state.getResourceSet();
        float sbw = set.getConfig().width;
        if (set.getConfig().backTexture == 1) {
            sbw *= 2.0F;
        }
        float ratioTex = set.getConfig().height / sbw;
        float ratioGui = (float) h2 / (float) ba1;
        int sizeX = 0;
        int sizeY = 0;
        if (ratioTex > ratioGui) {
            sizeX = (int) (h2 / ratioTex);
            sizeY = h2;
        } else {
            sizeX = ba1;
            sizeY = (int) (ba1 * ratioTex);
        }
        this.scale = (float) sizeY / set.getConfig().height;
        this.dummyButton = new GuiButtonSelectTexture(300, 0, 0, sizeX, sizeY, RTMResource.SIGNBOARD, set);

        this.addButton(new GuiButton(0, hw - 155, this.height - 25, 150, 20, I18n.format("gui.done", new Object[0])));
        this.addButton(new GuiButton(1, hw + 5, this.height - 25, 150, 20, I18n.format("gui.cancel", new Object[0])));

        this.addButton(new GuiButton(100, ba2, 5, 120, 20, "Select texture"));
        this.addButton(new GuiButton(101, ba2, 25, 30, 20, "Add"));
        this.addButton(new GuiButton(102, ba2 + 30, 25, 30, 20, "Copy"));
        this.addButton(new GuiButton(103, ba2 + 60, 25, 30, 20, "Del"));
        this.addButton(new GuiButton(104, ba2 + 90, 25, 30, 20, "Redraw"));
        //this.buttonTimeTable = this.addButton(new GuiButton(104, ba2, 215, 120, 20, this.state.getTimeTable().ttName));

        List<SignboardText> list = this.state.texts;
        if (this.currentTextObj == null && !list.isEmpty()) {
            this.currentTextObj = list.get(0);
        }

        if (this.currentTextObj != null) {
            this.fieldText = this.setTextField(ba2, 48, 120, 20, this.currentTextObj.getRawText());
            this.fieldText.addTips("If use timetable -> tt(col=<colmun_num>,offset=<offset_num>)");
            this.fieldText.addTips("  col=-1 -> display train name");
            this.buttonFont = this.addButton(new GuiButton(200, ba2, 70, 120, 20, this.currentTextObj.getText().getFont()));
            String colorCode = "0x" + Integer.toHexString(this.currentTextObj.getText().getColor());
            this.fieldColor = this.setTextField(ba2, 100, 80, 20, colorCode);
            this.buttonBold = this.addButton(new GuiButton(201, ba2 + 80, 100, 20, 20, "B"));
            this.buttonItalic = this.addButton(new GuiButton(202, ba2 + 100, 100, 20, 20, "I"));
            this.fieldTextSize = this.setTextField(ba2, 130, 60, 20, String.valueOf(this.currentTextObj.size));
            this.fieldTextWidth = this.setTextField(ba2 + 60, 130, 60, 20, String.valueOf(this.currentTextObj.width));
            this.fieldTextPosU = this.setTextField(ba2, 160, 60, 20, String.valueOf(this.currentTextObj.posU));
            this.fieldTextPosV = this.setTextField(ba2 + 60, 160, 60, 20, String.valueOf(this.currentTextObj.posV));
            this.buttonAnimeType = this.addButton(new GuiButton(203, ba2, 190, 60, 20, this.currentTextObj.animeType.toString()));
            this.fieldAnimeSpeed = this.setTextField(ba2 + 60, 190, 60, 20, String.valueOf(this.currentTextObj.animeSpeed));
            this.fieldStationSetting = this.setTextField(ba2, 220, 120, 20, this.state.getTTSetting());
            this.fieldStationSetting.addTips("tt=<file_name>,station=<station_name>,track=<track_num>");

            this.animeTypeIndex = this.currentTextObj.animeType.ordinal();
            this.style = this.currentTextObj.getText().getStyle();
            this.buttonBold.packedFGColour = ((this.style & Font.BOLD) != 0) ? COLOR_ON : COLOR_OFF;
            this.buttonItalic.packedFGColour = ((this.style & Font.ITALIC) != 0) ? COLOR_ON : COLOR_OFF;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.dummyButton.drawButton(this.mc, par1, par2, par3);//ホバーテキストが隠れないように先に描画
        super.drawScreen(par1, par2, par3);

        for (SignboardText text : this.state.texts) {
            float x = text.posU * this.scale;
            float y = text.posV * this.scale;
            text.render(x, y, this.zLevel, this.scale);

            if (text == this.currentTextObj) {
                this.drawSelectedText(text, x, y, par1, par2);
            }
        }

        if (this.currentTextObj != null) {
            int buttonArea = 130;
            int ba1 = this.width - buttonArea;
            int ba2 = ba1 + 5;
            this.drawString(this.fontRenderer, " Color               Style", ba2, 91, 0xFFFFFF);
            this.drawString(this.fontRenderer, " height         Width", ba2, 121, 0xFFFFFF);
            this.drawString(this.fontRenderer, " PosU           PosV", ba2, 151, 0xFFFFFF);
            this.drawString(this.fontRenderer, " AnimeType      AnimeSpeed", ba2, 181, 0xFFFFFF);
            this.drawString(this.fontRenderer, " StationSetting", ba2, 211, 0xFFFFFF);
        }

        if (this.draging) {
            this.updateDragState(par1, par2);
        }
    }

    private void drawSelectedText(SignboardText text, float x, float y, int mouseX, int mouseY) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorOpaque_I(0xFF0000);
        NGTRenderHelper.addQuadGuiFrameWithSize(x, y, text.width * this.scale, text.size * this.scale, this.zLevel);
        tessellator.draw();

        int state = this.getClickState(text, mouseX, mouseY);
        boolean moveNX = (state & (1 << 3)) > 0;
        boolean movePX = (state & (1 << 2)) > 0;
        boolean moveNY = (state & (1 << 1)) > 0;
        boolean movePY = (state & 1) > 0;

        float hSize = SELECT_TICKNESS;
        float minX = text.posU * this.scale;
        float minY = text.posV * this.scale;
        float maxX = text.width * this.scale + minX;
        float maxY = text.size * this.scale + minY;

        if ((moveNX && moveNY) || (moveNX && movePY) || (movePX && moveNY) || (movePX && movePY)) {
            //角選択
            tessellator.startDrawing(GL11.GL_QUADS);
            tessellator.setColorOpaque_I(0xFF0000);
            NGTRenderHelper.addQuadGuiFace(minX - hSize, minY - hSize, minX + hSize, minY + hSize, this.zLevel);
            NGTRenderHelper.addQuadGuiFace(maxX - hSize, minY - hSize, maxX + hSize, minY + hSize, this.zLevel);
            NGTRenderHelper.addQuadGuiFace(minX - hSize, maxY - hSize, minX + hSize, maxY + hSize, this.zLevel);
            NGTRenderHelper.addQuadGuiFace(maxX - hSize, maxY - hSize, maxX + hSize, maxY + hSize, this.zLevel);
            tessellator.draw();
        } else if (moveNX || movePX || moveNY || movePY) {
            //辺選択
            float centerX = (minX + maxX) * 0.5F;
            float centerY = (minY + maxY) * 0.5F;
            tessellator.startDrawing(GL11.GL_QUADS);
            tessellator.setColorOpaque_I(0xFF0000);
            NGTRenderHelper.addQuadGuiFace(minX - hSize, centerY - hSize, minX + hSize, centerY + hSize, this.zLevel);
            NGTRenderHelper.addQuadGuiFace(maxX - hSize, centerY - hSize, maxX + hSize, centerY + hSize, this.zLevel);
            NGTRenderHelper.addQuadGuiFace(centerX - hSize, minY - hSize, centerX + hSize, minY + hSize, this.zLevel);
            NGTRenderHelper.addQuadGuiFace(centerX - hSize, maxY - hSize, centerX + hSize, maxY + hSize, this.zLevel);
            tessellator.draw();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void updateDragState(int x, int y) {
        if (!Mouse.isButtonDown(0)) {
            this.draging = false;
        }

        if (this.currentTextObj != null) {
            float difX = (float) (x - this.prevDragX) / this.scale;
            float difY = (float) (y - this.prevDragY) / this.scale;

            if (this.dragState == 0) {
                this.currentTextObj.posU += difX;
                this.currentTextObj.posV += difY;
            } else {
                boolean moveNX = (this.dragState & (1 << 3)) > 0;
                boolean movePX = (this.dragState & (1 << 2)) > 0;
                boolean moveNY = (this.dragState & (1 << 1)) > 0;
                boolean movePY = (this.dragState & 1) > 0;

                if (moveNX) {
                    this.currentTextObj.posU += difX;
                    this.currentTextObj.width -= difX;
                }

                if (movePX) {
                    this.currentTextObj.width += difX;
                }

                if (moveNY) {
                    this.currentTextObj.posV += difY;
                    this.currentTextObj.size -= difY;
                }

                if (movePY) {
                    this.currentTextObj.size += difY;
                }
            }

            this.fieldTextPosU.setText(String.valueOf(this.currentTextObj.posU));
            this.fieldTextPosV.setText(String.valueOf(this.currentTextObj.posV));
            this.fieldTextSize.setText(String.valueOf(this.currentTextObj.size));
            this.fieldTextWidth.setText(String.valueOf(this.currentTextObj.width));
            this.prevDragX = x;
            this.prevDragY = y;
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        SignboardText clickedObj = null;
        float minX = 0.0F, maxX = 0.0F, minY = 0.0F, maxY = 0.0F;

        for (SignboardText text : this.state.texts) {
            minX = text.posU * this.scale;
            minY = text.posV * this.scale;
            maxX = text.width * this.scale + minX;//th * text.getText().getWidth() / text.getText().getHeight();
            maxY = text.size * this.scale + minY;
            if (x >= minX - SELECT_TICKNESS && x <= maxX + SELECT_TICKNESS && y >= minY - SELECT_TICKNESS && y <= maxY + SELECT_TICKNESS) {
                clickedObj = text;
                break;
            }
        }

        boolean selected = (this.currentTextObj != null && this.currentTextObj == clickedObj);

        if (clickedObj != null) {
            this.updateText();
            this.currentTextObj = clickedObj;
            this.initGui();

            if (selected) {
                this.draging = true;
                this.prevDragX = x;
                this.prevDragY = y;
                this.dragState = this.getClickState(clickedObj, x, y);
            }
        }
    }

    private int getClickState(SignboardText text, int x, int y) {
        int state = 0;
        float minX = text.posU * this.scale;
        float minY = text.posV * this.scale;
        float maxX = text.width * this.scale + minX;
        float maxY = text.size * this.scale + minY;

        if (this.inRange(x, minX, SELECT_TICKNESS)) {
            state += 1 << 3;
        }

        if (this.inRange(x, maxX, SELECT_TICKNESS)) {
            state += 1 << 2;
        }

        if (this.inRange(y, minY, SELECT_TICKNESS)) {
            state += 1 << 1;
        }

        if (this.inRange(y, maxY, SELECT_TICKNESS)) {
            state += 1;
        }

        return state;
    }

    private boolean inRange(float target, float center, float tickness) {
        return target >= (center - tickness) && target <= (center + tickness);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.saveState();
            this.mc.displayGuiScreen(null);
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(null);
        } else if (button.id == 100)//select texture
        {
            this.saveState();
            this.resetState = true;
            this.mc.displayGuiScreen(new GuiSelectTexture(this.selector, this));
        } else if (button.id == 101)//add
        {
            this.updateText();
            this.currentTextObj = new SignboardText(true, this.state.geTimeTable());
            this.state.texts.add(this.currentTextObj);
            this.initGui();
        } else if (button.id == 102)//copy
        {
            if (this.currentTextObj != null) {
                this.updateText();
                this.currentTextObj = this.currentTextObj.copy();
                this.state.texts.add(this.currentTextObj);
                this.initGui();
            }
        } else if (button.id == 103)//del
        {
            if (this.currentTextObj != null) {
                this.state.texts.remove(this.currentTextObj);
                this.currentTextObj = null;
                this.initGui();
            }
        } else if (button.id == 104)//redraw
        {
            this.updateText();
        }
		/*else if(button.id == 104)//TimeTable
        {
			this.openTTGui(button.displayString);
        }*/
        else if (button.id == 200)//font
        {
            this.openFontGui(this.currentTextObj.getText().getFont());
        } else if (button.id == 201) {
            this.style ^= Font.BOLD;
            this.buttonBold.packedFGColour = ((this.style & Font.BOLD) != 0) ? COLOR_ON : COLOR_OFF;
        } else if (button.id == 202) {
            this.style ^= Font.ITALIC;
            this.buttonItalic.packedFGColour = ((this.style & Font.ITALIC) != 0) ? COLOR_ON : COLOR_OFF;
        } else if (button.id == 203) {
            this.animeTypeIndex = (this.animeTypeIndex + 1) % AnimeType.values().length;
            this.buttonAnimeType.displayString = AnimeType.values()[this.animeTypeIndex].toString();
        }

        super.actionPerformed(button);
    }

    public void setFont(String name) {
        this.buttonFont.displayString = name;
    }

    /**
     * GUI閉じる前に
     */
    private void saveState() {
        if (this.currentTextObj != null) {
            this.state.setTTSetting(this.fieldStationSetting.getText());
        }

        if (this.selector.closeGui(this.state)) {
            RTMCore.NETWORK_WRAPPER.sendToServer(new PacketSelectResource(this.selector));
        }
    }

    private void updateText() {
        if (this.currentTextObj == null) {
            return;
        }

        String text = this.fieldText.getText();
        String font = this.buttonFont.displayString;
        //int style = Integer.valueOf(this.fieldStyle.getText());
        int color = Integer.decode(this.fieldColor.getText());
        this.currentTextObj.posU = Float.valueOf(this.fieldTextPosU.getText());
        this.currentTextObj.posV = Float.valueOf(this.fieldTextPosV.getText());
        this.currentTextObj.size = Float.valueOf(this.fieldTextSize.getText());
        this.currentTextObj.width = Float.valueOf(this.fieldTextWidth.getText());
        this.currentTextObj.animeType = AnimeType.values()[this.animeTypeIndex];//Integer.valueOf(this.fieldAnimeType.getText());
        this.currentTextObj.animeSpeed = Float.valueOf(this.fieldAnimeSpeed.getText());
        this.currentTextObj.setText(text, font, this.style, color, true);
    }

    private void openFontGui(String prevFont) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        int index = 0;
        for (int i = 0; i < fonts.length; ++i) {
            if (prevFont.equals(fonts[i].getFontName())) {
                index = i;
                break;
            }
        }
        JComboBox<Font> box = new JComboBox<>(fonts);
        box.setSelectedIndex(index);
        box.setRenderer(new ComboItemFont());

        this.openListGui("Select Font", box, (event) -> {
            String fontName = ((Font) box.getSelectedItem()).getName();
            GuiSignboard.this.setFont(fontName);
        });
    }

	/*private void openTTGui(String prevTT)
	{
		String[] names = TimeTableManager.INSTANCE.getNames();
		int index = 0;
		for(int i = 0; i < names.length; ++i)
		{
			if(names[i].equals(prevTT))
			{
				index = i;break;
			}
		}
		JComboBox<String> box = new JComboBox<>(names);
		box.setSelectedIndex(index);
		this.openListGui("Select TT", box, (event)->{
			this.state.setTimeTableName((String)box.getSelectedItem());
		});
	}*/

    private <E> void openListGui(String name, JComboBox<E> box, ActionListener ok) {
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(320, 180);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);

        JPanel panelSelectFont = new JPanel();
        panelSelectFont.add(box);

        JButton buttonOK = new JButton("OK");
        buttonOK.addActionListener((event) -> {
            ok.actionPerformed(event);
            frame.dispose();
        });
        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener((event) -> {
            frame.dispose();
        });
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.X_AXIS));
        panelButton.add(buttonOK);
        panelButton.add(buttonCancel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panelSelectFont);
        panel.add(panelButton);

        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }

    public class ComboItemFont extends JLabel implements ListCellRenderer {
        public ComboItemFont() {
            this.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Font font = (Font) value;
            this.setText(font.getName());
            this.setFont(new Font(font.getName(), font.getStyle(), 12));
            if (isSelected) {
                this.setForeground(Color.white);
                this.setBackground(Color.black);
            } else {
                this.setForeground(Color.black);
                this.setBackground(Color.white);
            }
            return this;
        }
    }
}