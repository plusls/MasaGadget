package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fixSearchbarHotkeyInput;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.gui.GuiBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.platform.PlatformType;

@Dependencies(
        require = {
                @Dependency(value = ModId.malilib, versionPredicates = "<0.11.6"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(
        require = {
                @Dependency(value = ModId.minecraft, versionPredicates = "<1.18-"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE)
        }
)
@Mixin(value = GuiBase.class, remap = false)
public class MixinGuiBase {
    @Shadow
    private int keyInputCount;
    @Unique
    private long masa_gadget_mod$openTime;

    @Inject(
            method = "init",
            at = @At("HEAD"),
            remap = true
    )
    private void preInit(CallbackInfo ci) {
        if (!Configs.fixSearchbarHotkeyInput.getBooleanValue()) {
            return;
        }

        this.masa_gadget_mod$openTime = System.nanoTime();
        this.keyInputCount = 0;
    }

    @Inject(
            method = "charTyped",
            at = @At("HEAD"),
            cancellable = true,
            remap = true
    )
    private void preCharTyped(char charIn, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.fixSearchbarHotkeyInput.getBooleanValue()) {
            return;
        }

        if (this.keyInputCount <= 0 && System.nanoTime() - this.masa_gadget_mod$openTime <= 100000000) {
            this.keyInputCount++;
            cir.setReturnValue(true);
        }
    }
}
