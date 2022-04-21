package com.plusls.MasaGadget.mixin.event.disconnect;

import com.plusls.MasaGadget.event.DisconnectEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {
    // TODO 去掉该部分
    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At(value = "HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            DisconnectEvent.onDisconnect();
        }
    }
}
