package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.hendrixshen.magiclib.compat.minecraft.blaze3d.vertex.VertexFormatCompatApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//#if MC <= 11605
//$$ import org.lwjgl.opengl.GL11;
//#endif

public class InventoryOverlayRenderHandler {
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

    public static void onHitCallback(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged) {
        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() || !Configs.inventoryPreviewSupportSelect) {
            return;
        }
        if (!oldStatus) {
            // 重置预览选择槽
            InventoryOverlayRenderHandler.instance.resetSelectedIdx();
        }
    }


    private static void fillGradient(PoseStack matrices, Collection<GradientData> gradientDataCollection) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        //#if MC > 11605
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //#else
        //$$ RenderSystem.disableAlphaTest();
        //$$ RenderSystem.shadeModel(GL11.GL_SMOOTH);
        //#endif
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormatCompatApi.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (GradientData gradientData : gradientDataCollection) {
            fillGradient(matrices.last().pose(), bufferBuilder, gradientData);
        }
        tesselator.end();

        //#if MC <= 11605
        //$$ RenderSystem.shadeModel(GL11.GL_FLAT);
        //#endif

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, GradientData gradientData) {
        bufferBuilder.vertex(matrix, gradientData.endX, gradientData.startY, gradientData.z)
                .color(gradientData.colorStart.r, gradientData.colorStart.g, gradientData.colorStart.b, gradientData.colorStart.a).endVertex();
        bufferBuilder.vertex(matrix, gradientData.startX, gradientData.startY, gradientData.z)
                .color(gradientData.colorStart.r, gradientData.colorStart.g, gradientData.colorStart.b, gradientData.colorStart.a).endVertex();
        bufferBuilder.vertex(matrix, gradientData.startX, gradientData.endY, gradientData.z)
                .color(gradientData.colorEnd.r, gradientData.colorEnd.g, gradientData.colorEnd.b, gradientData.colorEnd.a).endVertex();
        bufferBuilder.vertex(matrix, gradientData.endX, gradientData.endY, gradientData.z)
                .color(gradientData.colorEnd.r, gradientData.colorEnd.g, gradientData.colorEnd.b, gradientData.colorEnd.a).endVertex();
    }

    public <T> void render(T obj) {
        PoseStack matrixStack = (PoseStack) obj;
        // fuck mojang
        // for 1.18
        // 不添加会渲染错误，不知道麻将哪里 pop 了没有 apply
        //#if MC > 11605
        RenderSystem.applyModelViewMatrix();
        //#endif

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
                                            matrixStack.pushPose();
                                            matrixStack.translate(0, 0, 400);
                                            ModInfo.LOGGER.debug("subRenderX: {} subRenderY: {}", subRenderX, subRenderY);
                                            renderSelectedRect(matrixStack, subRenderX, subRenderY);
                                            renderOrderedTooltip(matrixStack, subItemStack, subRenderX, subRenderY + 8);
                                            matrixStack.popPose();

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

    // for 1.14

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
        fillGradient(matrices, ImmutableList.of(new GradientData(
                Color4f.fromColor(0x80ffffff), Color4f.fromColor(0x80ffffff),
                x, y, x + 16, y + 16, 0)));
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    public void renderOrderedTooltip(PoseStack matrices, ItemStack stack, int x, int y) {
        y = y + 8;
        RenderSystem.disableDepthTest();
        Minecraft mc = Minecraft.getInstance();
        List<Component> components = stack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ?
                TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        if (components.isEmpty())
            return;
        int xOffset = 0;
        int yOffset = (components.size() == 1) ? -2 : 0;

        for (Component component : components) {
            int m = mc.font.width(component);
            if (m > xOffset)
                xOffset = m;
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
        matrices.pushPose();


        float oldBlitOffset = mc.getItemRenderer().blitOffset;
        mc.getItemRenderer().blitOffset = 400.0F;

        Color4f colorA = Color4f.fromColor(0xf0100010);

        ArrayList<GradientData> gradientDataArrayList = new ArrayList<>();
        gradientDataArrayList.add(new GradientData(colorA, colorA, renderX - 3, renderY - 4,
                renderX + xOffset + 3, renderY - 3, 400));
        gradientDataArrayList.add(new GradientData(colorA, colorA, renderX - 3, renderY + yOffset + 3,
                renderX + xOffset + 3, renderY + yOffset + 4, 400));
        gradientDataArrayList.add(new GradientData(colorA, colorA, renderX - 3, renderY - 3,
                renderX + xOffset + 3, renderY + yOffset + 3, 400));
        gradientDataArrayList.add(new GradientData(colorA, colorA, renderX - 4, renderY - 3,
                renderX - 3, renderY + yOffset + 3, 400));
        gradientDataArrayList.add(new GradientData(colorA, colorA, renderX + xOffset + 3, renderY - 3,
                renderX + xOffset + 4, renderY + yOffset + 3, 400));

        Color4f colorB = Color4f.fromColor(0x505000ff);
        Color4f colorC = Color4f.fromColor(0x5028007f);

        gradientDataArrayList.add(new GradientData(colorB, colorC, renderX - 3, renderY - 3 + 1,
                renderX - 3 + 1, renderY + yOffset + 3 - 1, 400));
        gradientDataArrayList.add(new GradientData(colorB, colorC, renderX + xOffset + 2, renderY - 3 + 1,
                renderX + xOffset + 3, renderY + yOffset + 3 - 1, 400));
        gradientDataArrayList.add(new GradientData(colorB, colorB, renderX - 3, renderY - 3,
                renderX + xOffset + 3, renderY - 3 + 1, 400));
        gradientDataArrayList.add(new GradientData(colorC, colorC, renderX - 3, renderY + yOffset + 2,
                renderX + xOffset + 3, renderY + yOffset + 3, 400));

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        fillGradient(matrices, gradientDataArrayList);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        matrices.translate(0.0D, 0.0D, 400.0D);

        for (int i = 0; i < components.size(); i++) {
            mc.font.drawInBatch(components.get(i), renderX, renderY, 0xffffffff, true,
                    matrices.last().pose(), false, 0, 0xf000f0);
            renderY += 10 + ((i == 0) ? 2 : 0);
        }
        matrices.popPose();
        mc.getItemRenderer().blitOffset = oldBlitOffset;
        RenderSystem.enableDepthTest();
    }

    public static class GradientData {
        public int startX;
        public int startY;
        public int endX;
        public int endY;

        public int z;

        public Color4f colorStart;
        public Color4f colorEnd;

        public GradientData(Color4f colorStart, Color4f colorEnd, int startX, int startY, int endX, int endY, int z) {
            this.endY = endY;
            this.colorStart = colorStart;
            this.colorEnd = colorEnd;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.z = z;
        }
    }
}
