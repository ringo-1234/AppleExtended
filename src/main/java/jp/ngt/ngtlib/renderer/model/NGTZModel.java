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

import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTZ;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.NGTObjectRenderer;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class NGTZModel implements IModelNGT {
    private final List<NGTOParts> objects = new ArrayList();
    private final float scale;

    private final ArrayList<GroupObject> parts = new ArrayList<GroupObject>();
    private final Map<String, Material> materials = new HashMap<String, Material>();
    private final float[] sizeBox = new float[6];

    public NGTZModel(ResourceLocation par1, float par2) {
        Map<String, NGTObject> objs = (new NGTZ(par1)).getObjects();
        this.scale = par2;
        for (Entry<String, NGTObject> set : objs.entrySet()) {
            NGTOParts parts = new NGTOParts(set.getKey(), set.getValue());
            this.objects.add(parts);
            NGTOModel.calcSizeBox(parts.ngto, this.scale, this.sizeBox);
        }
        this.materials.put(NGTOModel.GROUP_NAME, new Material((byte) 0, TextureMap.LOCATION_BLOCKS_TEXTURE));
    }

    @Override
    public float[] getSize() {
        return this.sizeBox;
    }

    @Override
    public void renderAll(boolean smoothing) {
        for (NGTOParts obj : this.objects) {
            obj.render(this.scale);
        }
    }

    @Override
    public void renderOnly(boolean smoothing, String... groupNames) {
        for (NGTOParts obj : this.objects) {
            for (String s : groupNames) {
                if (s.equals(obj.name)) {
                    obj.render(this.scale);
                }
            }
        }
    }

    @Override
    public void renderPart(boolean smoothing, String partName) {
        for (NGTOParts obj : this.objects) {
            if (partName.equals(obj.name)) {
                obj.render(this.scale);
            }
        }
    }

    @Override
    public int getDrawMode() {
        return 0;
    }

    @Override
    public ArrayList<GroupObject> getGroupObjects() {
        return this.parts;
    }

    @Override
    public Map<String, Material> getMaterials() {
        return this.materials;
    }

    @Override
    public FileType getType() {
        return FileType.NGTZ;
    }

    private class NGTOParts {
        private final String name;
        private final NGTObject ngto;
        private GLObject[] glLists;
        private NGTWorld world;

        public NGTOParts(String par1, NGTObject par2) {
            this.name = par1;
            this.ngto = par2;
        }

        public void render(float scale) {
            if (this.world == null) {
                if (NGTUtil.getClientWorld() == null) {
                    return;
                }
                this.world = new NGTWorld(NGTUtil.getClientWorld(), this.ngto);
            }
            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, scale);
            float x = (float) this.ngto.xSize * 0.5F;
            float z = (float) this.ngto.zSize * 0.5F;
            GL11.glTranslatef(-x, 0.0F, -z);
            int pass = MinecraftForgeClient.getRenderPass();
            if (pass == -1) {
                pass = 0;
            }
            NGTObjectRenderer.INSTANCE.renderTileEntities(this.world, 0.0F, pass);
            NGTObjectRenderer.INSTANCE.renderEntities(this.world, 0.0F, pass);
            this.renderBlocks(pass);
            GL11.glPopMatrix();
        }

        private void renderBlocks(int pass) {
            if (this.glLists == null) {
                this.glLists = new GLObject[2];
            }

            NGTUtilClient.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            boolean smoothing = NGTUtilClient.getMinecraft().gameSettings.ambientOcclusion != 0;
            if (smoothing) {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }
            if (!GLHelper.isValid(this.glLists[pass])) {
                this.glLists[pass] = GLHelper.generateGLList(this.glLists[pass]);
                GLHelper.startCompile(this.glLists[pass]);
                NGTObjectRenderer.INSTANCE.renderNGTObject(this.world, this.ngto, true, 0, pass);
                GLHelper.endCompile();
            } else {
                GLHelper.callList(this.glLists[pass]);
            }
            if (smoothing) {
                GL11.glShadeModel(GL11.GL_FLAT);
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GLHelper.enableLighting();
            NGTUtilClient.getMinecraft().entityRenderer.enableLightmap();
        }
    }
}