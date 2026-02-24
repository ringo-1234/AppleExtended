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

package jp.ngt.ngtlib.renderer.media;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.texture.TextureUtil;

public class ScreenCapture extends ImageBase
{
	private final Robot robot;
	private final Dimension screenSize;
	private int texId = -1;

	private ScreenCapture() throws AWTException
	{
		this.robot = new Robot();
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.texId = TextureUtil.glGenTextures();
	}

	public static ScreenCapture create()
	{
		try
		{
			ScreenCapture capture = new ScreenCapture();
			return capture;
		}
		catch(AWTException e)
		{
			return null;
		}
	}

	/*public BufferedImage getImage(int startX, int startY, int endX, int endY)
	{
		BufferedImage image = this.robot.createScreenCapture(new Rectangle(startX, startY, endX, endY));
		TextureUtil.uploadTextureImage(this.texId, image);
        return image;
	}*/

	public int getWidth()
	{
		return this.screenSize.width;
	}

	public int getHeight()
	{
		return this.screenSize.height;
	}

	@Override
	public BufferedImage getImage()
	{
		BufferedImage image = this.robot.createScreenCapture(new Rectangle(0, 0, this.screenSize.width, this.screenSize.height));
		this.uploadTexture(this.texId, image);
        return image;
	}

	@Override
	public int getTextureId()
	{
		return this.texId;
	}
}