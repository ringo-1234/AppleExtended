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

package jp.ngt.rtm.electric;

import jp.ngt.rtm.modelpack.modelset.ModelSetSignal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderSignal extends TileEntitySpecialRenderer<TileEntitySignal> {
    public void renderTileEntitySignalAt(TileEntitySignal tileEntity, double par2, double par4, double par6, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);//Block&TileEntity描画のため、座標移動に+0.5しない

        int pass = MinecraftForgeClient.getRenderPass();
        float dir = tileEntity.getBlockDirection();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.0F, 0.5F);
        GL11.glTranslatef(tileEntity.getOffsetX(), tileEntity.getOffsetY(), tileEntity.getOffsetZ());
        GL11.glRotatef(dir, 0.0F, 1.0F, 0.0F);
        if (tileEntity.getRotationX() != 0.0F) GL11.glRotatef(tileEntity.getRotationX(), 1.0F, 0.0F, 0.0F);
        if (tileEntity.getRotationZ() != 0.0F) GL11.glRotatef(tileEntity.getRotationZ(), 0.0F, 0.0F, 1.0F);

        ModelSetSignal modelSet = tileEntity.getResourceState().getResourceSet();
        if (modelSet != null && !modelSet.isDummy()) {
            float s = tileEntity.getScale();
            if (s != 1.0F) {
                GL11.glScalef(s, s, s);
            }
            modelSet.modelObj.render(tileEntity, modelSet.getConfig(), pass, partialTicks);
        }
        GL11.glPopMatrix();

        this.renderBaseBlock(tileEntity, pass, partialTicks);
        this.renderBaseTileEntity(tileEntity, par2, par4, par6, pass, dir);
        GL11.glPopMatrix();
    }

    private void renderBaseBlock(TileEntitySignal signal, int pass, float partialTicks) {
        GL11.glPushMatrix();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GL11.glTranslatef(0.0F, 0.0F, 1.0F);

        IBlockState state = signal.getResourceState().getBlockState();
        float brightness = signal.getWorld().getLightBrightness(signal.getPos());
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, brightness);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void renderBaseTileEntity(TileEntitySignal signal, double x, double y, double z, int pass, float partialTicks) {
        TileEntity tile = signal.getOrigTileEntity();
        if (tile == null) {
            return;
        }

        tile.setWorld(signal.getWorld());
        TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);

        if (tile.shouldRenderInPass(pass) && renderer != null) {
            try {
                renderer.render(tile, 0, 0, 0, partialTicks, 0, 1.0F);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                CrashReport report = CrashReport.makeCrashReport(throwable, "Rendering TileEntity in Miniature");
                CrashReportCategory category = report.makeCategory("TileEntity Details");
                tile.addInfoToCrashReport(category);
                throw new ReportedException(report);
            }
        }
    }

    @Override
    public void render(TileEntitySignal tileEntity, double par2, double par4, double par6, float par8, int par9, float alpha) {
        this.renderTileEntitySignalAt(tileEntity, par2, par4, par6, par8);
    }
}