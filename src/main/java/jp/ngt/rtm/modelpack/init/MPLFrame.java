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

import org.lwjgl.opengl.Display;

import javax.swing.*;

public final class MPLFrame extends JFrame {
    public static final int FRAME_WIDTH = 480;
    public static final int FRAME_HEIGHT = 360;

    public MPLFrame(int scale) {
        super("RealTrainMod");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(FRAME_WIDTH * scale, FRAME_HEIGHT * scale);
        int winX = Display.getX();
        int winY = Display.getY();
        this.setLocation(winX, winY);
        //this.setLocationByPlatform(true);
        this.setResizable(false);
        //this.setUndecorated(true);
        //this.setAlwaysOnTop(true);//最前面に表示
    }
}
