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
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.platform.PlatformType;

@Dependencies(require = {
        @Dependency(value = ModId.litematica, versionPredicates = "<0.0.0-dev.20210917.192300"),
        @Dependency(ModId.tweakeroo),
        @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
})
@Mixin(value = InputHandler.class, remap = false)
public class MixinInputHandler {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "nudgeSelection", at = @At("HEAD"), ordinal = 0)
    private static Player modifyPlayer(Player player) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
                Configs.nudgeSelectionSupportFreeCamera.getBooleanValue()) {
            player = CameraEntity.getCamera();
        }

        return player;
    }
}
