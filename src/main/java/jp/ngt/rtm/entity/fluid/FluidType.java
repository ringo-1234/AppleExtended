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

package jp.ngt.rtm.entity.fluid;

import jp.ngt.ngtlib.util.ColorUtil;

public enum FluidType {
    PIG_IRON(0xFFFB4C, 0xE63300, 0xBEB3B0, 0x000000, 1200.0F, 0.15F, 0.01F, -1, Type.LIQUID),
    STEEL(0xFFFD4C, 0xE63300, 0x9090A0, 0x000000, 1500.0F, 0.15F, 0.01F, -1, Type.LIQUID),
    COKE(0xD85050, 0x000000, 0x505050, 0x000000, 1000.0F, 0.01F, 0.005F, 6000, Type.SOLID),
    SLAG(0xFFFB4C, 0xE63300, 0x878090, 0x000000, 1000.0F, 0.2F, 0.015F, -1, Type.LIQUID),
    IRON_ORE(0xFFFB4C, 0xE63300, 0xA0A0A0, 0x000000, 1200.0F, 0.01F, 0.005F, -1, Type.SOLID),
    //FURNACE_FIRE(0xAA00FF, 0xAA00FF, 0xAA00FF, 0xAA00FF, true),
    //EXHAUST_GAS(0x19C94C, 0xE63300, true),
    ;

    private final int topHotColor;
    private final int bottomHotColor;
    private final int topColdColor;
    private final int bottomColdColor;
    /**
     * 融点
     */
    public final float meltingPoint;
    /**
     * 粘度
     */
    public final float viscosity;
    /**
     * 熱伝導率(0.0~0.5)
     */
    public final float thermalConductivity;
    /**
     * 寿命
     */
    public final int life;
    public final Type type;

    private FluidType(int color1, int color2, int color3, int color4, float f1, float f2, float f3, int i1, Type t1) {
        this.topHotColor = color1;
        this.bottomHotColor = color2;
        this.topColdColor = color3;
        this.bottomColdColor = color4;
        this.meltingPoint = f1;
        this.viscosity = f2;
        this.thermalConductivity = f3 * 0.5F;
        this.life = i1;
        this.type = t1;
    }

    public int getColor(float gradation, float normalizedTemp) {
        int r = this.calcColorComponent(
                ColorUtil.getR(this.topHotColor), ColorUtil.getR(this.bottomHotColor),
                ColorUtil.getR(this.topColdColor), ColorUtil.getR(this.bottomColdColor),
                gradation, normalizedTemp, true);
        int g = this.calcColorComponent(
                ColorUtil.getG(this.topHotColor), ColorUtil.getG(this.bottomHotColor),
                ColorUtil.getG(this.topColdColor), ColorUtil.getG(this.bottomColdColor),
                gradation, normalizedTemp, true);
        int b = this.calcColorComponent(
                ColorUtil.getB(this.topHotColor), ColorUtil.getB(this.bottomHotColor),
                ColorUtil.getB(this.topColdColor), ColorUtil.getB(this.bottomColdColor),
                gradation, normalizedTemp, true);
        return ColorUtil.encode(r, g, b);
    }

    private int calcColorComponent(int topHot, int bottomHot, int topCold, int bottomCold, float gradation, float normalizedTemp, boolean useTemp) {
        float top = topHot;
        float bottom = bottomHot;
        if (useTemp) {
            top = ((topHot - topCold) * normalizedTemp) + topCold;
            bottom = ((bottomHot - bottomCold) * normalizedTemp) + bottomCold;
        }
        int color = (int) (((top - bottom) * gradation) + bottom);
        return color < 0 ? 0 : (color > 255 ? 255 : color);
    }

    public enum Type {
        GAS,
        LIQUID,
        SOLID;
    }

    /*
     * メモ
     * 鉄鉱石+コークス→(高炉)→銑鉄
     * 銑鉄+O2→(転炉、反射炉)→鋼鉄
     * */
}
