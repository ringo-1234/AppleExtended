package jp.ngt.rtm.modelpack.init;

import javax.swing.JFrame;

import org.lwjgl.opengl.Display;

public final class MPLFrame extends JFrame
{
	public static final int FRAME_WIDTH = 480;
	public static final int FRAME_HEIGHT = 360;

	public MPLFrame(int scale)
	{
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
