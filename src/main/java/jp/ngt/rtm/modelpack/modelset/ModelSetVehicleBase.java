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

import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig;
import jp.ngt.rtm.render.BasicVehiclePartsRenderer;
import jp.ngt.rtm.render.ModelObject;
import jp.ngt.rtm.render.PartsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.script.ScriptEngine;

public abstract class ModelSetVehicleBase<T extends VehicleBaseConfig> extends ModelSetBase<T> {
    @SideOnly(Side.CLIENT)
    public ResourceLocation rollsignTexture;
    @SideOnly(Side.CLIENT)
    public ScriptEngine soundSE;

    public ModelSetVehicleBase() {
        super();
    }

    public ModelSetVehicleBase(T par1) {
        super(par1);
    }

    @Override
    public void constructOnServer() {
        super.constructOnServer();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void constructOnClient() {
        super.constructOnClient();

        if (this.isDummy()) {
            this.modelObj = ModelObject.getDummy();
            this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/train/hoge.png");
            this.rollsignTexture = null;
        } else {
            PartsRenderer renderer = (!PartsRenderer.validPath(cfg.getModel().rendererPath)) ? new BasicVehiclePartsRenderer(String.valueOf(true)) : null;
            this.modelObj = new ModelObject(cfg.getModel(), this, renderer, "vehicle");

            this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
            this.rollsignTexture = cfg.rollsignTexture == null ? null : ModelPackManager.INSTANCE.getResource(cfg.rollsignTexture);

            if (this.cfg.soundScriptPath != null) {
                this.soundSE = com.anatawa12.fixRtm.scripting.FIXScriptUtil.getScriptAndDoScript(this.getConfig().soundScriptPath);
            }
        }
    }

    @Override
    public T getConfig() {
        return this.cfg;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderModelInGui(Minecraft par1) {
        VehicleBaseConfig cfg = (VehicleBaseConfig) this.cfg;
        this.modelObj.render(null, cfg, 0, 0.0F);
        this.modelObj.render(null, cfg, 1, 0.0F);
        this.renderPartsInGui(par1);
    }

    @SideOnly(Side.CLIENT)
    protected void renderPartsInGui(Minecraft par1) {
    }
}