package com.plusls.MasaGadget.mixin.event;

import com.plusls.MasaGadget.impl.event.RenderEntityEvent;
import top.hendrixshen.magiclib.impl.event.EventManager;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Inject(
            //#if MC >= 1.21.10
            //$$ method = "extractVisibleEntities",
            //#elseif MC >= 1.21
            //$$ method = "renderEntities",
            //#else
            method = "renderLevel",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 1.21.10
                    //$$ target = "Lnet/minecraft/client/renderer/LevelRenderer;extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"
                    //#else
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"
                    //#endif
            )
    )
    private void postRenderEntity(CallbackInfo ci, @Local Entity entity) {
        EventManager.dispatch(RenderEntityEvent.create(entity));
    }
}
