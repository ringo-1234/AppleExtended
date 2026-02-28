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

package jp.ngt.ngtlib.renderer;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.renderer.media.ElectricBulletinBoard;
import jp.ngt.ngtlib.renderer.media.ImageBase;
import jp.ngt.ngtlib.renderer.media.MediaBase;
import jp.ngt.ngtlib.renderer.media.MediaBase.MediaType;
import jp.ngt.ngtlib.renderer.model.ModelLoader;
import jp.ngt.ngtlib.renderer.model.PolygonModel;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public final class NGTRenderer {
    private static final PolygonModel FIRE = ModelLoader.loadModel(new ResourceLocation("ngtlib", "models/fire.mqo"), VecAccuracy.MEDIUM);
    private static final float FIRE_LEN_DIV = 1.0F / 4.1375F;

    public static void renderPole(NGTTessellator tessellator, float r, float length, boolean useTexture) {
        float minU;
        float maxU;
        float minV;
        float maxV;
        float[][] sp = ModelSolid.SPHERE;
        int l0 = (int) (length * 16.0D);

        for (int l = 0; l < l0; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                minU = (float) i1 * 0.0625F;
                maxU = (float) (i1 + 1) * 0.0625F;
                minV = (float) l * 0.0625F;
                maxV = (float) (l + 1) * 0.0625F;
                float l1 = l * 0.0625F;

                int ii0 = 64 + i1;
                int ii1 = 64 + i1;
                int ii2 = 64 + (i1 + 1) % 16;
                int ii3 = 64 + (i1 + 1) % 16;

                addVertex(tessellator, useTexture, sp[ii0][0] * r, sp[ii0][1] * r + l1, sp[ii0][2] * r, maxU, maxV);
                addVertex(tessellator, useTexture, sp[ii1][0] * r, sp[ii1][1] * r + 0.0625F + l1, sp[ii1][2] * r, maxU, minV);
                addVertex(tessellator, useTexture, sp[ii2][0] * r, sp[ii2][1] * r + 0.0625F + l1, sp[ii2][2] * r, minU, minV);
                addVertex(tessellator, useTexture, sp[ii3][0] * r, sp[ii3][1] * r + l1, sp[ii3][2] * r, minU, maxV);

                addVertex(tessellator, useTexture, sp[ii3][0] * r, sp[ii3][1] * r + l1, sp[ii3][2] * r, maxU, maxV);
                addVertex(tessellator, useTexture, sp[ii2][0] * r, sp[ii2][1] * r + 0.0625F + l1, sp[ii2][2] * r, maxU, minV);
                addVertex(tessellator, useTexture, sp[ii1][0] * r, sp[ii1][1] * r + 0.0625F + l1, sp[ii1][2] * r, minU, minV);
                addVertex(tessellator, useTexture, sp[ii0][0] * r, sp[ii0][1] * r + l1, sp[ii0][2] * r, minU, maxV);
            }
        }
    }

    private static void addVertex(NGTTessellator tessellator, boolean b, float x, float y, float z, float u, float v) {
        if (b) {
            tessellator.addVertexWithUV(x, y, z, u, v);
        } else {
            tessellator.addVertex(x, y, z);
        }
    }

    public static void renderSphere(NGTTessellator tessellator, float r) {
        float[][] sp = ModelSolid.SPHERE;
        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        for (int i0 = 0; i0 < 8; ++i0)//roZ
        {
            for (int i1 = 0; i1 < 16; ++i1)//roY
            {
                minU = (float) i1 * 0.0625F;
                maxU = (float) (i1 + 1) * 0.0625F;
                minV = (float) i0 * 0.125F;
                maxV = (float) (i0 + 1) * 0.125F;

                int ii0 = i0 * 16 + i1;
                int ii1 = (i0 + 1) * 16 + i1;
                int ii2 = (i0 + 1) * 16 + (i1 + 1) % 16;
                int ii3 = i0 * 16 + (i1 + 1) % 16;

                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV(sp[ii0][0] * r, sp[ii0][1] * r, sp[ii0][2] * r, maxU, maxV);
                tessellator.addVertexWithUV(sp[ii1][0] * r, sp[ii1][1] * r, sp[ii1][2] * r, maxU, minV);
                tessellator.addVertexWithUV(sp[ii2][0] * r, sp[ii2][1] * r, sp[ii2][2] * r, minU, minV);
                tessellator.addVertexWithUV(sp[ii3][0] * r, sp[ii3][1] * r, sp[ii3][2] * r, minU, maxV);

                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV(sp[ii3][0] * r, sp[ii3][1] * r, sp[ii3][2] * r, maxU, maxV);
                tessellator.addVertexWithUV(sp[ii2][0] * r, sp[ii2][1] * r, sp[ii2][2] * r, maxU, minV);
                tessellator.addVertexWithUV(sp[ii1][0] * r, sp[ii1][1] * r, sp[ii1][2] * r, minU, minV);
                tessellator.addVertexWithUV(sp[ii0][0] * r, sp[ii0][1] * r, sp[ii0][2] * r, minU, maxV);
            }
        }
    }

    public static void renderFrame(float minX, float minY, float minZ, float width, float height, float depth, int color, int alpha) {
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorRGBA_I(color, alpha);

        addFrame(tessellator, minX, minY, minZ, width, height, depth);

        tessellator.draw();
    }

    public static void addFrame(NGTTessellator tessellator, float minX, float minY, float minZ, float width, float height, float depth) {
        float maxX = minX + width;
        float maxY = minY + height;
        float maxZ = minZ + depth;

        tessellator.addVertex(minX, minY, minZ);//minY
        tessellator.addVertex(maxX, minY, minZ);

        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);

        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, minY, maxZ);

        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);

        tessellator.addVertex(minX, minY, minZ);//tate
        tessellator.addVertex(minX, maxY, minZ);

        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);

        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);

        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);

        tessellator.addVertex(minX, maxY, minZ);//maxY
        tessellator.addVertex(maxX, maxY, minZ);

        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);

        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(minX, maxY, maxZ);

        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);
    }

    public static void renderFire(float scaleXY, float scaleZ, int color, int rep) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GLHelper.disableLighting();
        GLHelper.setLightmapMaxBrightness();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        scaleZ *= FIRE_LEN_DIV;//デフォ長さで割る
        float alpha = 0.125F;
        float colorR = ((color >> 16) & 0xFF) / 255.0F;
        float colorG = ((color >> 8) & 0xFF) / 255.0F;
        float colorB = ((color) & 0xFF) / 255.0F;

        NGTTessellator tessellator = NGTTessellator.instance;
        for (int i = 0; i < rep; ++i) {
            GL11.glPushMatrix();
            float f0 = (float) i / (float) rep;//0.0~n-1/n
            float scres = 1.0F - f0;
            GL11.glScalef(scres * scaleXY, scres * scaleXY, scres * scaleZ);
            tessellator.startDrawing(GL11.GL_TRIANGLES);
            float r2 = (1.0F - colorR) * f0 + colorR;
            float g2 = (1.0F - colorG) * f0 + colorG;
            float b2 = (1.0F - colorB) * f0 + colorB;
            tessellator.setColorRGBA_F(r2, g2, b2, alpha);
            FIRE.tessellateAll(tessellator, false);
            tessellator.draw();
            GL11.glPopMatrix();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GLHelper.enableLighting();

        GL11.glPopMatrix();
    }

    private static boolean HAS_CAMERA_LIB = true;
    private static boolean HAS_TWITTER_LIB = true;

    public static void renderWebCamera(float width, float height, boolean fitAspectRatio, int cameraId) {
        if (HAS_CAMERA_LIB) {
            try {
                MediaBase media = MediaBase.getMedia(MediaType.CAMERA, String.valueOf(cameraId));
                if (media != null) {
                    media.render(width, height, fitAspectRatio);
                }
            } catch (Exception e) {
                e.printStackTrace();
                HAS_CAMERA_LIB = false;
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
                HAS_CAMERA_LIB = false;
                showLibInstallertion();
            }
        } else {
            ;
        }
    }

    public static void renderDesktop(float width, float height, boolean fitAspectRatio) {
        MediaBase media = MediaBase.getMedia(MediaType.CAPTURE, "default");
        if (media != null) {
            media.render(width, height, fitAspectRatio);
        }
    }

    public static void renderPicture(float width, float height, boolean fitAspectRatio, String url) {
        renderPicture(width, height, fitAspectRatio, url, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    public static void renderPicture(float width, float height, boolean fitAspectRatio, String url, float minU, float minV, float maxU, float maxV) {
        MediaBase media = MediaBase.getMedia(MediaType.PICTURE, url);
        if (media != null) {
            ((ImageBase) media).setUV(minU, minV, maxU, maxV);
            media.render(width, height, fitAspectRatio);
        }
    }

    public static void renderMap(float width, float height, boolean fitAspectRatio, String pos) {
        MediaBase media = MediaBase.getMedia(MediaType.MAP, pos);
        if (media != null) {
            media.render(width, height, fitAspectRatio);
        }
    }

    public static void renderTweet(float width, float height, boolean fitAspectRatio, String keyword) {
        if (HAS_TWITTER_LIB) {
            try {
                MediaBase media = MediaBase.getMedia(MediaType.TWEET, keyword);
                if (media != null) {
                    media.render(width, height, fitAspectRatio);
                }
            } catch (Exception e) {
                e.printStackTrace();
                HAS_TWITTER_LIB = false;
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
                HAS_TWITTER_LIB = false;
                showLibInstallertion();
            }
        } else {
            ;
        }
    }

    public static void renderEBB(float width, float height, boolean fitAspectRatio, String name, int resolution, int offsetU, int offsetV) {
        MediaBase media = MediaBase.getMedia(MediaType.EBB, name);
        if (media != null) {
            ((ElectricBulletinBoard) media).setParameter(resolution, offsetU, offsetV);
            media.render(width, height, fitAspectRatio);
        }
    }

    private static void showLibInstallertion() {
        ITextComponent component = new TextComponentTranslation(TextFormatting.RED + "Please install library. ->");
        ITextComponent component2 = new TextComponentTranslation("  §6§nDownload here", new Object[0]);
        component2.setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, NGTCore.LIB_INSTALLERTION_URL)));
        component.appendSibling(component2);
        NGTLog.showChatMessage(component);
    }
}