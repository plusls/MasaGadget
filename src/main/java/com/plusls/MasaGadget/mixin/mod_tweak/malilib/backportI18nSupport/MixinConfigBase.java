package com.plusls.MasaGadget.mixin.mod_tweak.malilib.backportI18nSupport;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.i18n.I18n;
import top.hendrixshen.magiclib.api.platform.PlatformType;
import top.hendrixshen.magiclib.util.ReflectionUtil;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

@Dependencies(
        require = {
                @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(
        require = {
                @Dependency(value = ModId.minecraft, versionPredicates = "<1.18-"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE)
        }
)
@Mixin(value = ConfigBase.class, remap = false)
public abstract class MixinConfigBase implements IConfigBase {
    @Unique
    private static final ValueContainer<Class<?>> masa_gadget_mod$tweakerMoreIConfigBaseClass = ReflectionUtil
            .getClass("me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase");

    @Inject(
            method = "getComment",
            at = @At("RETURN"),
            cancellable = true
    )
    private void useI18nComment(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(MiscUtil.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(),
                    cir.getReturnValue()));
        }
    }

    @Intrinsic
    @Override
    public String getConfigGuiDisplayName() {
        return IConfigBase.super.getConfigGuiDisplayName();
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Inject(
            method = "getConfigGuiDisplayName",
            at = @At("HEAD"),
            cancellable = true
    )
    private void patchGetConfigGuiDisplayName(CallbackInfoReturnable<String> cir) {
        if (MixinConfigBase.masa_gadget_mod$tweakerMoreIConfigBaseClass
                .filter(clazz -> clazz.isInstance(this)).isPresent()) {
            return;
        }

        if (Configs.backportI18nSupport.getBooleanValue()) {
            cir.setReturnValue(I18n.translateOrFallback("config.name." + this.getName().toLowerCase(),
                    this.getName()));
        }
    }
}
