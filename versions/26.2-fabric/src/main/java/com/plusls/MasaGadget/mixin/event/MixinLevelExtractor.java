package com.plusls.MasaGadget.mixin.event;

import com.plusls.MasaGadget.impl.event.RenderEntityEvent;
import top.hendrixshen.magiclib.impl.event.EventManager;

import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc26.1: subproject 1.16.5 (main project) [dummy]</li>
 * <li>mc26.2+        : subproject 26.2        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Mixin(LevelExtractor.class)
public abstract class MixinLevelExtractor {
    @Inject(
            method = "extractVisibleEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/extract/LevelExtractor;extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"
            )
    )
    private void postRenderEntity(CallbackInfo ci, @Local Entity entity) {
        EventManager.dispatch(RenderEntityEvent.create(entity));
    }
}
