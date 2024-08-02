package com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.game.Configs;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.render.context.RenderContext;

//#if MC < 12000
import com.plusls.MasaGadget.mixin.accessor.AccessorGuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import top.hendrixshen.magiclib.api.compat.minecraft.client.gui.FontCompat;
import top.hendrixshen.magiclib.impl.render.context.RenderGlobal;

import java.util.List;
//#if MC > 11605
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
//#if MC > 11404
import net.minecraft.client.renderer.MultiBufferSource;
import top.hendrixshen.magiclib.util.minecraft.render.RenderUtil;
//#endif
//#endif

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.screens.Screen;
//#endif

//#if MC > 11605
//$$ import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//#endif

//#if MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class InventoryOverlayRenderHandler {
    @Getter(lazy = true)
    private static final InventoryOverlayRenderHandler instance = new InventoryOverlayRenderHandler();
    private static final int UN_SELECTED = 114514;

    // Main Container
    private int selectedSlot = InventoryOverlayRenderHandler.UN_SELECTED;
    private int currentSlot = -1;
    private int renderX = -1;
    private int renderY = -1;
    private ItemStack itemStack = null;
    // Internal ShulkerBox
    private boolean selectInventory = false;
    private boolean renderingSubInventory = false;
    private int subSelectedSlot = InventoryOverlayRenderHandler.UN_SELECTED;
    private int subCurrentSlot = -1;
    private int subRenderX = -1;
    private int subRenderY = -1;
    private ItemStack subItemStack = null;

    public static void onHitCallback(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged) {
        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                !Configs.inventoryPreviewSupportSelect.getBooleanValue()) {
            return;
        }

        if (!oldStatus) {
            // Reset preview selection slot.
            InventoryOverlayRenderHandler.getInstance().resetSelectedSlot();
        }
    }

    public void render(@NotNull RenderContext renderContext) {
        //#if MC > 11605 && MC < 12000
        //$$ RenderSystem.applyModelViewMatrix();
        //#endif

        if (this.currentSlot == 0) {
            return;
        }

        if (this.selectedSlot != InventoryOverlayRenderHandler.UN_SELECTED &&
                this.adjustSelectedSlot() &&
                this.itemStack != null) {
            this.attachToSubShulkerBoxView(renderContext);
            this.attachToMainInventoryView(renderContext);
        }

        this.dropState();
    }

    private void attachToMainInventoryView(RenderContext renderContext) {
        if (!this.selectInventory) {
            this.renderSlotHighlight(renderContext, this.renderX, this.renderY);
            this.renderTooltip(renderContext, this.itemStack, this.renderX, this.renderY);
        }
    }

    private void attachToSubShulkerBoxView(RenderContext renderContext) {
        if (!this.selectInventory) {
            return;
        }

        if (!(this.itemStack.getItem() instanceof BlockItem) ||
                !(((BlockItem) this.itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
            this.switchSelectInventory();
            return;
        }

        //#if MC > 11904
        //$$ GuiGraphics gui = renderContext.getGuiComponent();
        //#endif

        this.renderSlotHighlight(renderContext, this.renderX, this.renderY);
        this.renderingSubInventory = true;
        RenderUtils.renderShulkerBoxPreview(this.itemStack,
                GuiUtils.getScaledWindowWidth() / 2 - 96,
                GuiUtils.getScaledWindowHeight() / 2 + 30,
                true
                //#if MC > 11904
                //$$ , gui
                //#endif
        );
        this.renderingSubInventory = false;

        if (this.subSelectedSlot != InventoryOverlayRenderHandler.UN_SELECTED &&
                this.adjustSubSelectedSlot() &&
                this.subItemStack != null
        ) {
            renderContext.pushMatrix();
            renderContext.translate(0, 0, 400);
            this.renderSlotHighlight(renderContext, this.subRenderX, this.subRenderY);
            this.renderTooltip(renderContext, this.subItemStack, this.subRenderX, this.subRenderY);
            renderContext.popMatrix();
        }
    }

    private boolean adjustSelectedSlot() {
        int oldSelectedSlot = this.selectedSlot;

        if (this.currentSlot > 0) {
            while (this.selectedSlot < 0) {
                this.selectedSlot += this.currentSlot;
            }

            this.selectedSlot %= this.currentSlot;
        }

        return oldSelectedSlot == this.selectedSlot;
    }

    private boolean adjustSubSelectedSlot() {
        int oldSelectedSlot = this.subSelectedSlot;

        if (this.subCurrentSlot > 0) {
            while (this.subSelectedSlot < 0) {
                this.subSelectedSlot += this.subCurrentSlot;
            }

            this.subSelectedSlot %= this.subCurrentSlot;
        }

        return oldSelectedSlot == this.subSelectedSlot;
    }

    public void updateState(int x, int y, ItemStack stack) {
        if (this.renderingSubInventory) {
            if (this.subCurrentSlot++ == this.subSelectedSlot) {
                this.subRenderX = x;
                this.subRenderY = y;
                this.subItemStack = stack;
            }
        } else {
            if (this.currentSlot++ == this.selectedSlot) {
                this.renderX = x;
                this.renderY = y;
                this.itemStack = stack;
            }
        }
    }

    private void dropState() {
        this.currentSlot = 0;
        this.itemStack = null;
        this.renderX = 0;
        this.renderY = 0;
        this.subCurrentSlot = 0;
        this.subItemStack = null;
        this.subRenderX = 0;
        this.subRenderY = 0;
    }

    public void switchSelectInventory() {
        this.selectInventory = !this.selectInventory;
        this.subSelectedSlot = InventoryOverlayRenderHandler.UN_SELECTED;
    }

    private void resetSelectedSlot() {
        this.selectedSlot = InventoryOverlayRenderHandler.UN_SELECTED;

        if (this.selectInventory) {
            this.switchSelectInventory();
        }
    }

    public void scrollerUp() {
        this.moveSelectedSlot(1);
    }

    public void scrollerDown() {
        this.moveSelectedSlot(-1);
    }

    private void moveSelectedSlot(int n) {
        if (this.selectInventory) {
            if (this.subSelectedSlot == InventoryOverlayRenderHandler.UN_SELECTED) {
                this.subSelectedSlot = 0;
            } else {
                this.subSelectedSlot += n;
            }
        } else {
            if (this.selectedSlot == InventoryOverlayRenderHandler.UN_SELECTED) {
                this.selectedSlot = 0;
            } else {
                this.selectedSlot += n;
            }
        }
    }

    private void renderSlotHighlight(@NotNull RenderContext renderContext, int x, int y) {
        //#if MC > 11605
        //$$ AbstractContainerScreen.renderSlotHighlight(
        //#if MC > 11904
        //$$         renderContext.getGuiComponent(),
        //#else
        //$$         renderContext.getMatrixStack().getPoseStack(),
        //#endif
        //$$         x,
        //$$         y,
        //$$         400
        //$$ );
        //#else
        RenderGlobal.disableDepthTest();
        RenderGlobal.colorMask(true, true, true, false);
        renderContext.pushMatrix();
        renderContext.translate(0, 0, 400);
        ((AccessorGuiComponent) renderContext.getGuiComponent()).masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                x,
                y,
                x + 16,
                y + 16,
                0x80FFFFFF,
                0x80FFFFFF
        );
        renderContext.popMatrix();
        RenderGlobal.colorMask(true, true, true, true);
        RenderGlobal.enableDepthTest();
        //#endif
    }

    private void renderTooltip(RenderContext renderContext, @NotNull ItemStack itemStack, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        //#if MC > 11904
        //$$ renderContext.getGuiComponent().renderTooltip(mc.font, Screen.getTooltipFromItem(mc, itemStack), itemStack.getTooltipImage(), x, y);
        //#else
        List<Component> tooltipLines = itemStack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ?
                TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);

        if (tooltipLines.isEmpty()) {
            return;
        }

        int xOffset = 0;
        int yOffset = tooltipLines.size() == 1 ? -2 : 0;

        for (Component line : tooltipLines) {
            int lineWidth = FontCompat.of(Minecraft.getInstance().font).width(line);

            if (lineWidth > xOffset) {
                xOffset = lineWidth;
            }

            yOffset += 10;
        }

        int renderX = x + 12;
        int renderY = y - 12;

        if (renderX + xOffset > GuiUtils.getScaledWindowWidth()) {
            renderX -= 28 + xOffset;
        }

        if (renderY + yOffset + 6 > GuiUtils.getScaledWindowHeight()) {
            renderY = GuiUtils.getScaledWindowHeight() - yOffset - 6;
        }

        renderContext.pushMatrix();
        renderContext.translate(0, 0, 400);

        //#if MC < 11904
        float backupBlitOffset = mc.getItemRenderer().blitOffset;
        mc.getItemRenderer().blitOffset = 400.0F;
        //#endif

        AccessorGuiComponent guiComponent = (AccessorGuiComponent) renderContext.getGuiComponent();
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 3,
                renderY - 4,
                renderX + xOffset + 3,
                renderY - 3,
                0xF0100010,
                0xF0100010
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 3,
                renderY + yOffset + 3,
                renderX + xOffset + 3,
                renderY + yOffset + 4,
                0xF0100010,
                0xF0100010
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 3,
                renderY - 3,
                renderX + xOffset + 3,
                renderY + yOffset + 3,
                0xF0100010,
                0xF0100010
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 4,
                renderY - 3,
                renderX - xOffset,
                renderY + yOffset + 3,
                0xF0100010,
                0xF0100010
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX + xOffset + 3,
                renderY - 3,
                renderX + xOffset + 4,
                renderY + yOffset + 3,
                0xF0100010,
                0xF0100010
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 3,
                renderY - 3 + 1,
                renderX - 3 + 1,
                renderY + yOffset + 3 - 1,
                0x505000FF,
                0x5028007F
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX + xOffset + 2,
                renderY - 3 + 1,
                renderX + xOffset + 3,
                renderY + yOffset + 3 - 1,
                0x505000FF,
                0x5028007F
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 3,
                renderY - 3,
                renderX + xOffset + 3,
                renderY - 3 + 1,
                0x505000FF,
                0x505000FF
        );
        guiComponent.masa_gadget_mod$fillGradient(
                //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
                //#endif
                renderX - 3,
                renderY + yOffset + 2,
                renderX + xOffset + 3,
                renderY + yOffset + 3,
                0x5028007F,
                0x5028007F
        );

        renderContext.translate(0, 0, 1);
        FontCompat fontCompat = FontCompat.of(mc.font);

        for (int i = 0; i < tooltipLines.size(); i++) {
            //#if MC > 11404
            MultiBufferSource.BufferSource immediate = RenderUtil.getBufferSource();
            //#endif
            fontCompat.drawInBatch(
                    tooltipLines.get(i),
                    renderX,
                    renderY,
                    0xFFFFFFFF,
                    true,
                    //#if MC > 11404
                    new PoseStack().last().pose(),
                    immediate,
                    //#endif
                    FontCompat.DisplayMode.NORMAL,
                    0,
                    0xF000F0
            );
            //#if MC > 11404
            immediate.endBatch();
            //#endif
            renderY += 10 + ((i == 0) ? 2 : 0);
        }

        renderContext.popMatrix();
        //#if MC < 11904
        mc.getItemRenderer().blitOffset = backupBlitOffset;
        //#endif
        RenderGlobal.enableDepthTest();
        //#endif
    }
}
