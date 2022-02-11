package com.plusls.MasaGadget.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.render.RenderUtils;

public class ScalableWidgetLabel extends WidgetLabel {
    public float scale;

    public ScalableWidgetLabel(int x, int y, int width, int height, int textColor, float scale, String... text) {
        super(x, y, width, height, textColor, text);
        this.scale = scale;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected) {
        if (this.visible) {
            RenderUtils.setupBlend();
            RenderSystem.pushMatrix();
            this.drawLabelBackground();
            int fontHeight = this.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); ++i) {
                String text = this.labels.get(i);
                if (this.centered) {
                    RenderSystem.translated(this.x, yTextStart + i * fontHeight, 0);
                    RenderSystem.scaled(scale, scale, scale);
                    this.drawCenteredStringWithShadow(0, 0, this.textColor, text);
                } else {
                    RenderSystem.translated(this.x, yTextStart + i * fontHeight, 0);
                    RenderSystem.scaled(scale, scale, scale);
                    this.drawStringWithShadow(0, 0, this.textColor, text);
                }
            }
            RenderSystem.popMatrix();
        }
    }
}
