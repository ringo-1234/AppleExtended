package jp.ngt.ngtlib.renderer.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureUtil;

public class Picture extends ImageBase
{
	protected final String url;
	protected BufferedImage image;
	protected int textureId = -1;

	protected Picture(String par1)
	{
		this.url = par1;
	}

	public static Picture create(String par1)
	{
		if(par1.isEmpty()){return null;}

		if(par1.endsWith(".gif"))
		{
			return new AnimatedPicture(par1);
		}
		return new Picture(par1);
	}

	protected void loadImage()
	{
		try
		{
			this.image = ImageIO.read(new URL(this.url));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		this.textureId = TextureUtil.glGenTextures();
		this.uploadTexture(this.textureId, this.image);
	}

	@Override
	public BufferedImage getImage()
	{
		if(this.textureId < 0)
		{
			this.loadImage();
		}
		return this.image;
	}

	@Override
	public int getTextureId()
	{
		return this.textureId;
	}
}