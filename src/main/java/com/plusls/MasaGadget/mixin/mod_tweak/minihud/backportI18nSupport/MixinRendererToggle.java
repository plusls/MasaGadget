package com.plusls.MasaGadget.mixin.mod_tweak.minihud.backportI18nSupport;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.minihud.config.RendererToggle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.i18n.I18n;

@Dependencies(require = @Dependency(value = ModId.minihud, versionPredicates = "<0.20.0"))
@Mixin(value = RendererToggle.class, remap = false)
public abstract class MixinRendererToggle implements IHotkeyTogglable {
    @Inject(
            method = "getComment",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    private void useI18nComment(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(MiscUtil.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(),
                    cir.getReturnValue()));
        }
    }

    @Override
    public String getConfigGuiDisplayName() {
        return IHotkeyTogglable.super.getConfigGuiDisplayName();
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Inject(
            method = "getConfigGuiDisplayName",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void patchGetConfigGuiDisplayName(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(I18n.translateOrFallback("config.name." + this.getName().toLowerCase(),
                    this.getName()));
        }
    }
}
