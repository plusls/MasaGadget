package com.plusls.MasaGadget.mixin.mod_tweak.litematica.nudgeSelectionSupportFreeCamera;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.litematica.event.InputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = {
        @Dependency(ModId.litematica),
        @Dependency(ModId.tweakeroo),
        @Dependency(value = "minecraft", versionPredicates = "<=1.15.2")
})
@Mixin(value = InputHandler.class, remap = false)
public class MixinInputHandler {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "nudgeSelection", at = @At(value = "HEAD"), ordinal = 0)
    static private Player modifyPlayer(Player player) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
                Configs.nudgeSelectionSupportFreeCamera.getBooleanValue()) {
            player = CameraEntity.getCamera();
        }

        return player;
    }
}
