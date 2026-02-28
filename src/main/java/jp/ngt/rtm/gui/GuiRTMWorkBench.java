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

import jp.ngt.ngtlib.gui.GuiContainerCustom;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityTrainWorkBench;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiRTMWorkBench extends GuiContainerCustom implements IResourceSelector {
    private static final ResourceLocation tex_0 = new ResourceLocation("rtm", "textures/gui/workbench_0.png");
    private static final ResourceLocation tex_1 = new ResourceLocation("rtm", "textures/gui/workbench_1.png");
    private static final ResourceLocation tex_2 = new ResourceLocation("rtm", "textures/gui/workbench_2.png");
    private static final ResourceLocation tex_3 = new ResourceLocation("rtm", "textures/gui/workbench_3.png");

    private ResourceState<ModelSetRail> state = new ResourceState<>(RTMResource.RAIL, null);
    private TileEntityTrainWorkBench workBench;
    private GuiButton button;
    private GuiButton buttonSelect;
    private GuiTextField heightField;
    /**
     * 0:クラフト, 1:アイテム一覧, 2:レシピ
     */
    public int pageIndex;
    private ContainerRTMWorkBench containerWorkBench;
    private ContainerRecipe containerRecipe;

    /**
     * 0:通常, 1:レール用
     */
    protected int workbenchType;
    private int currentScroll;
    private int prevScroll;
    private boolean enabled = true;

    public GuiRTMWorkBench(InventoryPlayer inventory, World world, TileEntityTrainWorkBench par3, boolean par4) {
        super(new ContainerRTMWorkBench(inventory, world, par3, par4));
        this.drawTextBox = false;

        this.containerWorkBench = (ContainerRTMWorkBench) this.inventorySlots;
        this.containerRecipe = new ContainerRecipe(this, world, par3);
        this.workBench = par3;
        this.xSize = 176;
        this.ySize = 188;
        this.pageIndex = 0;
        this.workbenchType = par3.getBlockMetadata();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        //this.setButtons();
        this.setPage(0);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.enabled = true;
    }

    private void setButtons() {
        this.buttonList.add(new GuiButton(110, this.guiLeft + 100, this.guiTop + 72, 48, 20, "Crafting"));
        this.button = new GuiButton(111, this.guiLeft + 150, this.guiTop + 72, 20, 20, "?");
        this.buttonList.add(this.button);
    }

    private void setPage(int par1) {
        this.pageIndex = par1;
        this.enabled = false;
        this.buttonList.clear();
        this.textFields.clear();
        this.heightField = null;

        if (this.pageIndex == 0) {
            this.inventorySlots = this.containerWorkBench;
            this.setButtons();

            if (this.workbenchType == 1) {
                String h = String.valueOf(this.containerWorkBench.railHeight);
                this.heightField = this.setTextField(this.guiLeft + 100, this.guiTop + 100, 70, 20, h);
                this.setSelectButtonAndModelset(this.containerWorkBench.modelName);
            }
        } else if (this.pageIndex == 1) {
            this.inventorySlots = this.containerRecipe;
            this.button = new GuiButtonBack(112, this.guiLeft + 151, this.guiTop + 171);
            this.buttonList.add(this.button);
            this.containerRecipe.setCurrentPage(1);
        } else if (this.pageIndex == 2) {
            this.containerRecipe.setCurrentPage(2);
            this.button = null;
        }
    }

    private void setSelectButtonAndModelset(String name) {
        ModelSetRail model = ModelPackManager.INSTANCE.getResourceSet(RTMResource.RAIL, name);
        this.state.setResourceName(name);
        this.buttonSelect = new GuiButtonSelectModel(121, this.guiLeft + 8, this.guiTop + 126, this.state.getResourceSet(), name);
        for (int i = 0; i < this.buttonList.size(); ++i) {
            GuiButton button = (GuiButton) this.buttonList.get(i);
            if (button.id == 121) {
                this.buttonList.remove(i);
                break;
            }
        }
        this.buttonList.add(this.buttonSelect);
        this.sendRailProp();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 28, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        this.drawDefaultBackground();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        switch (this.pageIndex) {
            case 0:
                this.mc.getTextureManager().bindTexture(this.workbenchType == 1 ? tex_3 : tex_0);
                break;
            case 1:
                this.mc.getTextureManager().bindTexture(tex_1);
                break;
            case 2:
                this.mc.getTextureManager().bindTexture(tex_2);
                break;
        }
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        this.mc.getTextureManager().bindTexture(tex_0);

        if (this.pageIndex == 0) {
            if (this.workBench.getCraftingTime() > 0) {
                int i1 = (int) (22.0F * ((float) this.workBench.getCraftingTime() / (float) this.workBench.Max_CraftingTime));
                this.drawTexturedModalRect(k + 104, l + 38, 176, 0, i1, 15);
            }
        }

        if (this.workbenchType == 1 && this.pageIndex == 0) {
            this.heightField.drawTextBox();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 110) {
            ContainerRTMWorkBench container = (ContainerRTMWorkBench) this.inventorySlots;
            ItemStack stackInResult = container.getResultItem();
            ItemStack stackSample = container.getSampeItem();
            if (stackInResult.isEmpty() && !stackSample.isEmpty() && this.workBench.getCraftingTime() <= 0) {
                if (this.workbenchType == 1) {
                    this.sendRailProp();
                }
                this.workBench.startCrafting(this.containerWorkBench.thePlayer, true);
            }
        } else if (button.id == 111)//itemList
        {
            this.setPage(1);
        } else if (button.id == 112)//back_Craft
        {
            int i = (this.pageIndex > 0 ? this.pageIndex - 1 : 0);
            this.setPage(i);
        } else if (button.id == 121)//selectRail
        {
            this.mc.displayGuiScreen(new GuiSelectModel(this.workBench.getWorld(), this));
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        //ボタンクリックと同時にアイテムを持ってしまう問題を回避
        this.dragSplitting = (this.button != null && this.isMouseOveredButton(this.button, par1, par2));

        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        super.keyTyped(par1, par2);

        if (this.workbenchType == 1 && this.pageIndex == 0) {
            this.sendRailProp();
        }
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (this.enabled) {
            if (this.pageIndex == 0) {
                super.handleMouseClick(slot, slotId, mouseButton, type);
            } else if (this.pageIndex == 1 || this.pageIndex == 2) {
                if (slot != null) {
                    slotId = slot.slotNumber;
                    this.containerRecipe.onSlotClicked(slot);
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();

        if (scroll != 0 && this.inventorySlots == this.containerRecipe) {
            this.prevScroll = this.currentScroll;
            scroll = scroll > 0 ? 1 : (scroll < 0 ? -1 : 0);
            this.currentScroll -= scroll;

            if (this.currentScroll < 0) {
                this.currentScroll = 0;
            }


            if (this.currentScroll > ContainerRecipe.getMaxScroll()) {
                this.currentScroll = ContainerRecipe.getMaxScroll();
            }

            if (this.pageIndex == 1)//アイテム一覧
            {
                this.containerRecipe.setItemList(this.currentScroll);
            }
        }
    }

    private boolean isMouseOveredButton(GuiButton par1, int par2, int par3) {
        return par2 >= par1.x && par3 >= par1.y && par2 < par1.x + par1.width && par3 < par1.y + par1.height;
    }

    private void sendRailProp() {
        float f0;
        try {
            f0 = Float.valueOf(this.heightField.getText());
        } catch (NumberFormatException e) {
            f0 = 0.0625F;//NGTLog.debug("NFE:" + this.heightField.getText());
        }
        String name = this.state.getResourceSet().getConfig().getName();
        String s = "workbench," + name + "," + f0;
        RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, s, this.workBench));
        this.containerWorkBench.setRailProp(name, f0);
    }

    @Override
    public ResourceState<ModelSetRail> getResourceState() {
        return this.state;
    }

    @Override
    public void updateResourceState() {
        ;
    }

    @Override
    public int[] getSelectorPos() {
        return new int[]{0, -1, 0};
    }

    @Override
    public boolean closeGui(ResourceState par1) {
        this.setSelectButtonAndModelset(par1.getResourceName());//display()より先にしないとstateがリセットされる
        this.mc.displayGuiScreen(this);
        return false;
    }

    @SideOnly(Side.CLIENT)
    public class GuiButtonBack extends GuiButton {
        public GuiButtonBack(int par1, int par2, int par3) {
            super(par1, par2, par3, 18, 12, "");
        }

        @Override
        public void drawButton(Minecraft mc, int x, int y, float ptick) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(tex_0);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
                int k = this.getHoverState(this.hovered);
                k = (k == 0) ? 2 : k - 1;
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.x, this.y, 176, 30 + k * 12, this.width, this.height);
                this.mouseDragged(mc, x, y);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public class GuiButtonModeChange extends GuiButton {
        public GuiButtonModeChange(int par1, int par2, int par3) {
            super(par1, par2, par3, 16, 16, "");
        }

        @Override
        public void drawButton(Minecraft mc, int x, int y, float ptick) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(tex_0);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                int k = (GuiRTMWorkBench.this.pageIndex == 0) ? 0 : 16;
                this.drawTexturedModalRect(this.x, this.y, 240, k, this.width, this.height);
                this.mouseDragged(mc, x, y);
            }
        }
    }
}