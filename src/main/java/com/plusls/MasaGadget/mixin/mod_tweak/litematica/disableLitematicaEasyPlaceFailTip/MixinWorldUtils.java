package com.plusls.MasaGadget.mixin.mod_tweak.litematica.disableLitematicaEasyPlaceFailTip;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(ModId.litematica))
@Mixin(value = WorldUtils.class, priority = 1100, remap = false)
public class MixinWorldUtils {
    @Inject(
            method = "handleEasyPlace",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/InfoUtils;showGuiOrInGameMessage(Lfi/dy/masa/malilib/gui/Message$MessageType;Ljava/lang/String;[Ljava/lang/Object;)V"
            ),
            cancellable = true
    )
    private static void cancelEasyPlaceFailTip(Minecraft mc, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.disableLitematicaEasyPlaceFailTip.getBooleanValue()) {
            cir.setReturnValue(true);
        }
    }
}