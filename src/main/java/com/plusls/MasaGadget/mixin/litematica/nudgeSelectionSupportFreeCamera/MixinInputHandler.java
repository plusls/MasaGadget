package com.plusls.MasaGadget.mixin.litematica.nudgeSelectionSupportFreeCamera;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.mixin.litematica.LitematicaDependencyUtil;
import fi.dy.masa.litematica.event.InputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Dependencies(dependencyList = {@Dependency(modId = MasaGadgetMixinPlugin.LITEMATICA_MOD_ID, version = "<" + LitematicaDependencyUtil.NUDGE_SELECTION_SUPPORT_FREECAMERA_BREAK_VERSION,
        predicate = LitematicaDependencyUtil.TweakerooPredicate.class),
        @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*")})
@Mixin(value = InputHandler.class, remap = false)
public class MixinInputHandler {
    @ModifyVariable(method = "nudgeSelection", at = @At(value = "HEAD"), ordinal = 0)
    static private PlayerEntity modifyPlayer(PlayerEntity player) {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && Configs.Litematica.NUDGE_SELECTION_SUPPORT_FREE_CAMERA.getBooleanValue()) {
            player = (PlayerEntity) MinecraftClient.getInstance().getCameraEntity();
        }
        return player;
    }
}
