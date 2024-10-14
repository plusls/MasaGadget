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
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.i18n.I18n;
import top.hendrixshen.magiclib.api.platform.PlatformType;

@Dependencies(
        require = {
                @Dependency(value = ModId.minihud, versionPredicates = "<0.20.0"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(
        require = {
                @Dependency(value = ModId.minecraft, versionPredicates = "<1.18-"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE)
        }
)
@Mixin(value = RendererToggle.class, remap = false)
public abstract class MixinRendererToggle implements IHotkeyTogglable {
    @Inject(method = "getComment", at = @At("RETURN"), cancellable = true)
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
    @Inject(method = "getConfigGuiDisplayName", at = @At("HEAD"), cancellable = true)
    private void patchGetConfigGuiDisplayName(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(I18n.translateOrFallback("config.name." + this.getName().toLowerCase(),
                    this.getName()));
        }
    }
}
