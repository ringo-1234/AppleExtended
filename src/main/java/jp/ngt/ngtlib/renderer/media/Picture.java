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

import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Picture extends ImageBase {
    protected final String url;
    protected BufferedImage image;
    protected int textureId = -1;

    protected Picture(String par1) {
        this.url = par1;
    }

    public static Picture create(String par1) {
        if (par1.isEmpty()) {
            return null;
        }

        if (par1.endsWith(".gif")) {
            return new AnimatedPicture(par1);
        }
        return new Picture(par1);
    }

    protected void loadImage() {
        try {
            this.image = ImageIO.read(new URL(this.url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.textureId = TextureUtil.glGenTextures();
        this.uploadTexture(this.textureId, this.image);
    }

    @Override
    public BufferedImage getImage() {
        if (this.textureId < 0) {
            this.loadImage();
        }
        return this.image;
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }
}