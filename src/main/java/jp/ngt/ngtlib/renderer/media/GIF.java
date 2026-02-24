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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.ResourceLocationCustom;

public class GIF
{
	protected final List<BufferedImage> images = new ArrayList<>();

	protected int width, height;
	protected int frameNum;
	/**frame/msec*/
	protected int frameRate;

	private GIF(){}

	/**
	 * @param source URL(http~) or ResourceLocation
	 * */
	public static GIF load(String source)
	{
		InputStream stream = null;
		if(source.startsWith("http"))
		{
			try
			{
				stream = new URL(source).openStream();
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			try
			{
				stream = NGTFileLoader.getInputStream(new ResourceLocationCustom(source));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return load(stream);
	}

	public static GIF load(InputStream stream)
	{
		List<BufferedImage> tempList = new ArrayList<>();

		ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		if(reader == null){return null;}

		try
		{
	        reader.setInput(ImageIO.createImageInputStream(stream));

	        int count = reader.getNumImages(true);
	        for(int i = 0; i < count ; i++)
	        {
	        	tempList.add(reader.read(i));
	        }
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return null;
		}

		if(tempList == null || tempList.isEmpty())
		{
			return null;
		}

		GIF gif = new GIF();

		//背景と合成
		BufferedImage firstImg = tempList.get(0);
		int width = firstImg.getWidth();
		int height = firstImg.getHeight();
		BufferedImage imgTemp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = imgTemp.getGraphics();
		for(int i = 0; i < tempList.size(); ++i)
		{
			graphics.drawImage(tempList.get(i), 0, 0, null);

			BufferedImage currentImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			currentImg.setData(imgTemp.getData());
			gif.images.add(currentImg);
		}

		int delayTime = 500;

		try
		{
			IIOMetadata imageMetaData =  reader.getImageMetadata(0);
			String metaFormatName = imageMetaData.getNativeMetadataFormatName();
	        IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);
			IIOMetadataNode gceNode = getNode(root, "GraphicControlExtension");
			delayTime = Integer.valueOf(gceNode.getAttribute("delayTime"));//1/100sec
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		gif.width = width;
		gif.height = height;
		gif.frameNum = gif.images.size();
		gif.frameRate = delayTime * 10;

		//結合処理
		/*this.image = new BufferedImage(this.width, this.height * this.frameNum, BufferedImage.TYPE_4BYTE_ABGR);
		for(int i = 0; i < this.frameNum; ++i)
		{
			Graphics g = this.image.getGraphics();
			g.drawImage(images.get(i), 0, i * this.height, null);
		}*/

		return gif;
	}

	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName)
	{
		int nNodes = rootNode.getLength();
		for(int i = 0; i < nNodes; ++i)
		{
			if(rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0)
			{
				return((IIOMetadataNode)rootNode.item(i));
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return node;
	}

	public BufferedImage getImage(int index)
	{
		return this.images.get(index);
	}

	private static final long INTERVAL = 1000 * 60 * 60;

	public int getCurrentFrameIndex()
	{
		long t = System.currentTimeMillis() % INTERVAL;
		return ((int)t / this.frameRate) % this.frameNum;
	}
}
