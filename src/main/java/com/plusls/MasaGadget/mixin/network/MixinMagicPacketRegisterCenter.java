package com.plusls.MasaGadget.mixin.network;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.network.packet.MagicPacketRegistrationCenter;

@Mixin(value = MagicPacketRegistrationCenter.class, remap = false)
public abstract class MixinMagicPacketRegisterCenter {
    @Inject(method = "common", at = @At("HEAD"))
    private static void register(CallbackInfo ci) {
        PcaSyncProtocol.registerPackets();
    }
}
