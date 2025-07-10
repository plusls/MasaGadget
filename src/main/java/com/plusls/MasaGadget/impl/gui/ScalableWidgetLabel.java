package com.plusls.MasaGadget.impl.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.render.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import top.hendrixshen.magiclib.api.render.context.GuiRenderContext;
import top.hendrixshen.magiclib.api.render.context.RenderContext;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

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
            //#if MC >= 12106
            //$$ GuiGraphics guiGraphicsOrPoseStack,
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
    ) {
        if (this.visible) {
            //#if MC < 12105
            RenderUtils.setupBlend();
            //#endif
            this.drawLabelBackground(
                    //#if MC >= 12106
                    //$$ guiGraphicsOrPoseStack
                    //#endif
            );
            GuiRenderContext renderContext = RenderContext.gui(
                    //#if MC > 11502
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
                            //#if MC >= 12106
                            //$$ guiGraphicsOrPoseStack,
                            //#endif
                            (int) x,
                            (int) y,
                            this.textColor,
                            text
                            //#if 12106 > MC && MC >= 11600
                            , guiGraphicsOrPoseStack
                            //#endif
                    );
                } else {
                    this.drawStringWithShadow(
                            //#if MC >= 12106
                            //$$ guiGraphicsOrPoseStack,
                            //#endif
                            (int) x,
                            (int) y,
                            this.textColor,
                            text
                            //#if 12106 > MC && MC >= 11600
                            , guiGraphicsOrPoseStack
                            //#endif
                    );
                }

                renderContext.popMatrix();
            }
        }
    }
}
