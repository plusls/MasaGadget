package com.plusls.MasaGadget.mixin.event.disconnect;

import com.plusls.MasaGadget.impl.event.DisconnectEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.impl.event.EventManager;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {
    @Inject(
            method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onDisconnect(CallbackInfo ci) {
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            EventManager.dispatch(new DisconnectEvent());
        }
    }
}
