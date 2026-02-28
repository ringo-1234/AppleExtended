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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.rtm.modelpack.cfg.OrnamentConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetOrnament;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderOrnament<T extends TileEntityOrnament> extends TileEntitySpecialRenderer<T> {
    private void renderOrnament(T par1, double par2, double par4, double par6, float par8) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) par2 + 0.5F, (float) par4 + 0.5F, (float) par6 + 0.5F);

        ModelSetOrnament modelSet = par1.getResourceState().getResourceSet();
        OrnamentConfig cfg = modelSet.getConfig();
        int pass = MinecraftForgeClient.getRenderPass();

        GL11.glTranslatef(par1.getOffsetX(), par1.getOffsetY(), par1.getOffsetZ());
        GL11.glRotatef(par1.getRotation(), 0.0F, 1.0F, 0.0F);

        if (modelSet.modelObj.renderer.getScript() == null) {
            float scale = par1.getRandomScale();
            GL11.glScalef(scale, scale, scale);
        }
        modelSet.modelObj.render(par1, cfg, pass, par8);

        GL11.glPopMatrix();
    }

    @Override
    public void render(T tileEntity, double par2, double par4, double par6, float par8, int par9, float alpha) {
        this.renderOrnament(tileEntity, par2, par4, par6, par8);
    }
}