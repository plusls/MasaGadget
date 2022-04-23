package com.plusls.MasaGadget.mixin.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.event.RenderEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render*", at = @At(value = "RETURN"))
    private void postRenderEntity(T entity, float yaw, float tickDelta, PoseStack matrixStack,
                                  MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
        RenderEvent.onEntityRendererLast(entityRenderDispatcher, entity, yaw, tickDelta, matrixStack, light);
    }
}
