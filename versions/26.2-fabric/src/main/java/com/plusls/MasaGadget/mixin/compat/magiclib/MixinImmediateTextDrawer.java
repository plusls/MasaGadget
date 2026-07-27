package com.plusls.MasaGadget.mixin.compat.magiclib;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.impl.render.text.ImmediateTextDrawer;

@Mixin(value = ImmediateTextDrawer.class, remap = false)
public abstract class MixinImmediateTextDrawer {
    @Unique
    private boolean masa_gadget_mod$drawn;

    @Shadow
    public abstract void draw();

    @Inject(method = "draw", at = @At("HEAD"))
    private void masa_gadget_mod$markDrawn(CallbackInfo ci) {
        this.masa_gadget_mod$drawn = true;
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void masa_gadget_mod$drawBeforeClose(CallbackInfo ci) {
        if (!this.masa_gadget_mod$drawn) {
            this.draw();
        }
    }
}
