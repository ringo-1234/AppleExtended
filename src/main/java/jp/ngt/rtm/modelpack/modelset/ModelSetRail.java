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

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.entity.util.CollisionObj;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.RailConfig;
import jp.ngt.rtm.render.BasicRailPartsRenderer;
import jp.ngt.rtm.render.ModelObject;
import jp.ngt.rtm.render.PartsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetRail extends ModelSetBase<RailConfig> {
    public ModelSetRail() {
        super();
    }

    public ModelSetRail(RailConfig par1) {
        super(par1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void constructOnClient() {
        super.constructOnClient();

        if (this.isDummy()) {
            this.modelObj = ModelObject.getDummy();
            this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/signal/button_4cB.png");
        } else {
            RailConfig cfg = this.getConfig();
            PartsRenderer renderer = (!PartsRenderer.validPath(cfg.model.rendererPath)) ? new BasicRailPartsRenderer() : null;
            this.modelObj = new ModelObject(cfg.model, this, renderer);
            this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
        }
    }

    @Override
    public RailConfig getDummyConfig() {
        return RailConfig.getDummy();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderModelInGui(Minecraft par1) {
        ModelObject mo = this.modelObj;
        NGTUtilClient.bindTexture(mo.textures[0].material.texture);
        mo.model.renderAll(false);
    }

    @Override
    public CollisionObj getCollisionObj() {
        return null;
    }
}