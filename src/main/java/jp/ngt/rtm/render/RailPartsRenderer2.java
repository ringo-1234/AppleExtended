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

package jp.ngt.rtm.render;

import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.model.Face;
import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.ngtlib.renderer.model.TextureCoordinate;
import jp.ngt.ngtlib.renderer.model.Vertex;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * VBO版
 */
@Deprecated
@SideOnly(Side.CLIENT)
public class RailPartsRenderer2 extends RailPartsRendererBase {
    public RailPartsRenderer2(String... par1) {
        super(par1);
    }

    /**
     * RenderLargeRailから呼ばれる
     */
    @Override
    public void renderRail(TileEntityLargeRailCore tileEntity, int index, double par2, double par4, double par6, float par8) {
        this.currentRailIndex = index;
        this.renderRailStatic(tileEntity, par2, par4, par6, par8);
        this.renderRailDynamic(tileEntity, par2, par4, par6, par8);
    }

    @Override
    public void renderStaticParts(TileEntityLargeRailCore rail, double par2, double par4, double par6) {
        if (rail.glLists == null) {
            rail.glLists = new GLObject[rail.subRails.size() + 1];
        }

        boolean hasGLList = GLHelper.isValid(rail.glLists[this.currentRailIndex]);
        if (!hasGLList) {
            //tileEntity.glList = GLHelper.generateVAO();
        } else if (rail.shouldRerenderRail)//再描画
        {
            hasGLList = false;
        }

        if (!hasGLList)//ディスプレイリスト生成
        {
            float[][] fa = this.createRailPos(rail);
            if (fa != null) {
                BlockPos pos = rail.getPos();
                int[] brightness = this.getRailBrightness(rail.getWorld(), pos.getX(), pos.getY(), pos.getZ(), fa);
                FloatBuffer fb = this.createMatrix(fa);

                FloatBuffer buffer = this.genFBuffer(rail, fb, brightness, this.modelSet.modelObj.model.getGroupObjects());
                rail.glLists[this.currentRailIndex] = this.genVBO(buffer);

                rail.shouldRerenderRail = false;
                hasGLList = true;
            } else {
                //GLHelper.deleteGLList(tileEntity.glList);
                rail.glLists[this.currentRailIndex] = null;
            }
        }

        if (hasGLList)//ディスプレイリスト描画
        {
            RailPosition rp = rail.getRailPositions()[0];
            double x = rp.posX - (double) rp.blockX;
            double y = rp.posY - (double) rp.blockY - 0.0625D;
            double z = rp.posZ - (double) rp.blockZ;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (par2 + x), (float) (par4 + y), (float) (par6 + z));
            this.bindTexture(this.getModelObject().textures[0].material.texture);//ディスプレイリストに入れると生成重い
            this.renderVBO(rail.glLists[this.currentRailIndex]);
            GL11.glPopMatrix();
        }
    }

    private FloatBuffer genFBuffer(TileEntityLargeRailCore tileEntity, FloatBuffer matrix, int[] brightness, List<GroupObject> gObjList) {
        List<Float> list = new ArrayList<>();
        int capacity = matrix.capacity() >> 4;
        for (int i = 0; i < capacity; ++i) {
            //tessellator.setBrightness(brightness[i]);
            for (int j = 0; j < gObjList.size(); ++j) {
                GroupObject group = gObjList.get(j);
                if (group.name.startsWith("side") && !(i == 0 || i == capacity - 1)) {
                    continue;
                }//レールの端以外は断面を描画しない, +1~2fps

                if (!this.shouldRenderObject(tileEntity, group.name, capacity, i)) {
                    continue;
                }//描画するかスクリプト側で判断

                for (int k = 0; k < group.faces.size(); ++k) {
                    Face face = group.faces.get(k);
                    //setNormal
                    for (int l = 0; l < face.vertices.length; ++l) {
                        Vertex vtx = face.vertices[l];
                        TextureCoordinate tex = face.textureCoordinates[l];
                        int pos = l << 4;
                        float x0 = vtx.getX() * matrix.get(pos + 0) + vtx.getY() * matrix.get(pos + 4) + vtx.getZ() * matrix.get(pos + 8) + matrix.get(pos + 12);
                        float y0 = vtx.getX() * matrix.get(pos + 1) + vtx.getY() * matrix.get(pos + 5) + vtx.getZ() * matrix.get(pos + 9) + matrix.get(pos + 13);
                        float z0 = vtx.getX() * matrix.get(pos + 2) + vtx.getY() * matrix.get(pos + 6) + vtx.getZ() * matrix.get(pos + 10) + matrix.get(pos + 14);
                        list.add(x0);
                        list.add(y0);
                        list.add(z0);
                        list.add(face.faceNormal.getX());
                        list.add(face.faceNormal.getY());
                        list.add(face.faceNormal.getZ());
                        list.add(tex.getU());
                        list.add(tex.getV());
                    }
                }
            }
        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(list.size());
        for (int i = 0; i < list.size(); ++i) {
            buffer.put(list.get(i));
        }
        buffer.flip();
        return buffer;
    }

    private GLObject genVBO(FloatBuffer buffer) {
        int vtxCout = buffer.capacity() / 5;
        GLObject glObj = GLHelper.generateVAO(vtxCout);
        GL30.glBindVertexArray(glObj.value);

        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        //GL_STATIC_DRAW:変更なし、何度も呼び出し、描画用
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);//VBO内容設定
        int stride = 4 * 3 + 4 * 3 + 4 * 2;
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);//属性設定
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 4 * 3);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, stride, 4 * 3 + 4 * 3);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        return glObj;
    }

    private void renderVBO(GLObject glObj) {
        GL30.glBindVertexArray(glObj.value);
        GL20.glEnableVertexAttribArray(0);

        //GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, glObj.size);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
}
