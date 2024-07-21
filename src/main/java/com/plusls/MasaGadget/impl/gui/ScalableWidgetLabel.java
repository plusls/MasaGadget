package com.plusls.MasaGadget.impl.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.render.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import top.hendrixshen.magiclib.api.compat.minecraft.client.gui.FontCompat;
import top.hendrixshen.magiclib.api.render.context.RenderContext;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

//#if MC > 11404
import top.hendrixshen.magiclib.util.minecraft.render.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
//#endif

//#if MC < 12000 && MC > 11404
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
            int mouseX,
            int mouseY,
            boolean selected
            //#if MC > 11904
            //$$ , GuiGraphics guiGraphicsOrPoseStack
            //#elseif MC > 11502
            , PoseStack guiGraphicsOrPoseStack
            //#endif
    ) {
        RenderContext renderContext = RenderContext.of(
                //#if MC > 11502
                guiGraphicsOrPoseStack
                //#endif
        );

        if (this.visible) {
            renderContext.pushMatrix();
            RenderUtils.setupBlend();
            this.drawLabelBackground();

            int fontHeight = this.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); i++) {
                String text = this.labels.get(i);

                if (this.centered) {
                    renderContext.translate(
                            this.x + this.width / 2.0f - this.getStringWidth(text) / 2.0f,
                            yTextStart + i * fontHeight,
                            0
                    );
                } else {
                    renderContext.translate(this.x, yTextStart + i * fontHeight, 0);
                }

                renderContext.scale(scale, scale, scale);
                //#if MC > 11404
                MultiBufferSource.BufferSource immediate = RenderUtil.getBufferSource();
                //#endif
                FontCompat.of(Minecraft.getInstance().font)
                        .drawInBatch(
                                text,
                                0.0F,
                                0.0F,
                                this.textColor,
                                true,
                                //#if MC > 11404
                                //#if MC > 11502
                                renderContext.getMatrixStack().getPoseStack().last().pose(),
                                //#else
                                //$$ new PoseStack().last().pose(),
                                //#endif
                                immediate,
                                //#endif
                                FontCompat.DisplayMode.NORMAL,
                                0,
                                0xf000f0
                        );
                //#if MC > 11404
                immediate.endBatch();
                //#endif
            }

            renderContext.popMatrix();
        }
    }
}
