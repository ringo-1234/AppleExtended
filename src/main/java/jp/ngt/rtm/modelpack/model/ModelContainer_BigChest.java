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

package jp.ngt.rtm.modelpack.model;

import jp.ngt.ngtlib.renderer.model.MCModel;
import net.minecraft.client.model.ModelChest;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class ModelContainer_BigChest extends MCModel {
    private ModelChest model = new ModelChest();

    public ModelContainer_BigChest() {
        this.setSizeBox(-1.5F, 0.0F, -1.5F, 1.5F, 3.0F, 1.5F);
    }

    @Override
    public void renderAll(boolean smoothing) {
        GL11.glPushMatrix();
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(-1.5F, -3.0F, -1.5F);
        float scale = 3.0F;
        GL11.glScalef(scale, scale, scale);
        this.model.renderAll();
        GL11.glPopMatrix();
    }
}