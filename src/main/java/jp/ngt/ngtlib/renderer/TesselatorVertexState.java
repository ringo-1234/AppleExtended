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

@SideOnly(Side.CLIENT)
public class TesselatorVertexState {
    private int[] rawBuffer;
    private int rawBufferIndex;
    private int vertexCount;
    private boolean hasTexture;
    private boolean hasBrightness;
    private boolean hasNormals;
    private boolean hasColor;

    public TesselatorVertexState(int[] buf, int bufI, int count, boolean hasTex, boolean hasBri, boolean hasNor, boolean hasCol) {
        this.rawBuffer = buf;
        this.rawBufferIndex = bufI;
        this.vertexCount = count;
        this.hasTexture = hasTex;
        this.hasBrightness = hasBri;
        this.hasNormals = hasNor;
        this.hasColor = hasCol;
    }

    public int[] getRawBuffer() {
        return this.rawBuffer;
    }

    public int getRawBufferIndex() {
        return this.rawBufferIndex;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public boolean getHasTexture() {
        return this.hasTexture;
    }

    public boolean getHasBrightness() {
        return this.hasBrightness;
    }

    public boolean getHasNormals() {
        return this.hasNormals;
    }

    public boolean getHasColor() {
        return this.hasColor;
    }
}