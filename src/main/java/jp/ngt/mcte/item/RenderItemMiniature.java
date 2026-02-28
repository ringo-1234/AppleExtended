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

package jp.ngt.mcte.item;

import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.block.MiniatureBlockState;
import jp.ngt.mcte.block.RenderMiniature;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.item.IItemRendererCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.NGTObjectRenderer;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderItemMiniature implements IItemRendererCustom {
    public final static RenderItemMiniature INSTANCE = new RenderItemMiniature();

    private Map<ItemStack, RenderProp> propMap = new HashMap<ItemStack, RenderProp>();

    private RenderItemMiniature() {
    }

    @Override
    public void renderItem(ItemStack itemStack) {
        GL11.glPushMatrix();
        //json側での移動が適用されないので、こちで調整
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
        this.renderItem(itemStack, 0);
        GL11.glPopMatrix();
    }

    /**
     * @param item
     * @param renderType 1なら装備として描画
     */
    public void renderItem(ItemStack item, int renderType) {
        if (!item.hasTagCompound()) {
            //MissingModel代わり
            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(0.0F, -0.425F, 0.0F);//-0.5
            Minecraft mc = NGTUtilClient.getMinecraft();
            mc.getBlockRendererDispatcher().renderBlockBrightness(Blocks.STONE.getDefaultState(), mc.player.getBrightness());
            GL11.glPopMatrix();
            return;
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(8256);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        RenderProp prop = this.propMap.get(item);
        if (prop == null) {
            NGTObject ngto = ItemMiniature.getNGTObject(item.getTagCompound());
            if (ngto == null) {
                return;
            }
            prop = new RenderProp(ngto, item);
            this.propMap.put(item, prop);
        }

        GL11.glTranslatef(prop.offsetX, prop.offsetY, prop.offsetZ);

        RenderMiniature.setupMotionAndScale(prop.mbState, 0.0F, prop.scale, prop.offsetX, prop.offsetY, prop.offsetZ, 0);

        GL11.glTranslatef(-prop.corX, 0.0F, -prop.corZ);
        int pass = MinecraftForgeClient.getRenderPass();
        if (pass >= 0 && renderType == 0) {
            this.renderWithProp(prop, pass);
        } else {
            //Item描画時
            //装備はpass=0のみ呼び出しのため、こちらで2pass処理
            ForgeHooksClient.setRenderPass(0);
            this.renderWithProp(prop, 0);
            ForgeHooksClient.setRenderPass(1);
            this.renderWithProp(prop, 1);
            ForgeHooksClient.setRenderPass(pass);
        }

        GLHelper.setLightmapMaxBrightness();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderWithProp(RenderProp prop, int pass) {
        NGTObjectRenderer.INSTANCE.renderTileEntities(prop.world, 0.0F, pass);
        NGTObjectRenderer.INSTANCE.renderEntities(prop.world, 0.0F, pass);
        this.renderBlocks(prop, 0.0F, pass);
    }

    //RenderMiniatureと統合すべし
    private void renderBlocks(RenderProp prop, float par3, int pass) {
        if (prop.glLists == null) {
            prop.glLists = new GLObject[2];
        }

        NGTUtilClient.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        boolean smoothing = NGTUtilClient.getMinecraft().gameSettings.ambientOcclusion != 0;
        if (smoothing) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }

        if (!GLHelper.isValid(prop.glLists[pass])) {
            prop.glLists[pass] = GLHelper.generateGLList(prop.glLists[pass]);
            GLHelper.startCompile(prop.glLists[pass]);
            NGTObjectRenderer.INSTANCE.renderNGTObject(prop.world, prop.ngto, true, prop.mode, pass);
            GLHelper.endCompile();
        } else {
            GLHelper.callList(prop.glLists[pass]);
        }

        if (smoothing) {
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        GLHelper.enableLighting();
        NGTUtilClient.getMinecraft().entityRenderer.enableLightmap();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);//明るさ戻す
    }

    private void renderArmor(ItemStack item) {
        if (item == null) {
            return;
        }

        if (item.getItem() == MCTE.itemMiniature) {
            float f1 = 0.625F;
            GL11.glScalef(f1, f1, f1);
            this.renderItem(item, 1);
        }
    }

    //以下Eventハンドル/////////////////////////////////////////////////////////////////////////

    //pass=0のみ
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();

        GL11.glPushMatrix();
        GL11.glRotatef(-player.renderYawOffset, 0.0F, 1.0F, 0.0F);//renderYawOffset->頭に追従
        if (player.isSneaking()) {
            //スニーク時角度調整、基点が足元なので注意
            GL11.glTranslatef(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(25.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, 0.5F, 0.0F);//必須1.9
        this.renderArmor(player.inventory.armorItemInSlot(2));//Chest
        GL11.glPopMatrix();

        //ヘルメットはデフォルトで描画されるので、なし
		/*GL11.glPushMatrix();
		event.getRenderer().getMainModel().bipedHead.postRender(0.0625F);
		this.renderArmor(player.inventory.armorItemInSlot(3));//Helmet
		GL11.glPopMatrix();*/
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        Entity entity = event.getEntity();

        if (event.getRenderer() instanceof RenderBiped && entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            GL11.glPushMatrix();
            /**RenderLivingEntity.doRender()*******************************************/
            float f2 = living.renderYawOffset;
            float f3 = living.rotationYawHead;
            if (living.isRiding() && living.getRidingEntity() instanceof EntityLivingBase) {
                EntityLivingBase rider = (EntityLivingBase) living.getRidingEntity();
                f2 = rider.renderYawOffset;
                float f4 = NGTMath.wrapAngle(f3 - f2);
                f4 = f4 < -85.0F ? -85.0F : (f4 >= 85.0F ? 85.0F : f4);
                f2 = f3 - f4;
                if (f4 * f4 > 2500.0F) {
                    f2 += f4 * 0.2F;
                }
            }
            float f13 = living.rotationPitch;
            GL11.glTranslatef((float) event.getX(), (float) event.getY(), (float) event.getZ());
            //rotateCorpse
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            if (living.deathTime > 0) {
                float f33 = ((float) living.deathTime - 1.0F) / 20.0F * 1.6F;
                f33 = (float) Math.sqrt(f33);
                if (f33 > 1.0F) {
                    f33 = 1.0F;
                }
                GL11.glRotatef(f33 * 90.0F, 0.0F, 0.0F, 1.0F);
            }
            float f5 = 0.0625F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            /************************************************************************/

            GL11.glPushMatrix();
            if (entity.isSneaking()) {
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(25.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
            }
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
            this.renderArmor(living.getItemStackFromSlot(EntityEquipmentSlot.CHEST));//Chest
            GL11.glPopMatrix();

            //ここで処理しなくても描画される
			/*GL11.glPushMatrix();
			((RenderBiped)event.getRenderer()).modelBipedMain.bipedHead.postRender(0.0625F);
			this.renderArmor(living.getItemStackFromSlot(EntityEquipmentSlot.HEAD));//Helmet
			GL11.glPopMatrix();*/

            GL11.glPopMatrix();
        }
    }

    /// ////////////////////////////////////////////////////////////////////////

    @SideOnly(Side.CLIENT)
    private class RenderProp {
        public NGTObject ngto;
        public MiniatureBlockState mbState;
        public float offsetX, offsetY, offsetZ;
        public float scale;
        public int mode;

        public NGTWorld world;
        public GLObject[] glLists;
        public float corX, corZ;
        public float scaleInInventory;

        public RenderProp(NGTObject par1, ItemStack item) {
            this.ngto = par1;
            this.world = new NGTWorld(NGTUtil.getClientWorld(), par1);
            this.corX = (float) par1.xSize * 0.5F;
            this.corZ = (float) par1.zSize * 0.5F;
            NBTTagCompound nbt = item.getTagCompound();
            this.mbState = ItemMiniature.getMiniatureBlockState(nbt, item);
            float[] fa = ItemMiniature.getOffset(nbt);
            this.offsetX = fa[0];
            this.offsetY = fa[1];
            this.offsetZ = fa[2];
            this.scale = ItemMiniature.getScale(nbt);
            this.mode = ItemMiniature.getMode(nbt).id;
            int i0 = par1.xSize > par1.ySize ? (par1.xSize > par1.zSize ? par1.xSize : par1.zSize) : (par1.ySize > par1.zSize ? par1.ySize : par1.zSize);
            this.scaleInInventory = 1.0F / (float) i0;
        }
    }
}