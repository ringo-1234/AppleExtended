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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * VBOを使わないTessellator<br>
 * ディスプレイリストとの併用推奨
 */
@SideOnly(Side.CLIENT)
public final class PolygonRenderer implements IRenderer {
    public static final PolygonRenderer INSTANCE = new PolygonRenderer();
    public static final float DIV_15 = 1.0F / 15.0F;

    private PolygonRenderer() {
    }

    @Override
    public void startDrawing(int par1) {
        GL11.glBegin(par1);
    }

    @Override
    public int draw() {
        GL11.glEnd();
        return 0;
    }

    @Override
    public void addVertexWithUV(float x, float y, float z, float u, float v) {
        GL11.glTexCoord2f(u, v);
        GL11.glVertex3f(x, y, z);
    }

    @Override
    public void setNormal(float x, float y, float z) {
        GL11.glNormal3f(x, y, z);
    }

    @Override
    public void setBrightness(int par1) {
        GLHelper.setBrightness(par1);
    }

    @Override
    public void setColor(int r, int g, int b, int a) {
        GL11.glColor4b((byte) r, (byte) g, (byte) b, (byte) a);
    }
}