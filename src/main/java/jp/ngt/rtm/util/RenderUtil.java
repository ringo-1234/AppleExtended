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

package jp.ngt.rtm.util;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

@SideOnly(Side.CLIENT)
public final class RenderUtil {
    private static FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static float[] COLOR_BUF = {0.0F, 0.0F, 0.0F};

    /**
     * id:0~3
     * r:-1でランダム
     */
    public static void enableCustomLighting(int id, float x, float y, float z, float r, float g, float b) {
        int light = getLight(id);
        if (light < 0) {
            return;
        }

        GL11.glDisable(GL11.GL_LIGHT0);//上からの光
        GL11.glDisable(GL11.GL_LIGHT1);
        //NGTUtilClient.getMinecraft().entityRenderer.disableLightmap(0.0D);

        if (r < 0.0F) {
            float hue = (float) (((int) ((double) ((System.currentTimeMillis() / 50) % 24000) * 15.0D)) % 360) / 360.0F;
            float[] buf = convertHueToRGB(hue);
            r = buf[0];
            g = buf[1];
            b = buf[2];
        }

        GL11.glEnable(light);
        //方向
        //GL11.glLight(light, GL11.GL_SPOT_DIRECTION, setColorBuffer(0.0F, -1.0F, 0.0F, 1.0F));
        //位置
        GL11.glLight(light, GL11.GL_POSITION, setColorBuffer(x, y, z, 1.0F));

        //反射光、光源からの光
        float spe = 1.0F;//def 1.0
        GL11.glLight(light, GL11.GL_SPECULAR, setColorBuffer(r, g, b, 1.0F));
        //拡散光、光源からの光の乱反射(反射光より暗く)
        float dif = 0.7F;//def 1.0
        GL11.glLight(light, GL11.GL_DIFFUSE, setColorBuffer(dif, dif, dif, 1.0F));
        //環境光、周辺からの光(拡散光より暗く)
        float amb = 0.2F;//def 0.0
        GL11.glLight(light, GL11.GL_AMBIENT, setColorBuffer(amb, amb, amb, 1.0F));

        //一定減衰率
        //GL11.glLightf(GL11.GL_LIGHT3, GL11.GL_CONSTANT_ATTENUATION, 1.0F);
        //線形減衰率
        //GL11.glLightf(GL11.GL_LIGHT3, GL11.GL_LINEAR_ATTENUATION, 0.05F);
        //2次減衰率
        //GL11.glLightf(GL11.GL_LIGHT3, GL11.GL_QUADRATIC_ATTENUATION, 0.05F);

        //GL11.GL_FRONT_AND_BACK

        //環境光、全体 def 0.2, 1.0
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, setColorBuffer(0.24725F, 0.1995F, 0.0745F, 1.0F));
        //拡散光、明るい部分 def 0.8, 1.0
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, setColorBuffer(0.75164F, 0.60648F, 0.22648F, 1.0F));
        //鏡面光、ハイライト def 0.0, 1.0
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, setColorBuffer(0.628281F, 0.555802F, 0.366065F, 1.0F));
        //鏡面指数 def 0 (0~128)
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 51.2F);
        //放射光 def 0.0, 1.0
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
    }

    public static void disableCustomLighting(int id) {
        int light = getLight(id);
        if (light < 0) {
            return;
        }
        GL11.glDisable(light);

        //NGTUtilClient.getMinecraft().entityRenderer.enableLightmap(0.0D);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        //RenderHelper.enableStandardItemLighting();
    }

    private static int getLight(int id) {
        switch (id) {
            case 0:
                return GL11.GL_LIGHT4;
            case 1:
                return GL11.GL_LIGHT5;
            case 2:
                return GL11.GL_LIGHT6;
            case 3:
                return GL11.GL_LIGHT7;
        }
        return -1;
    }

    private static FloatBuffer setColorBuffer(float x, float y, float z, float w) {
        COLOR_BUFFER.clear();
        COLOR_BUFFER.put(x).put(y).put(z).put(w);
        COLOR_BUFFER.flip();
        return COLOR_BUFFER;
    }

    /**
     * @param hue 0.0~1.0
     *
     */
    public static float[] convertHueToRGB(float hue) {
        int huei = (int) (hue * 360.0F);
        float r = 0.0F;
        float g = 0.0F;
        float b = 0.0F;
        switch (huei / 60) {
            case 0:
                r = 1.0F;
                g = ((float) (huei) / 60.0F);
                b = 0.0F;
                break;
            case 1:
                r = ((float) (120 - huei) / 60.0F);
                g = 1.0F;
                b = 0.0F;
                break;
            case 2:
                r = 0.0F;
                g = 1.0F;
                b = ((float) (120 - huei) / 60.0F);
                break;
            case 3:
                r = 0.0F;
                g = ((float) (240 - huei) / 60.0F);
                b = 1.0F;
                break;
            case 4:
                r = ((float) (huei - 240) / 60.0F);
                g = 0.0F;
                b = 1.0F;
                break;
            case 5:
                r = 1.0F;
                g = 0.0F;
                b = ((float) (360 - huei) / 60.0F);
                break;
        }
        COLOR_BUF[0] = r;
        COLOR_BUF[1] = g;
        COLOR_BUF[2] = b;
        return COLOR_BUF;
    }
}