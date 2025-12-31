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
