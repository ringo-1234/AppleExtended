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

package jp.ngt.ngtlib.renderer.model;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.IRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class GroupObject {
    public String name;
    public byte drawMode;
    public float smoothingAngle;
    public ArrayList<Face> faces = new ArrayList<>(16);

    public GroupObject(String par1, int par2) {
        this.name = par1;
        this.drawMode = (byte) par2;
    }

    public void calcVertexNormals(VecAccuracy accuracy) {
        //頂点を共有している面のリストを格納
        Map<Vertex, List<Face>> faceMap = new HashMap<>(this.faces.size() * 4);

        for (Face face : this.faces) {
            if (face.faceNormal == null) {
                face.calculateFaceNormal(accuracy);
            }

            for (int i = 0; i < face.vertices.length; ++i) {
                if (!((i == 0) || (i == 1) || (i % 3 == 2))) {
                    continue;//重複する頂点はパス
                }

                Vertex vtx = face.vertices[i];
                List<Face> list = faceMap.get(vtx);
                if (list == null) {
                    list = new ArrayList<>();
                    faceMap.put(vtx, list);
                }

                if (!list.contains(face)) {
                    list.add(face);
                }
            }
        }

        float angleCos = NGTMath.cos(this.smoothingAngle);//精度問題なし
        for (Face face : this.faces) {
            face.calcVertexNormals(faceMap, angleCos, accuracy);
        }
    }

    public void render(boolean smoothing) {
        if (!this.faces.isEmpty()) {
            NGTTessellator tessellator = NGTTessellator.instance;
            tessellator.startDrawing(this.drawMode);
            this.render(tessellator, smoothing);
            tessellator.draw();
        }
    }

    public void render(IRenderer tessellator, boolean smoothing) {
        if (!this.faces.isEmpty()) {
            for (Face face : faces) {
                face.addFaceForRender(tessellator, smoothing);
            }
        }
    }

    /**
     * DeepCopy, アウトラインモデル用
     *
     */
    public GroupObject copy(String name) {
        GroupObject go = new GroupObject(name, this.drawMode);
        for (Face origFace : this.faces) {
            Face face = origFace.copy();
            go.faces.add(face);
        }
        return go;
    }

    protected final class FaceSet {
        public final Face face;
        public final int index;

        public FaceSet(Face p1, int p2) {
            this.face = p1;
            this.index = p2;
        }
    }
}