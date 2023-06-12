package com.plusls.MasaGadget.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;

//#if MC > 11904
import net.minecraft.client.gui.GuiGraphics;
//#endif

public class ScalableWidgetLabel extends WidgetLabel {
    public float scale;

    public ScalableWidgetLabel(int x, int y, int width, int height, int textColor, float scale, String... text) {
        super(x, y, width, height, textColor, text);
        this.scale = scale;
    }

    @Override
    //#if MC > 11904
    public void render(int mouseX, int mouseY, boolean selected, GuiGraphics gui) {
        PoseStack poseStack = gui.pose();
    //#elseif MC > 11502
    //$$ public void render(int mouseX, int mouseY, boolean selected, PoseStack poseStack) {
        //#else
        //$$ public void render(int mouseX, int mouseY, boolean selected) {
        //$$ PoseStack poseStack = new PoseStack();
        //#endif
        if (this.visible) {
            poseStack.pushPose();
            RenderUtils.setupBlend();
            this.drawLabelBackground();

            int fontHeight = this.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); ++i) {
                String text = this.labels.get(i);
                if (this.centered) {
                    poseStack.translate(this.x + this.width / 2.0f - this.getStringWidth(text) / 2.0f, yTextStart + i * fontHeight, 0);
                } else {
                    poseStack.translate(this.x, yTextStart + i * fontHeight, 0);
                }
                poseStack.scale(scale, scale, scale);
                Minecraft.getInstance().font.drawInBatch(text, 0, 0, this.textColor, true, poseStack.last().pose(),
                        false, 0, 0xf000f0);
            }
            poseStack.popPose();
        }
    }
}
