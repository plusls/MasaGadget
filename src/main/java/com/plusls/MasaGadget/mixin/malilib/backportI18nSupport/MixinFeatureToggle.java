package com.plusls.MasaGadget.mixin.malilib.backportI18nSupport;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = {@Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(value = "minecraft", versionPredicate = "<=1.17.1")})
@Mixin(value = FeatureToggle.class, remap = false)
public abstract class MixinFeatureToggle implements IHotkeyTogglable {
    @Final
    @Shadow
    private String comment;


    @Inject(method = "getComment", at = @At(value = "HEAD"), cancellable = true)
    private void useI18nComment(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport) {
            cir.setReturnValue(MiscUtil.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(), this.comment));
        }
    }

    @Override
    public String getConfigGuiDisplayName() {
        if (Configs.backportI18nSupport) {
            return MiscUtil.getTranslatedOrFallback("config.name." + this.getName().toLowerCase(), this.getName());
        } else {
            return this.getName();
        }
    }
}
