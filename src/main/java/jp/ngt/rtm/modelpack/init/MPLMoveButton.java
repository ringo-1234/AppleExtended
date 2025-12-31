package jp.ngt.rtm.modelpack.init;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;

public final class MPLMoveButton extends JButton
{
	private static final float FONT_SIZE = 36.0F;

	/**
	 * @param adButton
	 * @param mode 1 or -1
	 * @param scale
	 * */
	public MPLMoveButton(MPLAdButton adButton, int mode, int scale)
	{
		super();
		this.setFont(this.getFont().deriveFont(FONT_SIZE * scale));
		int width = (MPLFrame.FRAME_WIDTH - MPLAdButton.BUTTON_WIDTH) / 2;
        //setPreferredSizeはサイズ反映されない
        this.setMaximumSize(new Dimension(width * scale, MPLAdButton.BUTTON_HEIGHT * scale));
        //this.setBorderPainted(false);//枠非表示
        this.setBorder(new BevelBorder(BevelBorder.RAISED, Color.WHITE, Color.GRAY));
        this.setForeground(Color.ORANGE);
        this.setBackground(Color.BLACK);

        String text = mode == 1 ? "▶" : (mode == -1 ? "◀" : "?");
        this.setText(text);

        this.addActionListener((event)->{
        	adButton.changeImage(-1);
        });
	}
}
