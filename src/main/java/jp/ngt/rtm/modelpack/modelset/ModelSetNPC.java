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

import jp.ngt.rtm.entity.util.CollisionObj;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.NPCConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetNPC extends ModelSetBase<NPCConfig> {
    public final ResourceLocation texture;
    public final ResourceLocation lightTexture;

    public ModelSetNPC() {
        super();
        this.texture = ModelPackManager.INSTANCE.getResource("textures/container/19g_JRF_0.png");
        this.lightTexture = null;
    }

    public ModelSetNPC(NPCConfig cfg) {
        super(cfg);
        this.texture = cfg.texture != null ? ModelPackManager.INSTANCE.getResource(cfg.texture) : null;
        this.lightTexture = cfg.lightTexture != null ? ModelPackManager.INSTANCE.getResource(cfg.lightTexture) : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void constructOnClient() {
        super.constructOnClient();

        if (this.isDummy()) {
            this.modelObj = ModelObject.getDummy();
            this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/container/button_19g_JRF_0.png");
        } else {
            NPCConfig cfg = this.getConfig();
            if (cfg.model != null) {
                this.modelObj = new ModelObject(cfg.model, this, null);
            }
            this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
        }
    }

    @Override
    public NPCConfig getDummyConfig() {
        return NPCConfig.getDummy();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderModelInGui(Minecraft par1) {
        if (this.modelObj != null) {
            this.modelObj.render(null, this.getConfig(), 0, 0.0F);
        }
    }

    @Override
    public CollisionObj getCollisionObj() {
        return null;
    }
}