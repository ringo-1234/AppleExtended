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

package jp.ngt.ngtlib.renderer;

import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.world.IBlockAccessNGT;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class NGTObjectRenderer {
    public static final NGTObjectRenderer INSTANCE = new NGTObjectRenderer();

    private NGTObjectRenderer() {
    }

    /**
     * @param par1
     * @param par2
     * @param changeLightting
     * @param mode            0:ミニチュア, 1:彫刻
     *                        <br>
     *                        <br>
     *                        ブロックの一括描画<br>
     *                        ※テクスチャバインドは行わない
     */
    public void renderNGTObject(IBlockAccessNGT par1, NGTObject par2, boolean changeLightting, int mode, int pass) {
        GL11.glPushMatrix();
        if (changeLightting) {
            GLHelper.disableLighting();//これないと若干暗くなる
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);//ガラスや鉄柵が不透明になる

        boolean isSculpture = (mode == 1 && par2.xSize == par2.ySize && par2.xSize == par2.zSize);

        if (isSculpture) {
            float f0 = (float) par2.xSize;
            GL11.glScalef(f0, f0, f0);
        }

        if (pass == 1) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }

        NGTTessellator tessellator = NGTTessellator.instance;
        BufferBuilder renderer = null;

        if (isSculpture) {
            tessellator.startDrawingQuads();
        } else {
            //1chunk当たりのバッファ・サイズ(RegionRenderCacheBuilderより)
            //0x200000 + 0x20000 + 0x20000 + 0x40000 = 0x280000
            //16*16*16で割る
            int bufSize = (par2.blockList.size() * 0x280000) >> 12;
            if (bufSize <= 0) {
                bufSize = 1 << 12;
            }
            renderer = new BufferBuilder(bufSize);
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        }

        for (int i = 0; i < par2.xSize; ++i) {
            for (int j = 0; j < par2.ySize; ++j) {
                for (int k = 0; k < par2.zSize; ++k) {
                    BlockSet set = par1.getBlockSet(i, j, k);
                    BlockRenderLayer layer = set.block.getBlockLayer();
                    boolean renderFlag = false;
                    //CUTOUT_MIPPED:ガラス、葉、Pane
                    //CUTOUT:ベッド、ドア、草、レール
                    //TRANSLUCENT:液体、ステンドグラス、氷
                    //canRenderInLayer(layer)は内部で単純比較してるだけ
                    if (pass == 0) {
                        renderFlag = (layer == BlockRenderLayer.SOLID)
                                || (layer == BlockRenderLayer.CUTOUT_MIPPED)
                                || (layer == BlockRenderLayer.CUTOUT);
                    } else if (pass == 1) {
                        renderFlag = (layer == BlockRenderLayer.TRANSLUCENT);
                    }

                    if (set.toBlockState().getMaterial() != Material.AIR && renderFlag) {
                        if (isSculpture) {
                            this.renderBlockAsSculpture(par1, set, par2, i, j, k);
                        } else {
                            this.renderBlockByRenderer(par1, renderer, set, i, j, k);
                        }
                    }
                }
            }
        }

        if (isSculpture) {
            tessellator.draw();
        } else {
            renderer.finishDrawing();
            //Tessellatorより
            WorldVertexBufferUploader vboUploader = new WorldVertexBufferUploader();
            vboUploader.draw(renderer);
        }


        if (pass == 1) {
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (changeLightting) {
            GLHelper.enableLighting();
        }
        GL11.glPopMatrix();
    }

    private void renderBlockByRenderer(IBlockAccess accessor, BufferBuilder renderer, BlockSet blockSet, int x, int y, int z) {
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState state = blockSet.block.getStateFromMeta(blockSet.metadata);
        dispatcher.renderBlock(state, new BlockPos(x, y, z), accessor, renderer);
    }

    private void renderBlockAsSculpture(IBlockAccess accessor, BlockSet set, NGTObject ngto, int x, int y, int z) {
        GL11.glPushMatrix();

        IBlockState state = set.toBlockState();
        Block block = set.block;
        int meta = set.metadata;
        float f0 = (float) ngto.xSize;
        float scale = 1.0F / f0;//1ブロック当りの大きさ
        float minX = (float) x / f0;
        float minY = (float) y / f0;
        float minZ = (float) z / f0;
        float maxX = minX + scale;
        float maxY = minY + scale;
        float maxZ = minZ + scale;
        NGTTessellator tessellator = NGTTessellator.instance;

        TextureAtlasSprite icon;
        float uDif;
        float minU;
        float maxU;
        float vDif;
        float minV;
        float maxV;

        if (state.shouldSideBeRendered(accessor, new BlockPos(x, y - 1, z), EnumFacing.DOWN)) {
            icon = this.getIcon(block, meta, 0);
            uDif = icon.getMaxU() - icon.getMinU();
            minU = icon.getMinU() + maxX * uDif;
            maxU = icon.getMinU() + minX * uDif;
            vDif = icon.getMaxV() - icon.getMinV();
            minV = icon.getMinV() + minZ * vDif;
            maxV = icon.getMinV() + maxZ * vDif;

            tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
            tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
            tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
            tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, minV);
            tessellator.addVertexWithUV(minX, minY, maxZ, minU, minV);
        }

        if (state.shouldSideBeRendered(accessor, new BlockPos(x, y + 1, z), EnumFacing.UP)) {
            icon = this.getIcon(block, meta, 1);
            uDif = icon.getMaxU() - icon.getMinU();
            minU = icon.getMinU() + maxX * uDif;
            maxU = icon.getMinU() + minX * uDif;
            vDif = icon.getMaxV() - icon.getMinV();
            minV = icon.getMinV() + maxZ * vDif;
            maxV = icon.getMinV() + minZ * vDif;

            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	        /*tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);
	        tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, maxV);
	        tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, maxV);
	        tessellator.addVertexWithUV(minX, maxY, minZ, maxU, minV);*/

            tessellator.addVertexWithUV(minX, maxY, maxZ, maxU, minV);
            tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, minV);
            tessellator.addVertexWithUV(maxX, maxY, minZ, minU, maxV);
            tessellator.addVertexWithUV(minX, maxY, minZ, maxU, maxV);
        }

        if (state.shouldSideBeRendered(accessor, new BlockPos(x, y, z - 1), EnumFacing.NORTH)) {
            icon = this.getIcon(block, meta, 2);
            uDif = icon.getMaxU() - icon.getMinU();
            minU = icon.getMaxU() - maxX * uDif;
            maxU = icon.getMaxU() - minX * uDif;
            vDif = icon.getMaxV() - icon.getMinV();
            minV = icon.getMaxV() - maxY * vDif;
            maxV = icon.getMaxV() - minY * vDif;

            tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
            tessellator.addVertexWithUV(maxX, maxY, minZ, minU, minV);
            tessellator.addVertexWithUV(maxX, minY, minZ, minU, maxV);
            tessellator.addVertexWithUV(minX, minY, minZ, maxU, maxV);
            tessellator.addVertexWithUV(minX, maxY, minZ, maxU, minV);
        }

        if (state.shouldSideBeRendered(accessor, new BlockPos(x, y, z + 1), EnumFacing.SOUTH)) {
            icon = this.getIcon(block, meta, 3);
            uDif = icon.getMaxU() - icon.getMinU();
            minU = icon.getMinU() + minX * uDif;
            maxU = icon.getMinU() + maxX * uDif;
            vDif = icon.getMaxV() - icon.getMinV();
            minV = icon.getMaxV() - maxY * vDif;
            maxV = icon.getMaxV() - minY * vDif;

            tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
            tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);
            tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
            tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
            tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV);
        }

        if (state.shouldSideBeRendered(accessor, new BlockPos(x - 1, y, z), EnumFacing.WEST)) {
            icon = this.getIcon(block, meta, 4);
            uDif = icon.getMaxU() - icon.getMinU();
            minU = icon.getMinU() + minZ * uDif;
            maxU = icon.getMinU() + maxZ * uDif;
            vDif = icon.getMaxV() - icon.getMinV();
            minV = icon.getMaxV() - maxY * vDif;
            maxV = icon.getMaxV() - minY * vDif;

            tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
            tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
            tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
            tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
            tessellator.addVertexWithUV(minX, maxY, maxZ, maxU, minV);
        }

        if (state.shouldSideBeRendered(accessor, new BlockPos(x + 1, y, z), EnumFacing.EAST)) {
            icon = this.getIcon(block, meta, 5);
            uDif = icon.getMaxU() - icon.getMinU();
            minU = icon.getMaxU() - maxZ * uDif;
            maxU = icon.getMaxU() - minZ * uDif;
            vDif = icon.getMaxV() - icon.getMinV();
            minV = icon.getMaxV() - maxY * vDif;
            maxV = icon.getMaxV() - minY * vDif;

            tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
            tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, minV);
            tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);
            tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
            tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
        }

        GL11.glPopMatrix();
    }

    private TextureAtlasSprite getIcon(Block block, int meta, int side) {
        IBlockState state = block.getStateFromMeta(meta);
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
        List<BakedQuad> list = model.getQuads(state, EnumFacing.getFront(side), 0);
        return list.isEmpty() ? model.getParticleTexture() : list.get(0).getSprite();
        //return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public void renderTileEntities(NGTWorld par1, float par8, int pass) {
        if (!par1.tileEntityLoaded) {
            this.loadTileEntity(par1);
            par1.tileEntityLoaded = true;
        }

        for (TileEntity tile : par1.getTileEntityList()) {
            BlockPos pos = tile.getPos();
            this.renderTileEntityByRenderer(tile, pos.getX(), pos.getY(), pos.getZ(), par8, pass);
        }
    }

    private void loadTileEntity(NGTWorld par1) {
        List<TileEntity> list = new ArrayList<TileEntity>();

        for (int i = 0; i < par1.blockObject.xSize; ++i) {
            for (int j = 0; j < par1.blockObject.ySize; ++j) {
                for (int k = 0; k < par1.blockObject.zSize; ++k) {
                    TileEntity tile = par1.getTileEntity(new BlockPos(i, j, k));
                    if (tile != null && TileEntityRendererDispatcher.instance.getRenderer(tile) != null) {
                        list.add(tile);
                    }
                }
            }
        }

        par1.setTileEntityList(list);
    }

    public void renderTileEntityByRenderer(TileEntity tile, double par2, double par4, double par6, float par8, int pass) {
        TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);

        if (tile.shouldRenderInPass(pass) && renderer != null) {
            try {
                renderer.render(tile, par2, par4, par6, par8, 0, 1.0F);
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

    public void renderEntities(NGTWorld par1, float par8, int pass) {
        List<Entity> list = par1.getEntityList();
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!entity.isDead) {
                this.renderEntityByRenderer(entity, par8, pass);
            }
        }
    }

    public void renderEntityByRenderer(Entity entity, float par8, int pass) {
        if (!entity.shouldRenderInPass(pass)) {
            return;
        }

        //RenderManager.instance.renderEntitySimple(entity, par8);
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) par8;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) par8;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) par8;
        float f1 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par8;

        int i = entity.isBurning() ? 15728880 : entity.getBrightnessForRender();
        GLHelper.setBrightness(i);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        try {
            NGTUtilClient.getMinecraft().getRenderManager().renderEntity(entity, d0, d1, d2, f1, par8, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}