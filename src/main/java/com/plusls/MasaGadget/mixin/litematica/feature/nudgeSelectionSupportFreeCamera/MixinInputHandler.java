package com.plusls.MasaGadget.mixin.litematica.feature.nudgeSelectionSupportFreeCamera;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import fi.dy.masa.litematica.event.InputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = InputHandler.class, remap = false)
public class MixinInputHandler {
    @ModifyVariable(method = "nudgeSelection", at = @At(value = "HEAD"), ordinal = 0)
    static private PlayerEntity modifyPlayer(PlayerEntity oldPlayerEntity) {
        if (MasaGadgetMixinPlugin.isTweakerooLoaded && FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue()) {
            return (PlayerEntity) MinecraftClient.getInstance().getCameraEntity();
        } else {
            return oldPlayerEntity;
        }
    }

}
