package com.plusls.MasaGadget.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class RenderEvent {
    private static final List<EntityRendererLastCallback> entityRendererCallbackList = new ArrayList<>();

    public static void register(EntityRendererLastCallback callback) {
        entityRendererCallbackList.add(callback);
    }


    public static void onEntityRendererLast(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                            PoseStack matrixStack, int light) {
        entityRendererCallbackList.forEach(callback ->
                callback.onEntityRendererLast(dispatcher, entity, yaw, tickDelta, matrixStack, light));
    }

    @FunctionalInterface
    public interface EntityRendererLastCallback {
        void onEntityRendererLast(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                  PoseStack matrixStack, int light);
    }
}
