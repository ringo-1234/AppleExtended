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

package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.rtm.entity.util.ColFace;
import jp.ngt.rtm.entity.util.CollisionObj;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.ModelConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.script.ScriptEngine;

import jp.apple.script.ScriptLoader;

public abstract class ModelSetBase<T extends ModelConfig> extends ResourceSet<T> {
    @SideOnly(Side.CLIENT)
    public ModelObject modelObj;
    @SideOnly(Side.CLIENT)
    public ResourceLocation buttonTexture;

    @SideOnly(Side.CLIENT)
    public ScriptEngine guiSE;
    @SideOnly(Side.CLIENT)
    public ResourceLocation guiTexture;

    private CollisionObj collisionObj;
    private boolean syncFinished;

    public ScriptEngine serverSE;

    public ModelSetBase() {
        super();
    }

    public ModelSetBase(T par1) {
        super(par1);
    }

    @Override
    public void constructOnServer() {
        if (this.cfg.serverScriptPath != null) {
            this.serverSE = ScriptLoader.load(((ModelConfig) this.cfg).serverScriptPath);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void constructOnClient() {
        if (!this.isDummy()) {
            if (this.cfg.serverScriptPath != null) {
                this.serverSE = ScriptLoader.load(((ModelConfig) this.cfg).serverScriptPath);
            }

            if (this.cfg.guiScriptPath != null) {
                this.guiSE = ScriptLoader.load(((ModelConfig) this.cfg).guiScriptPath);
                this.guiTexture = ModelPackManager.INSTANCE.getResource(this.cfg.guiTexture);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void finishConstruct() {
        if (this.modelObj != null) {
            this.collisionObj = new CollisionObj(this.modelObj.model, this.getConfig());
        }
    }

    @Override
    public T getConfig() {
        return this.cfg;
    }

    @SideOnly(Side.CLIENT)
    public void renderModelInGui(Minecraft mc) {
        this.modelObj.render(null, this.getConfig(), 0, 0.0F);
    }

    public void addColFace(String partsName, ColFace face, byte status) {
        if (!this.syncFinished) {
            if (this.collisionObj == null) {
                this.collisionObj = new CollisionObj();
            }
            this.collisionObj.addColFace(partsName, face, status);
            this.syncFinished = (status == 2);
        }
    }

    public CollisionObj getCollisionObj() {
        return this.collisionObj;
    }
}