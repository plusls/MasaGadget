package com.plusls.MasaGadget.mixin.generic.cacheContainerMenu;

import com.plusls.MasaGadget.generic.cacheContainerMenu.cacheContainerMenu.CacheContainerMenuHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @Inject(method = "closeContainer", at = @At(value = "RETURN"))
    private void postCloseContainer(CallbackInfo ci) {
        CacheContainerMenuHandler.clearLastClickData();
    }
}
