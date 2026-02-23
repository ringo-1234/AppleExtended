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

package jp.ngt.rtm.modelpack.init;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public final class MPLBarPanel extends JPanel
{
	private static final float FONT_SIZE = 12.0F;

	public MPLBarPanel(JLabel label, JProgressBar bar, int scale)
	{
		super();

		this.setBackground(Color.BLACK);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		int height = (MPLFrame.FRAME_HEIGHT - MPLAdButton.BUTTON_HEIGHT) / 2;
		this.setMaximumSize(new Dimension(MPLFrame.FRAME_WIDTH * scale, height * scale));
		this.add(label);
		this.add(bar);

		Color green = new Color(0x00FF02);
		int h2 = height / 2;

		label.setFont(label.getFont().deriveFont(FONT_SIZE * scale));
		label.setForeground(green);
		label.setPreferredSize(new Dimension(MPLAdButton.BUTTON_WIDTH * scale, h2 * scale));
		label.setAlignmentX(0.5F);//中央に表示

		bar.setFont(bar.getFont().deriveFont(FONT_SIZE * scale));
		bar.setForeground(green);
		bar.setBackground(Color.WHITE);
		bar.setUI(new BasicProgressBarUI() {//文字色
			@Override protected Color getSelectionForeground() {
				return Color.BLACK;
			}
			@Override protected Color getSelectionBackground() {
				return Color.BLACK;
			}
		});
		bar.setPreferredSize(new Dimension(MPLAdButton.BUTTON_WIDTH * scale, h2 * scale));
		bar.setAlignmentX(0.5F);//中央に表示
		bar.setValue(0);
	}
}
