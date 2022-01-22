package com.plusls.MasaGadget.mixin.event.disconnect;

import com.plusls.MasaGadget.event.DisconnectEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
            DisconnectEvent.onDisconnect();
        }
    }
}
