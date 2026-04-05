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

import jp.ngt.rtm.modelpack.cfg.MachineConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public final class RenderMachine extends TileEntitySpecialRenderer<TileEntityMachineBase> {
    public static final RenderMachine INSTANCE = new RenderMachine();

    private RenderMachine() {
    }

    private void renderMachine(TileEntityMachineBase par1, double par2, double par4, double par6, float par8) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) par2 + 0.5F, (float) par4, (float) par6 + 0.5F);

        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glTranslatef(par1.getOffsetX(), par1.getOffsetY(), par1.getOffsetZ());
        ModelSetMachine modelSet = par1.getResourceState().getResourceSet();
        MachineConfig cfg = modelSet.getConfig();
        if (cfg.rotateByMetadata) {
            switch (par1.getBlockMetadata()) {
                case 0:
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    break;//-y
                case 1:
                    break;
                case 2:
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    break;//-z
                case 3:
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                    break;//+z
                case 4:
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                    break;//+x
                case 5:
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                    break;//-x
            }
        }
        GL11.glTranslatef(0.0F, -0.5F, 0.0F);
        float yaw = par1.getRotation();
        if (cfg.rotateByMetadata && par1.getBlockMetadata() == 0) {
            yaw = -yaw;
        }
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        if (par1.getRotationX() != 0.0F) GL11.glRotatef(par1.getRotationX(), 1.0F, 0.0F, 0.0F);
        if (par1.getRotationZ() != 0.0F) GL11.glRotatef(par1.getRotationZ(), 0.0F, 0.0F, 1.0F);
        float s = par1.getScale();
        if (s != 1.0F) {
            GL11.glScalef(s, s, s);
        }
        modelSet.modelObj.render(par1, cfg, MinecraftForgeClient.getRenderPass(), par8);

        GL11.glPopMatrix();
    }

    @Override
    public void render(TileEntityMachineBase tileEntity, double par2, double par4, double par6, float par8, int par9, float alpha) {
        this.renderMachine(tileEntity, par2, par4, par6, par8);
    }
}