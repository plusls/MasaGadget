package com.plusls.MasaGadget.impl.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import lombok.Getter;
import lombok.Setter;
import top.hendrixshen.magiclib.api.render.context.GuiRenderContext;
import top.hendrixshen.magiclib.api.render.context.RenderContext;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 1.21.11
//$$ import fi.dy.masa.malilib.render.GuiContext;
//#endif

//#if MC < 12105
import fi.dy.masa.malilib.render.RenderUtils;
//#endif
// CHECKSTYLE.ON: ImportOrder

// CHECKSTYLE.OFF: ImportOrder
//#if 1.21.11 > MC && MC > 1.19.4
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

//#if 1.20.1 > MC && MC > 1.15.2
import com.mojang.blaze3d.vertex.PoseStack;
//#endif
// CHECKSTYLE.ON: ImportOrder

@Getter
@Setter
public class ScalableWidgetLabel extends WidgetLabel {
    private float scale;

    public ScalableWidgetLabel(int x, int y, int width, int height, int textColor, float scale, String... text) {
        super(x, y, width, height, textColor, text);
        this.scale = scale;
    }

    @Override
    public void render(
            // CHECKSTYLE.OFF: NoWhitespaceBefore
            // CHECKSTYLE.OFF: SeparatorWrap
            //#if MC >= 1.21.11
            //$$ GuiContext guiContextOrGuiGraphics,
            //#elseif MC >= 1.21.6
            //$$ GuiGraphics guiContextOrGuiGraphics,
            //#endif
            int mouseX,
            int mouseY,
            boolean selected
            //#if MC < 12106
            //#if MC > 11904
            //$$ , GuiGraphics guiGraphicsOrPoseStack
            //#elseif MC > 11502
            , PoseStack guiGraphicsOrPoseStack
            //#endif
            //#endif
            // CHECKSTYLE.ON: SeparatorWrap
            // CHECKSTYLE.ON: NoWhitespaceBefore
    ) {
        if (this.visible) {
            //#if MC < 12105
            RenderUtils.setupBlend();
            //#endif
            this.drawLabelBackground(
                    // CHECKSTYLE.ON: Indentation
                    //#if MC >= 1.21.6
                    //$$ guiContextOrGuiGraphics
                    //#endif
                    // CHECKSTYLE.OFF: Indentation
            );
            GuiRenderContext renderContext = RenderContext.gui(
                    //#if MC >= 1.21.6
                    //$$ guiContextOrGuiGraphics
                    //#elseif MC >= 1.16
                    guiGraphicsOrPoseStack
                    //#endif
            );

            int fontHeight = this.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); i++) {
                String text = this.labels.get(i);
                double x = this.x + (this.centered ? this.width / 2.0 : 0);
                double y = yTextStart + i * fontHeight * scale;
                renderContext.pushMatrix();
                renderContext.scale(scale, scale);
                x /= scale;
                y /= scale;

                if (this.centered) {
                    this.drawCenteredStringWithShadow(
                            // CHECKSTYLE.OFF: NoWhitespaceBefore
                            // CHECKSTYLE.OFF: SeparatorWrap
                            //#if MC >= 12106
                            //$$ guiContextOrGuiGraphics,
                            //#endif
                            (int) x,
                            (int) y,
                            this.textColor,
                            text
                            //#if 12106 > MC && MC >= 11600
                            , guiGraphicsOrPoseStack
                            //#endif
                            // CHECKSTYLE.ON: SeparatorWrap
                            // CHECKSTYLE.ON: NoWhitespaceBefore
                    );
                } else {
                    this.drawStringWithShadow(
                            // CHECKSTYLE.OFF: NoWhitespaceBefore
                            // CHECKSTYLE.OFF: SeparatorWrap
                            //#if MC >= 12106
                            //$$ guiContextOrGuiGraphics,
                            //#endif
                            (int) x,
                            (int) y,
                            this.textColor,
                            text
                            //#if 12106 > MC && MC >= 11600
                            , guiGraphicsOrPoseStack
                            //#endif
                            // CHECKSTYLE.ON: SeparatorWrap
                            // CHECKSTYLE.ON: NoWhitespaceBefore
                    );
                }

                renderContext.popMatrix();
            }
        }
    }
}
