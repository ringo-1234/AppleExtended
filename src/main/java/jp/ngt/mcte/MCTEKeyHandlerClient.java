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

package jp.ngt.mcte;

import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.mcte.editor.filter.FilterManager;
import jp.ngt.mcte.network.PacketMCTEKey;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MCTEKeyHandlerClient {
    private static final String CATG_MCTE = "mcte.key";
    public static final KeyBinding keyEditMenu = new KeyBinding("mcte.editor.menu", Keyboard.KEY_K, CATG_MCTE);
    public static final KeyBinding keyEditMode = new KeyBinding("mcte.editor.mode", Keyboard.KEY_M, CATG_MCTE);
    public static final KeyBinding keyDelete = new KeyBinding("mcte.editor.delete", Keyboard.KEY_DELETE, CATG_MCTE);
    public static final KeyBinding keyUndo = new KeyBinding("mcte.editor.undo", Keyboard.KEY_Z, CATG_MCTE);
    public static final KeyBinding keyCut = new KeyBinding("mcte.editor.cut", Keyboard.KEY_X, CATG_MCTE);
    public static final KeyBinding keyCopy = new KeyBinding("mcte.editor.copy", Keyboard.KEY_C, CATG_MCTE);
    public static final KeyBinding keyPaste = new KeyBinding("mcte.editor.paste", Keyboard.KEY_V, CATG_MCTE);
    public static final KeyBinding keyFill = new KeyBinding("mcte.editor.fill", Keyboard.KEY_B, CATG_MCTE);
    public static final KeyBinding keyClear = new KeyBinding("mcte.editor.clear", Keyboard.KEY_N, CATG_MCTE);

    public static void init() {
        ClientRegistry.registerKeyBinding(keyEditMenu);
        ClientRegistry.registerKeyBinding(keyEditMode);
        ClientRegistry.registerKeyBinding(keyDelete);
        ClientRegistry.registerKeyBinding(keyUndo);
        ClientRegistry.registerKeyBinding(keyCut);
        ClientRegistry.registerKeyBinding(keyCopy);
        ClientRegistry.registerKeyBinding(keyPaste);
        ClientRegistry.registerKeyBinding(keyFill);
        ClientRegistry.registerKeyBinding(keyClear);
    }

    @SubscribeEvent
    public void keyDown(KeyInputEvent event) {
        EntityPlayer player = NGTUtil.getClientPlayer();

        if (keyEditMenu.isPressed()) {
            if (NGTUtil.isEquippedItem(player, MCTE.painter)) {
                player.openGui(MCTE.instance, MCTE.guiIdPainter, player.getEntityWorld(), 0, 0, 0);
            } else {
                this.sendKeyToServer(MCTE.KEY_EditMenu);
            }
        } else if (keyEditMode.isPressed()) {
            this.sendKeyToServer(MCTE.KEY_EditMode);
        } else if (keyUndo.isPressed()) {
            this.sendKeyToServer(MCTE.KEY_Undo);
        } else if (keyClear.isPressed()) {
            this.sendKeyToServer(MCTE.KEY_Clear);
        } else {
            //エディタがない場合はキーを無効に(競合防止)
            EntityEditor editor = this.getClientEditor(player);
            if (editor != null) {
                if (keyDelete.isPressed()) {
                    if (FilterManager.INSTANCE.execFilter(player, editor, "Delete")) {
                        NGTLog.sendChatMessage(player, "Delete Blocks");
                    }
                } else if (keyCut.isPressed()) {
                    if (FilterManager.INSTANCE.execFilter(player, editor, "Cut")) {
                        NGTLog.sendChatMessage(player, "Cut Blocks");
                    }
                } else if (keyCopy.isPressed()) {
                    if (FilterManager.INSTANCE.execFilter(player, editor, "Copy")) {
                        NGTLog.sendChatMessage(player, "Copy Blocks");
                    }
                } else if (keyPaste.isPressed()) {
                    if (FilterManager.INSTANCE.execFilter(player, editor, "Paste")) {
                        NGTLog.sendChatMessage(player, "Paste Blocks");
                    }
                } else if (keyFill.isPressed()) {
                    if (FilterManager.INSTANCE.execFilter(player, editor, "Fill")) {
                        NGTLog.sendChatMessage(player, "Fill Blocks");
                    }
                }
            }
        }
    }

    private void sendKeyToServer(byte keyCode) {
        EntityPlayer player = NGTUtil.getClientPlayer();
        MCTE.NETWORK_WRAPPER.sendToServer(new PacketMCTEKey(player, keyCode));
    }

    private EntityEditor getClientEditor(EntityPlayer player) {
        List<Entity> list = NGTUtilClient.getMinecraft().world.loadedEntityList;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) instanceof EntityEditor) {
                EntityEditor editor = (EntityEditor) list.get(i);
                if (player.equals(editor.getPlayer())) {
                    return editor;
                }
            }
        }
        return null;
    }
}