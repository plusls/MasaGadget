package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.backportI18nSupport;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.i18n.I18n;

@Dependencies(require = @Dependency(value = ModId.tweakeroo, versionPredicates = "<0.11.1"))
@Mixin(value = FeatureToggle.class, remap = false)
public abstract class MixinFeatureToggle implements IHotkeyTogglable {
    @Inject(
            method = "getComment",
            at = @At("RETURN"),
            cancellable = true
    )
    private void useI18nComment(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(I18n.translateOrFallback("config.comment." + this.getName().toLowerCase(),
                    cir.getReturnValue()));
        }
    }

    @Inject(
            method = "getConfigGuiDisplayName",
            at = @At("HEAD"),
            cancellable = true
    )
    private void patchGetConfigGuiDisplayName(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(I18n.translateOrFallback("config.name." + this.getName().toLowerCase(),
                    this.getName()));
        }
    }
}
