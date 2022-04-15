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
        Minecraft client = Minecraft.getInstance();
        if (entityRenderDispatcher.distanceToSqr(entity) <= 4096.0D) {
            matrixStack.pushPose();
            matrixStack.translate(0, height, 0);
            matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());
            matrixStack.scale(-0.018F, -0.018F, 0.018F);
            matrixStack.translate(0, 0, -33);
            Matrix4f matrix4f = matrixStack.last().pose();
            float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
            float xOffset = (float) (-client.font.width(text) / 2);
            // 		ARG 1 text
            //		ARG 2 x
            //		ARG 3 y
            //		ARG 4 color
            //			COMMENT the text color in the 0xAARRGGBB format
            //		ARG 5 shadow
            //		ARG 6 matrix
            //		ARG 7 vertexConsumers
            //		ARG 8 seeThrough
            //		ARG 9 backgroundColor
            //		ARG 10 light
            client.font.drawInBatch(text, xOffset, 0, 0x20ffffff, false, matrix4f, vertexConsumerProvider, false, backgroundColor, 0xf00000);
            client.font.drawInBatch(text, xOffset, 0, 0xffffffff, false, matrix4f, vertexConsumerProvider, false, 0, 0xf00000);
            matrixStack.popPose();
        }
    }
}
