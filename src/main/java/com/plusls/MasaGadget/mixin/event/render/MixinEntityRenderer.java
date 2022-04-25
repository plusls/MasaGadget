package com.plusls.MasaGadget.mixin.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.event.RenderEvent;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC > 11404
import net.minecraft.client.renderer.MultiBufferSource;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render*", at = @At(value = "RETURN"))
    //#if MC > 11404
    private void postRenderEntity(T entity, float yaw, float tickDelta, PoseStack matrixStack,
                                  MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
        //#else
        //$$ private void postRenderEntity(T entity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
        //$$ PoseStack matrixStack = new PoseStack();
        //$$ matrixStack.translate(x, y, z);
        //$$ int light = 0;
        //#endif
        RenderEvent.onEntityRendererLast(entityRenderDispatcher, entity, yaw, tickDelta, matrixStack, light);
    }
}
