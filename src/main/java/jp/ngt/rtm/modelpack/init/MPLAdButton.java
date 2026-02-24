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

package jp.ngt.rtm.modelpack.init;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;

import jp.ngt.ngtlib.io.NGTJson;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;

public final class MPLAdButton extends JButton
{
	public static final int BUTTON_WIDTH = 400;
	public static final int BUTTON_HEIGHT = 225;
	private static final int IMAGE_WIDTH = 1024;
	private static final int IMAGE_HEIGHT = 576;

	private Advertisement[] ads;
	private Icon[] images;
	private int index;

	public MPLAdButton(int scale)
	{
		super();
		//this.setMargin(new Insets(0, 0, 0, 0));
		//this.setBorderPainted(false);//枠非表示
		this.setBorder(new BevelBorder(BevelBorder.RAISED, Color.WHITE, Color.GRAY));

		this.initAd(scale);
	}

	private void initAd(int scale)
	{
		this.ads = this.getAds();
		if(this.ads == null)
		{
			;
		}
		else
		{
			this.shuffleAds();
			this.loadImages(scale);
			this.changeImage(0);

			this.addActionListener((event)->{this.onClick();});
		}
	}

	/**jsonから広告を取得*/
	private Advertisement[] getAds()
	{
		try
		{
			URL url = new URL(RTMCore.AD_URL);
	    	String json = NGTText.append(NGTText.readTextL(url.openStream(), ""), true);
			return NGTJson.getObjectFromJson(json, Advertisement[].class);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**広告をランダムに並べ直す*/
	private void shuffleAds()
	{
		List<Advertisement> list = new ArrayList<>();
		NGTUtil.addArray(list, this.ads);
		Collections.shuffle(list);
		this.ads = list.toArray(new Advertisement[list.size()]);
	}

	private void loadImages(int scale)
	{
		this.images = new Icon[this.ads.length];
		for(int i = 0; i < this.ads.length; ++i)//画像結合して左右アニメーションしたい
		{
			BufferedImage image;
			try
			{
				//image = ImageIO.read(new URL(this.ads[i].picture));
				URLConnection urlc = (new URL(this.ads[i].picture)).openConnection();
				if(urlc instanceof HttpsURLConnection)//SSLHandshakeException防止
				{
					X509TrustManager x509mng = new X509TrustManager(){
					    @Override
					    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException{}

					    @Override
					    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{}

					    @Override
					    public X509Certificate[] getAcceptedIssuers(){return null;}
					};
					SSLContext sslContext = SSLContext.getInstance("SSL");
			        sslContext.init(null, new X509TrustManager[]{x509mng}, new SecureRandom());
			        ((HttpsURLConnection)urlc).setSSLSocketFactory(sslContext.getSocketFactory());
				}

				InputStream istream = urlc.getInputStream();
				ImageInputStream stream = ImageIO.createImageInputStream(istream);
		        BufferedImage bi;
		        try
		        {
		            bi = ImageIO.read(stream);
		            if(bi == null){stream.close();}
		        }
		        finally
		        {
		            istream.close();
		        }
				image = bi;

			}
			catch (IOException | KeyManagementException | NoSuchAlgorithmException e)
			{
				e.printStackTrace();
				image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				NGTLog.debug("[ModelPack] Failed to load image : " + this.ads[i].picture);
			}

			Image img2 = image.getScaledInstance(BUTTON_WIDTH * scale, BUTTON_HEIGHT * scale, Image.SCALE_FAST);
			this.images[i] = new ImageIcon(img2);
		}
	}

	/**広告画像を変更*/
	public void changeImage(int move)
	{
		if(this.ads == null){return;}

		this.index += move;
		if(this.index < 0)
		{
			this.index += this.ads.length;
		}
		else if(this.index >= this.ads.length)
		{
			this.index -= this.ads.length;
		}
		this.setIcon(this.images[this.index]);
	}

	private void onClick()
	{
		if(this.ads == null){return;}

		try
		{
			URI uri = new URI(this.ads[this.index].url);
			Desktop.getDesktop().browse(uri);
		}
		catch(URISyntaxException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
