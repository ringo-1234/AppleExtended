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

package jp.ngt.rtm.modelpack.mng;

import javax.swing.JFrame;

import org.lwjgl.opengl.Display;

public class ModelPackMngGUI extends JFrame
{
	public ModelPackMngGUI()
	{
		super("RTM ModelPack Manager");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    //this.setSize(FRAME_WIDTH * scale, FRAME_HEIGHT * scale);
	    int winX = Display.getX();
	    int winY = Display.getY();
	    this.setLocation(winX, winY);
	    //this.setLocationByPlatform(true);
	    this.setResizable(true);
	}
}
