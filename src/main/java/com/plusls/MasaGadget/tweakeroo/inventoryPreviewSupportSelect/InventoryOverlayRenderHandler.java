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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

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

    public static void renderSelectedRect(int x, int y) {
        // 选中框
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(x, y, x + 16, y + 16, -2130706433, -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    public static void renderOrderedTooltip(ItemStack stack, int x, int y) {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Text> list = stack.getTooltip(mc.player, mc.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
        List<String> text = Lists.newArrayList();
        for (Text t : list) {
            text.add(t.asFormattedString());
        }
        if (text.isEmpty())
            return;
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        int i = 0;

        for (String string : text) {
            int j = mc.textRenderer.getStringWidth(string);
            if (j > i) {
                i = j;
            }
        }

        int k = x + 12;
        int l = y - 12;
        int n = 8;
        if (text.size() > 1) {
            n += 2 + (text.size() - 1) * 10;
        }

        if (k + i > GuiUtils.getScaledWindowWidth()) {
            k -= 28 + i;
        }

        if (l + n + 6 > GuiUtils.getScaledWindowHeight()) {
            l = GuiUtils.getScaledWindowHeight() - n - 6;
        }

        int o = -267386864;
        fillGradient(k - 3, l - 4, k + i + 3, l - 3, -267386864, -267386864);
        fillGradient(k - 3, l + n + 3, k + i + 3, l + n + 4, -267386864, -267386864);
        fillGradient(k - 3, l - 3, k + i + 3, l + n + 3, -267386864, -267386864);
        fillGradient(k - 4, l - 3, k - 3, l + n + 3, -267386864, -267386864);
        fillGradient(k + i + 3, l - 3, k + i + 4, l + n + 3, -267386864, -267386864);
        int p = 1347420415;
        int q = 1344798847;
        fillGradient(k - 3, l - 3 + 1, k - 3 + 1, l + n + 3 - 1, 1347420415, 1344798847);
        fillGradient(k + i + 2, l - 3 + 1, k + i + 3, l + n + 3 - 1, 1347420415, 1344798847);
        fillGradient(k - 3, l - 3, k + i + 3, l - 3 + 1, 1347420415, 1347420415);
        fillGradient(k - 3, l + n + 2, k + i + 3, l + n + 3, 1344798847, 1344798847);
        MatrixStack matrixStack = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrixStack.translate(0.0D, 0.0D, 300);
        Matrix4f matrix4f = matrixStack.peek().getModel();

        for (int r = 0; r < text.size(); ++r) {
            String string2 = (String) text.get(r);
            if (string2 != null) {
                mc.textRenderer.draw(string2, (float) k, (float) l, -1, true, matrix4f, immediate, false, 0, 15728880);
            }

            if (r == 0) {
                l += 2;
            }

            l += 10;
        }

        immediate.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableRescaleNormal();
    }

    protected static void fillGradient(int top, int left, int right, int bottom, int color1, int color2) {
        float f = (float) (color1 >> 24 & 255) / 255.0F;
        float g = (float) (color1 >> 16 & 255) / 255.0F;
        float h = (float) (color1 >> 8 & 255) / 255.0F;
        float i = (float) (color1 & 255) / 255.0F;
        float j = (float) (color2 >> 24 & 255) / 255.0F;
        float k = (float) (color2 >> 16 & 255) / 255.0F;
        float l = (float) (color2 >> 8 & 255) / 255.0F;
        float m = (float) (color2 & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(right, left, 300).color(g, h, i, f).next();
        bufferBuilder.vertex(top, left, 300).color(g, h, i, f).next();
        bufferBuilder.vertex(top, bottom, 300).color(k, l, m, j).next();
        bufferBuilder.vertex(right, bottom, 300).color(k, l, m, j).next();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public void render() {
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
                            renderSelectedRect(renderX, renderY);
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
                                            renderSelectedRect(subRenderX, subRenderY);
                                            renderOrderedTooltip(subItemStack, subRenderX, subRenderY + 8);
                                            RenderSystem.popMatrix();
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
                        renderSelectedRect(renderX, renderY);
                        renderOrderedTooltip(itemStack, renderX, renderY + 8);
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

}
