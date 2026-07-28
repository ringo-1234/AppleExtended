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

package jp.apple.render.item;

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.item.ItemWithModel;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CustomIconItemStackRenderer extends TileEntityItemStackRenderer {
    public static final CustomIconItemStackRenderer INSTANCE = new CustomIconItemStackRenderer();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (!(stack.getItem() instanceof ItemWithModel)) {
            return;
        }
        ResourceLocation tex = ((ItemWithModel<?>) stack.getItem()).getCustomIconTexture(stack);
        if (tex == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        NGTUtilClient.bindTexture(tex);

        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.scale(1F / 16F, -1F / 16F, 1F / 16F);
        GlStateManager.translate(-8.0F, -8.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 16, 0).tex(0, 1).endVertex();
        buffer.pos(16, 16, 0).tex(1, 1).endVertex();
        buffer.pos(16, 0, 0).tex(1, 0).endVertex();
        buffer.pos(0, 0, 0).tex(0, 0).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}