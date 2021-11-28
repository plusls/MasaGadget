package com.plusls.MasaGadget.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;


public class RenderUtil {

    // 只能在 EntityRenderer.render 中调用
    public static void renderTextOnEntity(MatrixStack matrixStack, Entity entity,
                                          EntityRenderDispatcher entityRenderDispatcher, VertexConsumerProvider vertexConsumerProvider,
                                          Text text, float height) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (entityRenderDispatcher.getSquaredDistanceToCamera(entity) <= 4096.0D) {
            matrixStack.push();
            matrixStack.translate(0, height, 0);
            matrixStack.multiply(entityRenderDispatcher.getRotation());
            matrixStack.scale(-0.018F, -0.018F, 0.018F);
            matrixStack.translate(0, 0, -33);
            Matrix4f lv = matrixStack.peek().getModel();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int k = (int) (g * 255.0F) << 24;
            float h = (float) (-client.textRenderer.getWidth(text) / 2);
            client.textRenderer.draw(text, h, 0, 553648127, false, lv, vertexConsumerProvider, false, k, 0xf00000);
            client.textRenderer.draw(text, h, 0, -1, false, lv, vertexConsumerProvider, false, 0, 0xf00000);
            matrixStack.pop();
        }
    }
}
