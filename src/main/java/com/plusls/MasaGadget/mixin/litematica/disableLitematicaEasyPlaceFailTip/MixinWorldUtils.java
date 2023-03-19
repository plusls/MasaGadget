package com.plusls.MasaGadget.mixin.litematica.disableLitematicaEasyPlaceFailTip;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.LITEMATICA_MOD_ID))
@Mixin(value = WorldUtils.class, priority = 1001, remap = false)
public class MixinWorldUtils {
    @Inject(method = "handleEasyPlace", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/util/InfoUtils;showGuiOrInGameMessage(Lfi/dy/masa/malilib/gui/Message$MessageType;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0), cancellable = true)
    private static void cancelEasyPlaceFailTip(Minecraft mc, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.disableLitematicaEasyPlaceFailTip) {
            cir.setReturnValue(true);
        }
    }
}