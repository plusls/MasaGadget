package com.plusls.MasaGadget.mixin.litematica.disableLitematicaEasyPlaceFailTip;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Dependencies(dependencyList = @Dependency(modId = ModInfo.LITEMATICA_MOD_ID, version = "*"))
@Mixin(value = WorldUtils.class, priority = 1001, remap = false)
public class MixinWorldUtils {
    @Inject(method = "handleEasyPlace", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/util/InfoUtils;showGuiOrInGameMessage(Lfi/dy/masa/malilib/gui/Message$MessageType;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0), cancellable = true)
    private static void cancelEasyPlaceFailTip(MinecraftClient mc, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.Litematica.DISABLE_LITEMATICA_EASY_PLACE_FAIL_TIP.getBooleanValue()) {
            cir.setReturnValue(true);
        }
    }
}