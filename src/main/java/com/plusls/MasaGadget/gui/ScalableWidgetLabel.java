package com.plusls.MasaGadget.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.TextComponent;

public class ScalableWidgetLabel extends WidgetLabel {
    public float scale;

    public ScalableWidgetLabel(int x, int y, int width, int height, int textColor, float scale, String... text) {
        super(x, y, width, height, textColor, text);
        this.scale = scale;
    }

    public void render(int mouseX, int mouseY, boolean selected) {
        this.render(mouseX, mouseY, selected, new PoseStack());
    }

        @Override
    public void render(int mouseX, int mouseY, boolean selected, PoseStack matrixStack) {
        if (this.visible) {
            matrixStack.pushPose();
            RenderUtils.setupBlend();
            this.drawLabelBackground();

            int fontHeight = this.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); ++i) {
                String text = this.labels.get(i);
                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                if (this.centered) {
                    matrixStack.translate(this.x + this.width / 2.0f - this.getStringWidth(text) / 2.0f, yTextStart + i * fontHeight, 0);
                } else {
                    matrixStack.translate(this.x, yTextStart + i * fontHeight, 0);
                }
                matrixStack.scale(scale, scale, scale);
                Minecraft.getInstance().font.drawInBatch(text, 0, 0, this.textColor, true, matrixStack.last().pose(),
                        bufferSource, false, 0, 0xf000f0);
                bufferSource.endBatch();
            }
            matrixStack.popPose();
        }
    }
}
