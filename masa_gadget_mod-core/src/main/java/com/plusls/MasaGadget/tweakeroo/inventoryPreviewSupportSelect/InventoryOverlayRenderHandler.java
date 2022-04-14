package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.plusls.MasaGadget.ModInfo;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryOverlayRenderHandler implements IRenderer {
    final public static InventoryOverlayRenderHandler instance = new InventoryOverlayRenderHandler();
    final private static int UN_SELECTED = 114514;
    private int selectedIdx = UN_SELECTED;
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

    protected static void fillGradient(PoseStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator lv = Tesselator.getInstance();
        BufferBuilder lv2 = lv.getBuilder();
        lv2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        fillGradient(matrices.last().pose(), lv2, startX, startY, endX, endY, z, colorStart, colorEnd);
        lv.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    protected static void fillGradient(Matrix4f matrix, BufferBuilder arg2, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float f = (colorStart >> 24 & 0xFF) / 255.0F;
        float g = (colorStart >> 16 & 0xFF) / 255.0F;
        float h = (colorStart >> 8 & 0xFF) / 255.0F;
        float p = (colorStart & 0xFF) / 255.0F;
        float q = (colorEnd >> 24 & 0xFF) / 255.0F;
        float r = (colorEnd >> 16 & 0xFF) / 255.0F;
        float s = (colorEnd >> 8 & 0xFF) / 255.0F;
        float t = (colorEnd & 0xFF) / 255.0F;
        arg2.vertex(matrix, endX, startY, z).color(g, h, p, f).endVertex();
        arg2.vertex(matrix, startX, startY, z).color(g, h, p, f).endVertex();
        arg2.vertex(matrix, startX, endY, z).color(r, s, t, q).endVertex();
        arg2.vertex(matrix, endX, endY, z).color(r, s, t, q).endVertex();
    }

    public void render(PoseStack matrixStack) {
        // fuck mojang
        // for 1.18
        // 不添加会渲染错误，不知道麻将哪里 pop 了没有 apply
        RenderSystem.applyModelViewMatrix();

        if (currentIdx == 0) {
            return;
        }
        if (selectedIdx != UN_SELECTED) {
            if (selectedIdx >= currentIdx) {
                selectedIdx %= currentIdx;
            } else if (selectedIdx < 0) {
                while (selectedIdx < 0) {
                    selectedIdx += currentIdx;
                }
            } else {
                if (itemStack != null) {
                    if (selectInventory) {
                        if (itemStack.getItem() instanceof BlockItem &&
                                ((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
                            renderSelectedRect(matrixStack, renderX, renderY);
                            // 盒子预览
                            renderingSubInventory = true;
                            RenderUtils.renderShulkerBoxPreview(itemStack,
                                    GuiUtils.getScaledWindowWidth() / 2 - 96,
                                    GuiUtils.getScaledWindowHeight() / 2 + 30, true);
                            renderingSubInventory = false;
                            if (subSelectedIdx != UN_SELECTED) {
                                if (subCurrentIdx != 0) {
                                    if (subSelectedIdx >= subCurrentIdx) {
                                        subSelectedIdx %= subCurrentIdx;
                                    } else if (subSelectedIdx < 0) {
                                        while (subSelectedIdx < 0) {
                                            subSelectedIdx += subCurrentIdx;
                                        }
                                    } else {
                                        if (subItemStack != null) {
                                            PoseStack stack = RenderSystem.getModelViewStack();
                                            stack.pushPose();
                                            stack.translate(0, 0, 400);
                                            RenderSystem.applyModelViewMatrix();
                                            ModInfo.LOGGER.debug("subRenderX: {} subRenderY: {}", subRenderX, subRenderY);
                                            renderSelectedRect(matrixStack, subRenderX, subRenderY);
                                            renderOrderedTooltip(matrixStack, subItemStack, subRenderX, subRenderY + 8);
                                            RenderSystem.getModelViewStack().popPose();
                                            RenderSystem.applyModelViewMatrix();

                                        } else {
                                            ModInfo.LOGGER.debug("InventoryOverlayRenderHandler sub wtf???");
                                        }
                                    }

                                }

                            }
                        } else {
                            // 激活预览但是被预览的物品不是盒子
                            switchSelectInventory();
                        }
                    }
                    if (!selectInventory) {
                        ModInfo.LOGGER.debug("renderX: {} renderY: {}", renderX, renderY);
                        renderSelectedRect(matrixStack, renderX, renderY);
                        renderOrderedTooltip(matrixStack, itemStack, renderX, renderY + 8);
                    }

                } else {
                    ModInfo.LOGGER.debug("InventoryOverlayRenderHandler wtf???");
                }
            }
        }
        currentIdx = 0;
        itemStack = null;
        renderX = -1;
        renderY = -1;

        subCurrentIdx = 0;
        subItemStack = null;
        subRenderX = -1;
        subRenderY = -1;
    }

    public void updateState(int x, int y, ItemStack stack) {
        if (renderingSubInventory) {
            if (subCurrentIdx++ == subSelectedIdx) {
                subRenderX = x;
                subRenderY = y;
                subItemStack = stack;
            }
        } else {
            if (currentIdx++ == selectedIdx) {
                renderX = x;
                renderY = y;
                itemStack = stack;
            }
        }
    }

    public void switchSelectInventory() {
        selectInventory = !selectInventory;
        subSelectedIdx = UN_SELECTED;
    }

    public void resetSelectedIdx() {
        selectedIdx = UN_SELECTED;
        if (selectInventory) {
            switchSelectInventory();
        }
    }

    public void addSelectedIdx(int n) {
        if (selectInventory) {
            if (subSelectedIdx == UN_SELECTED) {
                subSelectedIdx = 0;
            } else {
                subSelectedIdx += n;
            }
        } else {
            if (selectedIdx == UN_SELECTED) {
                selectedIdx = 0;
            } else {
                selectedIdx += n;
            }
        }
    }

    public void renderSelectedRect(PoseStack matrices, int x, int y) {
        // 选中框
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(matrices, x, y, x + 16, y + 16, -2130706433, -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    public void renderOrderedTooltip(PoseStack matrices, ItemStack stack, int x, int y) {
        y = y + 8;

        Minecraft mc = Minecraft.getInstance();
        List<FormattedCharSequence> lines = Lists.transform(stack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL), Component::getVisualOrderText);
        List<ClientTooltipComponent> components = lines.stream().map(ClientTooltipComponent::create).collect(Collectors.toList());
        if (components.isEmpty())
            return;
        int k = 0;
        int l = (components.size() == 1) ? -2 : 0;
        for (ClientTooltipComponent lv : components) {
            int m = lv.getWidth(mc.font);
            if (m > k)
                k = m;
            l += lv.getHeight();
        }
        int n = x + 12;
        int o = y - 12;
        int p = k;
        int q = l;
        if (n + k > GuiUtils.getScaledWindowWidth())
            n -= 28 + k;
        if (o + q + 6 > GuiUtils.getScaledWindowHeight())
            o = GuiUtils.getScaledWindowHeight() - q - 6;
        matrices.pushPose();

        int r = -267386864;
        int s = 1347420415;
        int t = 1344798847;
        int u = 400;
        float f = mc.getItemRenderer().blitOffset;
        mc.getItemRenderer().blitOffset = 400.0F;
        Tesselator lv2 = Tesselator.getInstance();
        BufferBuilder lv3 = lv2.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        lv3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f lv4 = matrices.last().pose();
        fillGradient(lv4, lv3, n - 3, o - 4, n + p + 3, o - 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, n - 3, o + q + 3, n + p + 3, o + q + 4, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, n - 3, o - 3, n + p + 3, o + q + 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, n - 4, o - 3, n - 3, o + q + 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, n + p + 3, o - 3, n + p + 4, o + q + 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, n - 3, o - 3 + 1, n - 3 + 1, o + q + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(lv4, lv3, n + p + 2, o - 3 + 1, n + p + 3, o + q + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(lv4, lv3, n - 3, o - 3, n + p + 3, o - 3 + 1, 400, 1347420415, 1347420415);
        fillGradient(lv4, lv3, n - 3, o + q + 2, n + p + 3, o + q + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        lv3.end();
        BufferUploader.end(lv3);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        MultiBufferSource.BufferSource lv5 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        matrices.translate(0.0D, 0.0D, 400.0D);
        int v = o;
        for (int w = 0; w < components.size(); w++) {
            ClientTooltipComponent lv6 = components.get(w);
            lv6.renderText(mc.font, n, v, lv4, lv5);
            v += lv6.getHeight() + ((w == 0) ? 2 : 0);
        }
        lv5.endBatch();
        matrices.popPose();
        v = o;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent lv7 = components.get(i);
            lv7.renderImage(mc.font, n, v, matrices, mc.getItemRenderer(), 400);
            v += lv7.getHeight() + ((i == 0) ? 2 : 0);
        }
        mc.getItemRenderer().blitOffset = f;
    }

    protected void fillGradient(PoseStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd, 0);
    }
}
