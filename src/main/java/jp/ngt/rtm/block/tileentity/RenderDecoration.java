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

import jp.ngt.ngtlib.item.IItemRendererCustom;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.block.decoration.DecorationModel;
import jp.ngt.rtm.block.decoration.DecorationStore;
import jp.ngt.rtm.block.decoration.Element;
import jp.ngt.rtm.block.decoration.Face;
import jp.ngt.rtm.item.ItemDecoration;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderDecoration extends TileEntitySpecialRenderer<TileEntityDecoration> implements IItemRendererCustom {
    @Override
    public void render(TileEntityDecoration par1, double par2, double par4, double par6, float par8, int par9, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) par2 + 0.0F, (float) par4 + 0.0F, (float) par6 + 0.0F);
        DecorationModel model = DecorationStore.INSTANCE.getModel(par1.getModelName());
        this.renderModel(model, true);
        GL11.glPopMatrix();
    }

    public void renderModel(DecorationModel model, boolean onWorld) {
        GLHelper.disableLighting();
        //RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        NGTUtilClient.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);//GUIから呼ばれた場合のぬるぽ防止
        NGTTessellator tessellator = NGTTessellator.instance;
        tessellator.startDrawingQuads();
        for (Element element : model.elements) {
            for (Face face : element.faces) {
                TextureAtlasSprite icon = NGTUtilClient.getIcon(face.texture);
                tessellator.setColorOpaque_F(face.shadow, face.shadow, face.shadow);
                this.addVertex(tessellator, icon, face.vertex[0]);
                this.addVertex(tessellator, icon, face.vertex[1]);
                this.addVertex(tessellator, icon, face.vertex[2]);
                this.addVertex(tessellator, icon, face.vertex[3]);
            }
        }
        tessellator.draw();

        GLHelper.enableLighting();
        //RenderHelper.enableStandardItemLighting();
        if (onWorld) {
            //インベントリでこれやると、夜に暗くなる
            NGTUtilClient.getMinecraft().entityRenderer.enableLightmap();
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void addVertex(NGTTessellator tessellator, TextureAtlasSprite icon, float[] array) {
        float u = icon.getMinU() + (icon.getMaxU() - icon.getMinU()) * array[3];
        float v = icon.getMinV() + (icon.getMaxV() - icon.getMinV()) * array[4];
        tessellator.addVertexWithUV(array[0], array[1], array[2], u, v);
    }

    @Override
    public void renderItem(ItemStack itemStack) {
        String name = ItemDecoration.getModelName(itemStack);
        DecorationModel model = DecorationStore.INSTANCE.getModel(name);
        GL11.glPushMatrix();
        //json側での移動が適用されないので、こっちで調整
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
        this.renderModel(model, false);
        GL11.glPopMatrix();
    }
}