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
//#endif

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

//#if MC > 11605
//$$ import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//#endif

//#if MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.MultiBufferSource;
//#endif

public class InventoryOverlayRenderHandler {
    @Getter(lazy = true)
    private static final InventoryOverlayRenderHandler instance = new InventoryOverlayRenderHandler();
    private static final int UN_SELECTED = 114514;

    private int selectedIdx = InventoryOverlayRenderHandler.UN_SELECTED;
    private int currentIdx = -1;
    private int renderX = -1;
    private int renderY = -1;
    private ItemStack itemStack = null;
    // 支持显示盒子在箱子里
    private boolean selectInventory = false;
    private boolean renderingSubInventory = false;
    private int subSelectedIdx = UN_SELECTED;
    private int subCurrentIdx = -1;
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
            InventoryOverlayRenderHandler.getInstance().resetSelectedIdx();
        }
    }

    public void render(@NotNull RenderContext renderContext) {
        //#if MC > 11904
        //$$ GuiGraphics gui = renderContext.getGuiComponent();
        //#elseif MC > 11502
        //#endif

        //#if MC > 11605 && MC < 12000
        //$$ RenderSystem.applyModelViewMatrix();
        //#endif

        if (this.currentIdx == 0) {
            return;
        }

        if (this.selectedIdx != InventoryOverlayRenderHandler.UN_SELECTED) {
            if (this.selectedIdx >= this.currentIdx) {
                this.selectedIdx %= this.currentIdx;
            } else if (this.selectedIdx < 0) {
                while (this.selectedIdx < 0) {
                    this.selectedIdx += this.currentIdx;
                }
            } else if (this.itemStack != null) {
                if (this.selectInventory) {
                    if (this.itemStack.getItem() instanceof BlockItem &&
                            ((BlockItem) this.itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
                        this.renderSelectedRect(renderContext, this.renderX, this.renderY);
                        // 盒子预览
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

                        if (this.subSelectedIdx != InventoryOverlayRenderHandler.UN_SELECTED) {
                            if (this.subCurrentIdx != 0) {
                                if (this.subSelectedIdx >= this.subCurrentIdx) {
                                    this.subSelectedIdx %= this.subCurrentIdx;
                                } else if (this.subSelectedIdx < 0) {
                                    while (this.subSelectedIdx < 0) {
                                        this.subSelectedIdx += this.subCurrentIdx;
                                    }
                                } else if (this.subItemStack != null) {
                                    this.renderSelectedRect(renderContext, this.renderX, this.renderY);
                                }
                            }
                        }
                    } else {
                        // 激活预览但是被预览的物品不是盒子
                        this.switchSelectInventory();
                    }
                }

                if (!this.selectInventory) {
                    this.renderSelectedRect(renderContext, this.renderX, this.renderY);
                    this.renderTooltip(renderContext, this.itemStack, this.renderX, this.renderY);
                }
            }
        }

        this.currentIdx = 0;
        this.itemStack = null;
        this.renderX = -1;
        this.renderY = -1;
        this.subCurrentIdx = 0;
        this.subItemStack = null;
        this.subRenderX = -1;
        this.subRenderY = -1;
    }

    // for 1.14
    public void updateState(int x, int y, ItemStack stack) {
        if (this.renderingSubInventory) {
            if (this.subCurrentIdx++ == this.subSelectedIdx) {
                this.subRenderX = x;
                this.subRenderY = y;
                this.subItemStack = stack;
            }
        } else {
            if (this.currentIdx++ == this.selectedIdx) {
                this.renderX = x;
                this.renderY = y;
                this.itemStack = stack;
            }
        }
    }

    public void switchSelectInventory() {
        this.selectInventory = !this.selectInventory;
        this.subSelectedIdx = InventoryOverlayRenderHandler.UN_SELECTED;
    }

    public void resetSelectedIdx() {
        this.selectedIdx = InventoryOverlayRenderHandler.UN_SELECTED;

        if (this.selectInventory) {
            this.switchSelectInventory();
        }
    }

    public void addSelectedIdx(int n) {
        if (this.selectInventory) {
            if (this.subSelectedIdx == InventoryOverlayRenderHandler.UN_SELECTED) {
                this.subSelectedIdx = 0;
            } else {
                this.subSelectedIdx += n;
            }
        } else {
            if (this.selectedIdx == InventoryOverlayRenderHandler.UN_SELECTED) {
                this.selectedIdx = 0;
            } else {
                this.selectedIdx += n;
            }
        }
    }

    public void renderSelectedRect(RenderContext renderContext, int x, int y) {
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
        RenderGlobal.disableLighting();
        RenderGlobal.disableDepthTest();
        RenderGlobal.colorMask(true, true, true, false);
        renderContext.pushMatrix();
        renderContext.translate(x, y, 1);
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
        RenderGlobal.enableLighting();
        RenderGlobal.enableDepthTest();
        //#endif
    }

    private void renderTooltip(RenderContext renderContext, ItemStack itemStack, int x, int y) {
        //#if MC > 11904
        //$$ renderContext.getGuiComponent().renderTooltip(Minecraft.getInstance().font, itemStack, x, y);
        //#else
        Minecraft mc = Minecraft.getInstance();
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
        renderContext.translate(0, 0, 1);

        //#if MC < 11904
        float backupBlitOffset = mc.getItemRenderer().blitOffset;
        mc.getItemRenderer().blitOffset = 400.0F;
        RenderGlobal.disableTexture();
        //#endif

        RenderGlobal.enableBlend();
        RenderGlobal.defaultBlendFunc();

        AccessorGuiComponent guiComponent = (AccessorGuiComponent) renderContext.getGuiComponent();
        guiComponent.masa_gadget_mod$fillGradient(
        //#if MC > 11502
                renderContext.getMatrixStack().getPoseStack(),
        //#endif
                renderX - 3,
                renderY - 4,
                renderX + xOffset + 3,
                renderY - 3, 0xF0100010,
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
        RenderGlobal.disableBlend();

        //#if MC < 11904
        RenderGlobal.enableTexture();
        //#endif

        renderContext.translate(0, 0, 1);
        FontCompat fontCompat = FontCompat.of(mc.font);

        for (int i = 0; i < tooltipLines.size(); i++) {
            fontCompat.drawInBatch(
                    tooltipLines.get(i),
                    renderX,
                    renderY,
                    0xFFFFFFFF,
                    true,
        //#if MC > 11404
                    new PoseStack().last().pose(),
                    MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()),
        //#endif
                    FontCompat.DisplayMode.NORMAL,
                    0,
                    0xF000F0
            );
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
