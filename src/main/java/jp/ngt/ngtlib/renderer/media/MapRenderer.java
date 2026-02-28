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

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.block.material.MapColor;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class MapRenderer extends ImageBase {
    private MapData mapData;

    public static MapRenderer create(String pos) {
        String[] sa = pos.split(",");
        int x = Integer.valueOf(sa[0]);
        int y = Integer.valueOf(sa[1]);
        int z = Integer.valueOf(sa[2]);
        return new MapRenderer(x, y, z);
    }

    private MapRenderer(int x, int y, int z) {
        String name = "ngt_mapdata_" + System.currentTimeMillis();
        this.mapData = new MapData(name);
        this.mapData.calculateMapCenter(x, z, 3);
        this.mapData.dimension = NGTUtil.getClientWorld().provider.getDimension();
        this.mapData.trackingPosition = false;
        this.mapData.unlimitedTracking = false;

        //MapInfo mapinfo = this.mapData.getMapInfo(NGTUtilClient.getMinecraft().player);

        int depth = 0;//0~2
        byte color = (byte) (MapColor.WATER.colorIndex * 4 + depth);
        Arrays.fill(this.mapData.colors, color);
        NGTUtilClient.getMinecraft().entityRenderer.getMapItemRenderer().updateMapTexture(this.mapData);
    }

    @Override
    public void render(float width, float height, boolean fitAspectRatio) {
        float scale = 1.0F / 64.0F;
        GL11.glTranslatef(0.5F, -0.5F, 0.0625F);
        GL11.glScalef(-scale, scale, scale);
        NGTUtilClient.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(this.mapData, false);//falseでマップ上アイコン描画
    }

    @Override
    public BufferedImage getImage() {
        return null;
    }

    @Override
    public int getTextureId() {
        return 0;
    }
}