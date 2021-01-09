package com.plusls.MasaGadget.mixin.util;

import com.plusls.MasaGadget.util.DisconnectEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Inject(method = "disconnect()V", at = @At(value = "HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
            DisconnectEvent.onDisconnect();
        }
    }
}
