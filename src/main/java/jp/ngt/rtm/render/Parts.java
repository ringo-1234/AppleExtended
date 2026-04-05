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
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.model.GroupObject;
import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Parts {
    public final String[] objNames;
    private GroupObject[] objs;
    private GLObject[] gLists;
    protected boolean ignoreCollision;

    public Parts(String... par1) {
        this.objNames = par1;
    }

    public void init(PartsRenderer renderer) {
        this.gLists = new GLObject[renderer.modelObj.textures.length];
    }

    public GroupObject[] getObjects(IModelNGT model) {
        if (this.objs == null) {
            this.objs = new GroupObject[this.objNames.length];
            for (int i = 0; i < this.objs.length; ++i) {
                for (GroupObject obj : model.getGroupObjects()) {
                    if (this.objNames[i].equals(obj.name)) {
                        this.objs[i] = obj;
                        break;
                    }
                }
            }
        }
        return this.objs;
    }

    public boolean containsName(String name) {
        return NGTUtil.contains(this.objNames, name);
    }

    public void render(PartsRenderer renderer) {
        boolean smoothing = renderer.modelSet.getConfig().smoothing;
        IModelNGT model = renderer.modelObj.model;
        if (model.getGroupObjects().isEmpty())//NGTZ
        {
            model.renderOnly(smoothing, this.objNames);
        } else {
            int i = renderer.currentMatId;
            if (!GLHelper.isValid(this.gLists[i])) {
                this.gLists[i] = GLHelper.generateGLList(this.gLists[i]);
                GLHelper.startCompile(this.gLists[i]);
                NGTRenderHelper.renderCustomModel(model, (byte) i, smoothing, this.objNames);
                GLHelper.endCompile();
            } else {
                if (smoothing) {
                    GL11.glShadeModel(GL11.GL_SMOOTH);
                }

                if (this.ignoreMatId(renderer)) {
                    for (GLObject glo : this.gLists) {
                        GLHelper.callList(glo);
                    }
                } else {
                    GLHelper.callList(this.gLists[i]);
                }

                if (smoothing) {
                    GL11.glShadeModel(GL11.GL_FLAT);
                }
            }
        }
    }

    public boolean ignoreMatId(PartsRenderer renderer) {
        return false;
    }

    public void ignoreCollision(boolean par1) {
        this.ignoreCollision = par1;
    }
}