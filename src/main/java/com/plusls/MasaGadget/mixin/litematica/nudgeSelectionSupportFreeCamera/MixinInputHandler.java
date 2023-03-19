package com.plusls.MasaGadget.mixin.litematica.nudgeSelectionSupportFreeCamera;


import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.litematica.event.InputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

@Dependencies(and = {@Dependency(ModInfo.LITEMATICA_MOD_ID), @Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(value = "minecraft", versionPredicate = "<=1.15.2")})
@Mixin(value = InputHandler.class, remap = false)
public class MixinInputHandler {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "nudgeSelection", at = @At(value = "HEAD"), ordinal = 0)
    static private Player modifyPlayer(Player player) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && Configs.nudgeSelectionSupportFreeCamera) {
            player = CameraEntity.getCamera();
        }
        return player;
    }
}
