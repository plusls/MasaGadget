package com.plusls.MasaGadget.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class RenderEvent {
    private static final List<EntityRendererLastCallback> entityRendererCallbackList = new ArrayList<>();

    public static void register(EntityRendererLastCallback callback) {
        entityRendererCallbackList.add(callback);
    }

    public static void onEntityRendererLastCompat(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                                  Object matrixStack) {
        onEntityRendererLast(dispatcher, entity, yaw, tickDelta, (PoseStack) matrixStack, null, 0);
    }

    public static void onEntityRendererLast(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
        entityRendererCallbackList.forEach(callback ->
                callback.onEntityRendererLast(dispatcher, entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light));
    }

    @FunctionalInterface
    public interface EntityRendererLastCallback {
        void onEntityRendererLast(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                  PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light);
    }
}
