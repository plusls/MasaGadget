package io.github.plusls.MasaGadget.mixin.client;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public abstract class MixinMinecraftClient {
    @Inject(
            method = {"disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"},
            at = {@At("HEAD")}
    )
    private void onDisconnectPre(Screen screen, CallbackInfo ci) {
        MasaGadgetMod.masaGagdetInServer = false;
    }
}
