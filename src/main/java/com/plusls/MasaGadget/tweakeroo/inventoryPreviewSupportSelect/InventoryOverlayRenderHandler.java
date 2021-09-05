package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.plusls.MasaGadget.ModInfo;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryOverlayRenderHandler implements IRenderer {
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

    final public static InventoryOverlayRenderHandler instance = new InventoryOverlayRenderHandler();

    public void render(MatrixStack matrixStack) {
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
                                            RenderSystem.pushMatrix();
                                            RenderSystem.translated(0, 0, 400);
                                            // RenderSystem.applyModelViewMatrix(); TODO
                                            renderSelectedRect(matrixStack, subRenderX, subRenderY);
                                            renderOrderedTooltip(matrixStack, subItemStack, subRenderX, subRenderY + 8);
                                            RenderSystem.pushMatrix();
                                            // RenderSystem.applyModelViewMatrix(); TODO

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

    public void renderSelectedRect(MatrixStack matrices, int x, int y) {
        // 选中框
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(matrices, x, y, x + 16, y + 16, -2130706433, -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    public void renderOrderedTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<OrderedText> lines = Lists.transform(stack.getTooltip(mc.player, mc.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL), Text::asOrderedText);
        if (lines.isEmpty())
            return;
        int k = 0;
        for (OrderedText lv : lines) {
            int l = mc.textRenderer.getWidth(lv);
            if (l > k)
                k = l;
        }
        int m = x + 12;
        int n = y - 12;
        int o = k;
        int p = 8;
        if (lines.size() > 1)
            p += 2 + (lines.size() - 1) * 10;
        if (m + k > GuiUtils.getScaledWindowWidth())
            m -= 28 + k;
        if (n + p + 6 > GuiUtils.getScaledWindowHeight())
            n = GuiUtils.getScaledWindowHeight() - p - 6;
        matrices.push();
        int q = -267386864;
        int r = 1347420415;
        int s = 1344798847;
        int t = 400;
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        lv3.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f lv4 = matrices.peek().getModel();
        fillGradient(lv4, lv3, m - 3, n - 4, m + o + 3, n - 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, m - 3, n + p + 3, m + o + 3, n + p + 4, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, m - 3, n - 3, m + o + 3, n + p + 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, m - 4, n - 3, m - 3, n + p + 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, m + o + 3, n - 3, m + o + 4, n + p + 3, 400, -267386864, -267386864);
        fillGradient(lv4, lv3, m - 3, n - 3 + 1, m - 3 + 1, n + p + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(lv4, lv3, m + o + 2, n - 3 + 1, m + o + 3, n + p + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(lv4, lv3, m - 3, n - 3, m + o + 3, n - 3 + 1, 400, 1347420415, 1347420415);
        fillGradient(lv4, lv3, m - 3, n + p + 2, m + o + 3, n + p + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        lv3.end();
        BufferRenderer.draw(lv3);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate lv5 = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0D, 0.0D, 400.0D);
        for (int u = 0; u < lines.size(); u++) {
            OrderedText lv6 = lines.get(u);
            if (lv6 != null)
                mc.textRenderer.draw(lv6, m, n, -1, true, lv4, (VertexConsumerProvider) lv5, false, 0, 15728880);
            if (u == 0)
                n += 2;
            n += 10;
        }
        lv5.draw();
        matrices.pop();
    }

    protected void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd, 0);
    }

    protected static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), lv2, startX, startY, endX, endY, 0, colorStart, colorEnd);
        lv.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
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
        arg2.vertex(matrix, endX, startY, z).color(g, h, p, f).next();
        arg2.vertex(matrix, startX, startY, z).color(g, h, p, f).next();
        arg2.vertex(matrix, startX, endY, z).color(r, s, t, q).next();
        arg2.vertex(matrix, endX, endY, z).color(r, s, t, q).next();
    }
}
