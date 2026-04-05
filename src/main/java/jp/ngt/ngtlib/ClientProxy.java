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

package jp.ngt.ngtlib;

import jp.ngt.ngtlib.event.NGTEventHandlerClient;
import jp.ngt.ngtlib.gui.GuiWarning;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.util.PackInfo;
import jp.ngt.ngtlib.util.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private static GuiWarning GUI_WARNING = new GuiWarning(Minecraft.getMinecraft());

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public File getMinecraftDirectory(String folder) {
        return new File(NGTUtilClient.getMinecraft().mcDataDir, folder);
    }

    @Override
    public String getUserName() {
        return NGTUtilClient.getMinecraft().getSession().getPlayerID();
    }

    @Override
    public void preInit() {
        if (NGTCore.versionCheck) {
            VersionChecker.addToCheckList(new PackInfo(NGTCore.metadata.name, NGTCore.metadata.url, NGTCore.metadata.updateUrl, NGTCore.metadata.version));
        }

        //一旦無効化
		/*NGTCertificate.checkPlayerData(this.getUserName());

		if(!NGTCertificate.canUse())
		{
			MinecraftForge.EVENT_BUS.register(GUI_WARNING);
		}*/

        MinecraftForge.EVENT_BUS.register(NGTEventHandlerClient.INSTANCE);

        ModelLoader.setCustomModelResourceLocation(NGTCore.protection_key, 0,
                new ModelResourceLocation("ngtlib:protection_key", "inventory"));
    }

    @Override
    public void postInit() {
        VersionChecker.checkVersion();
    }

    @Override
    public void removeGuiWarning() {
        MinecraftForge.EVENT_BUS.unregister(GUI_WARNING);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int meta) {
        if (NGTUtil.isServer()) {
            super.breakBlock(world, x, y, z, meta);
            return;
        }

        if (NGTUtilClient.getMinecraft().playerController != null && NGTUtilClient.getMinecraft().player != null) {
            NGTUtilClient.getMinecraft().playerController.onPlayerDestroyBlock(new BlockPos(x, y, z));
        }
    }

    @Override
    public void zoom(EntityPlayer player, int count) {
        float fovModifierHand = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, NGTUtilClient.getMinecraft().entityRenderer, "fovModifierHand", "field_78507_R");
        fovModifierHand = 0.1F;
        ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, NGTUtilClient.getMinecraft().entityRenderer, fovModifierHand, "fovModifierHand", "field_78507_R");
    }

    @Override
    public int getChunkLoadDistance() {
        return NGTUtilClient.getMinecraft().gameSettings.renderDistanceChunks << 4;
    }
}