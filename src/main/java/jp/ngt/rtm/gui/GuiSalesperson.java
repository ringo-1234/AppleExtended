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

package jp.ngt.rtm.gui;

import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMItem.MoneyType;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.entity.npc.Menu;
import jp.ngt.rtm.entity.npc.MenuEntry;
import jp.ngt.rtm.entity.npc.Role;
import jp.ngt.rtm.network.PacketSyncItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public final class GuiSalesperson extends GuiNPC {
    private final int MIN_SLOT_INDEX = 4 * 9 + 4;
    private final int MENU_BUTTON_ROW = 7;

    protected final boolean isOwner;
    protected final Menu menu;

    private MenuEntry selectedMenu;
    private int amountMoney = 0;
    private boolean showMenu = false;
    private int pageIndex = 0;
    private int maxPage = 0;
    private int selectedRow = 0;
    private String message = "";

    private final GuiButton[] delButtons = new GuiButton[MENU_BUTTON_ROW];
    private final MenuEntry[] menuItems = new MenuEntry[MENU_BUTTON_ROW];
    private final GuiButton[] decButtons = new GuiButton[MENU_BUTTON_ROW];
    private final GuiButton[] addButtons = new GuiButton[MENU_BUTTON_ROW];
    private final int[] itemCounts = new int[MENU_BUTTON_ROW];

    public GuiSalesperson(EntityPlayer par1, EntityNPC par2) {
        super(par1, par2);

        this.isOwner = (par1.equals(par2.getOwner()));
        this.menu = new Menu(par2.getMenu(), par2.getRole());
        this.selectedMenu = this.menu.get(0);
        this.message = I18n.format("gui.npc.put_money");
    }

    @Override
    public void initGui() {
        super.initGui();

        if (!this.showMenu) {
            this.buttonList.add(new GuiButtonItem(200, this.guiLeft + 90, this.guiTop + 26, 20, 20));
            String s = (this.npc.getRole() == Role.BUYER) ? "gui.npc.sell" : "gui.npc.buy";
            this.buttonList.add(new GuiButton(201, this.guiLeft + 85, this.guiTop + 49, 40, 20, I18n.format(s)));
            GuiButton button = new GuiButton(202, this.guiLeft + 130, this.guiTop + 49, 40, 20, I18n.format("gui.npc.register"));
            button.enabled = this.isOwner;
            this.buttonList.add(button);

            if (this.npc.getRole() == Role.SALESPERSON) {
                this.message = I18n.format("gui.npc.put_money");
            } else if (this.npc.getRole() == Role.BUYER) {
                this.message = I18n.format("gui.npc.put_item");
            }
        } else {
            this.buttonList.clear();

            this.buttonList.add(new GuiButton(300, this.guiLeft + (this.xSize / 2) - 20, this.guiTop + this.ySize - 25, 40, 20, I18n.format("gui.npc.close")));
            this.buttonList.add(new GuiButton(301, this.guiLeft + this.xSize - 70, this.guiTop + 5, 20, 20, "<"));
            this.buttonList.add(new GuiButton(302, this.guiLeft + this.xSize - 25, this.guiTop + 5, 20, 20, ">"));
            GuiButton bImport = new GuiButton(303, this.guiLeft + 5, this.guiTop + 5, 40, 20, I18n.format("gui.npc.import"));
            GuiButton bExport = new GuiButton(304, this.guiLeft + 50, this.guiTop + 5, 40, 20, I18n.format("gui.npc.export"));
            bImport.enabled = this.isOwner;
            bExport.enabled = this.isOwner;
            this.buttonList.add(bImport);
            this.buttonList.add(bExport);

            List<MenuEntry> list = this.menu.getList();
            for (int i = 0; i < MENU_BUTTON_ROW; ++i) {
                int menuIndex = this.pageIndex * MENU_BUTTON_ROW + i;
                if (menuIndex < list.size()) {
                    int y = this.guiTop + 30 + i * 24;
                    this.delButtons[i] = new GuiButton(1000 + i, this.guiLeft + 5, y, 20, 20, "X");
                    this.buttonList.add(this.delButtons[i]);
                    this.menuItems[i] = list.get(menuIndex);
                    this.decButtons[i] = new GuiButton(2000 + i, this.guiLeft + this.xSize - 60, y, 20, 20, "-");
                    this.buttonList.add(this.decButtons[i]);
                    this.addButtons[i] = new GuiButton(3000 + i, this.guiLeft + this.xSize - 25, y, 20, 20, "+");
                    this.buttonList.add(this.addButtons[i]);
                } else {
                    this.delButtons[i] = null;
                    this.menuItems[i] = null;
                    this.decButtons[i] = null;
                    this.addButtons[i] = null;
                }
                this.itemCounts[i] = 0;
            }

            this.maxPage = (this.menu.getList().size() - 1) / MENU_BUTTON_ROW;
            this.selectRow(0);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        if (!this.showMenu) {
            if (button.id == 200)//お品書き
            {
                this.setPage(0);
            } else if (button.id == 201)//買うor売る
            {
                if (this.npc.getRole() == Role.BUYER) {
                    if (!this.sell()) {
                        this.message = I18n.format("gui.npc.can_not_sell");
                    }
                } else if (this.npc.getRole() == Role.SALESPERSON) {
                    if (!this.buy()) {
                        this.message = I18n.format("gui.npc.can_not_buy");
                    }
                }
            } else if (button.id == 202)//登録
            {
                String s;
                switch (this.registerItem()) {
                    case 0:
                        s = "gui.npc.reg_successful";
                        break;
                    case 1:
                        s = "gui.npc.reg_no_item";
                        break;
                    case 2:
                        s = "gui.npc.reg_no_money";
                        break;
                    case 3:
                        s = "gui.npc.reg_invalid_size";
                        break;
                    default:
                        s = "gui.npc.reg_failed";
                        break;
                }
                this.message = I18n.format(s);
            }
        } else {
            if (button.id == 300)//close
            {
                MenuEntry entry = this.menuItems[this.selectedRow];
                int count = this.itemCounts[this.selectedRow];
                if (count > 0) {
                    ItemStack item = entry.item.copy();
                    item.setCount(item.getCount() * count);
                    this.selectedMenu = new MenuEntry(item, entry.price * count);
                }
                this.setPage(-1);
            } else if (button.id == 301)//ページ-
            {
                if (this.pageIndex >= 1) {
                    this.setPage(--this.pageIndex);
                }
            } else if (button.id == 302)//ページ+
            {
                if (this.pageIndex < this.maxPage) {
                    this.setPage(++this.pageIndex);
                }
            } else if (button.id == 303)//import
            {
                this.menu.importFromText(this.npc.getRole());
                this.setPage(this.pageIndex);
            } else if (button.id == 304)//export
            {
                this.menu.exportToText();
            } else if (button.id >= 1000) {
                int menuButtonType = button.id / 1000;
                int menuIndex = button.id % 1000;
                if (menuButtonType == 1)//del
                {
                    int idx = this.pageIndex * MENU_BUTTON_ROW + menuIndex;
                    this.menu.remove(idx);
                    this.setPage(this.pageIndex);
                } else if (menuButtonType == 2)//-
                {
                    int count = this.itemCounts[menuIndex];
                    if (count > 0) {
                        this.itemCounts[menuIndex] = --count;
                        this.selectRow(menuIndex);
                    }
                } else if (menuButtonType == 3)//+
                {
                    int count = this.itemCounts[menuIndex];
                    if (count < this.menuItems[menuIndex].maxCount) {
                        this.itemCounts[menuIndex] = ++count;
                        this.selectRow(menuIndex);
                    }
                }
            }
        }
    }

    private void setPage(int page) {
        if (page < 0) {
            this.showMenu = false;
        } else {
            this.pageIndex = page;
            this.showMenu = true;
        }
        this.initGui();
    }

    private void selectRow(int index) {
        this.selectedRow = index;
        boolean notSelected = (this.itemCounts[this.selectedRow] <= 0);

        for (int i = 0; i < MENU_BUTTON_ROW; ++i) {
            if (this.delButtons[i] == null) {
                break;
            }

            boolean isSelected = (this.selectedRow == i) || notSelected;
            this.delButtons[i].enabled = this.isOwner;
            this.addButtons[i].enabled = (this.itemCounts[i] < this.menuItems[i].maxCount) && isSelected;
            this.decButtons[i].enabled = (this.itemCounts[i] > 0) && isSelected;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            this.npc.setMenu(this.menu.toString());
            PacketNBT.sendToServer(this.npc);
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!this.showMenu) {
            super.drawScreen(mouseX, mouseY, partialTicks);

            int dispMoney = 0;
            if (this.npc.getRole() == Role.SALESPERSON) {
                dispMoney = this.selectedMenu.price - this.amountMoney;

            } else if (this.npc.getRole() == Role.BUYER) {
                dispMoney = this.amountMoney;
            }
            this.fontRenderer.drawString(String.format("￥%d", dispMoney), this.guiLeft + 115, this.guiTop + 32, 0x000000);
            this.fontRenderer.drawString(this.message, this.guiLeft + 83, this.guiTop + 72, 0xFF0000);
        } else {
            this.drawDefaultBackground();

            int x0 = (this.width - this.xSize) / 2;
            int y0 = (this.height - this.ySize) / 2;
            this.drawGradientRect(x0, y0, x0 + this.xSize, y0 + this.ySize, 0xFFE0E0E0, 0xFFE0E0E0);

            for (int i = 0; i < this.buttonList.size(); ++i) {
                ((GuiButton) this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            this.fontRenderer.drawString(String.format("%d/%d", this.pageIndex, this.maxPage),
                    this.guiLeft + this.xSize - 45, this.guiTop + 10, 0x000000);

            for (int i = 0; i < MENU_BUTTON_ROW; ++i) {
                if (this.menuItems[i] == null) {
                    break;
                }

                int y = this.guiTop + 35 + i * 24;

                MenuEntry entry = this.menuItems[i];
                drawItem(this.mc, entry.item, this.guiLeft + 27, y - 3);
                this.fontRenderer.drawString(entry.item.getDisplayName(), this.guiLeft + 50, y - 5, 0x000000);
                this.fontRenderer.drawString(String.format("￥%d", entry.price), this.guiLeft + 50, y + 5, 0x000000);
                this.fontRenderer.drawString(String.format("%d", this.itemCounts[i]), this.guiLeft + this.xSize - 35, y, 0x000000);
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slot, slotId, mouseButton, type);

        if (this.npc.getRole() == Role.SALESPERSON) {
            this.amountMoney = this.countMoney(false);
            if (this.amountMoney > 0) {
                this.message = I18n.format("gui.npc.push_buy");
            } else {
                this.message = I18n.format("gui.npc.put_money");
            }
        } else if (this.npc.getRole() == Role.BUYER) {
            this.amountMoney = this.countSaleItemPayment();
            if (this.amountMoney > 0) {
                this.message = I18n.format("gui.npc.push_sell");
            } else if (this.amountMoney == 0) {
                this.message = I18n.format("gui.npc.put_item");
            } else {
                this.message = I18n.format("gui.npc.can_not_sell");
            }
        }
    }

    private boolean buy() {
        int payment = this.countMoney(false);
        if (payment < this.selectedMenu.price) {
            return false;//料金不足
        }
        this.countMoney(true);//支払い処理

        payment -= this.selectedMenu.price;

        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
            if (slot.slotNumber >= MIN_SLOT_INDEX && !slot.getHasStack()) {
                //slot.putStack(this.selectedMenu.item.copy());
                this.changeSlot(this.selectedMenu.item.copy(), i);//商品渡し
                break;
            }
        }

        this.payback(payment);//お釣り返金
        return true;
    }

    private boolean sell() {
        if (this.amountMoney > 0) {
            for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
                Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
                if (slot.slotNumber >= MIN_SLOT_INDEX && slot.getHasStack()) {
                    this.changeSlot(ItemStack.EMPTY, i);//支払額計算はhandleMouseClick()でやってるので削除のみ実施
                }
            }
            this.payback(this.amountMoney);//料金支払い
            return true;
        }

        return false;
    }

    /**
     * @return 買取金額合計, 買取不可の場合-1
     *
     */
    private int countSaleItemPayment() {
        List<MenuEntry> list = this.menu.getList();
        int payment = 0;

        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
            if (slot.slotNumber >= MIN_SLOT_INDEX && slot.getHasStack()) {
                ItemStack item = slot.getStack();
                if (this.isMoney(item)) {
                    return -1;//インベントリにお金がある場合は取引しない
                } else {
                    int paymentTmp = 0;
                    for (MenuEntry entry : list) {
                        if (this.compareItemWithoutSize(item, entry.item) && entry.item.getCount() == 1) {
                            paymentTmp = entry.price * item.getCount();
                            break;
                        }
                    }

                    if (paymentTmp > 0) {
                        payment += paymentTmp;
                    } else {
                        return -1;//買取不可の商品がある場合は取引しない
                    }
                }
            }
        }

        return payment;
    }

    private boolean compareItemWithoutSize(ItemStack stack1, ItemStack stack2) {
        return (stack1.getItem() == stack2.getItem()) && (stack1.getItemDamage() == stack2.getItemDamage());
    }

    private void payback(int payment) {
        int paymentTmp = payment;

        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
            if (slot.slotNumber >= MIN_SLOT_INDEX && !slot.getHasStack()) {
                if (paymentTmp > 0) {
                    int maxId = MoneyType.values().length - 1;
                    for (int id = maxId; id >= 0; --id) {
                        int price = MoneyType.getPrice(id);
                        if (paymentTmp >= price) {
                            int count = paymentTmp / price;
                            paymentTmp -= count * price;
                            this.changeSlot(new ItemStack(RTMItem.money, count, id), i);
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * @return 0:成功, 1:アイテム未設定, 2:金額未設定, 3:アイテム数が1以上, -1:その他エラー
     *
     */
    private int registerItem() {
        ItemStack item = ((Slot) this.inventorySlots.inventorySlots.get(MIN_SLOT_INDEX)).getStack();
        int price = this.countMoney(false);
        if (item.isEmpty() || this.isMoney(item)) {
            return 1;
        } else if (price <= 0) {
            return 2;
        } else if (this.npc.getRole() == Role.BUYER && item.getCount() > 1)//買取の場合、StackSizeは1のみ
        {
            return 3;
        } else {
            return this.menu.add(new MenuEntry(item.copy(), price)) ? 0 : -1;
        }
    }

    private int countMoney(boolean pay) {
        int amount = 0;
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = this.inventorySlots.inventorySlots.get(i);
            if (slot.slotNumber >= MIN_SLOT_INDEX) {
                ItemStack item = slot.getStack();
                if (this.isMoney(item)) {
                    amount += MoneyType.getPrice(item.getItemDamage()) * item.getCount();

                    if (pay) {
                        this.changeSlot(ItemStack.EMPTY, i);
                    }
                }
            }
        }

        return amount;
    }

    private boolean isMoney(ItemStack item) {
        return (item.getItem() == RTMItem.money);
    }

    private void changeSlot(ItemStack item, int id) {
        ((Slot) this.inventorySlots.inventorySlots.get(id)).putStack(item);//同期前にslotを埋めておく
        RTMCore.NETWORK_WRAPPER.sendToServer(new PacketSyncItem(this.player, item, id));
        //this.mc.playerController.sendSlotPacket(item, id);
    }

    private static void drawItem(Minecraft mc, ItemStack item, int x, int y) {
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, item, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, item, x, y, null);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public class GuiButtonItem extends GuiButton {
        public GuiButtonItem(int buttonId, int x, int y, int widthIn, int heightIn) {
            super(buttonId, x, y, widthIn, heightIn, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            super.drawButton(mc, mouseX, mouseY, partialTicks);

            drawItem(mc, GuiSalesperson.this.selectedMenu.item, this.x + 2, this.y + 2);
        }
    }
}
