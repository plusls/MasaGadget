package com.plusls.MasaGadget.compat.mixin.event.render;

import com.mojang.blaze3d.vertex.PoseStackCompat;
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

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render*", at = @At(value = "RETURN"))
    private void postRenderEntity(T entity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
        PoseStackCompat poseStackCompat = new PoseStackCompat();
        poseStackCompat.translateCompat(x, y, z);
        RenderEvent.onEntityRendererLastCompat(entityRenderDispatcher, entity, yaw, tickDelta, poseStackCompat);
    }
}