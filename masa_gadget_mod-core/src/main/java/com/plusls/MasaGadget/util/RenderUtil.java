package com.plusls.MasaGadget.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;


public class RenderUtil {

    // 只能在 EntityRenderer.render 中调用
    public static void renderTextOnEntity(PoseStack matrixStack, Entity entity,
                                          EntityRenderDispatcher entityRenderDispatcher, MultiBufferSource vertexConsumerProvider,
                                          Component text, float height) {
        // TODO 理解代码并兼容低版本
        Minecraft client = Minecraft.getInstance();
        if (entityRenderDispatcher.distanceToSqr(entity) <= 4096.0D) {
            matrixStack.pushPose();
            matrixStack.translate(0, height, 0);
            matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());
            matrixStack.scale(-0.018F, -0.018F, 0.018F);
            matrixStack.translate(0, 0, -33);
            Matrix4f lv = matrixStack.last().pose();
            float g = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int k = (int) (g * 255.0F) << 24;
            float h = (float) (-client.font.width(text) / 2);
            client.font.drawInBatch(text, h, 0, 553648127, false, lv, vertexConsumerProvider, false, k, 0xf00000);
            client.font.drawInBatch(text, h, 0, -1, false, lv, vertexConsumerProvider, false, 0, 0xf00000);
            matrixStack.popPose();
        }
    }
}
