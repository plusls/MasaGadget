package com.plusls.MasaGadget.mixin.malilib.backportI18nSupport;


import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.tweakeroo.config.ConfigBooleanClient;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

@Dependencies(and = {@Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(value = "minecraft", versionPredicate = "<=1.17.1")})
@Mixin(value = ConfigBooleanClient.class, remap = false)
public abstract class MixinConfigBooleanClient {

    @Dynamic
    @Redirect(method = "getConfigGuiDisplayName", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/config/ConfigBooleanClient;getName()Ljava/lang/String;", ordinal = 0))
    private String i18nDisplayName(ConfigBooleanClient instance) {
        if (Configs.backportI18nSupport) {
            return MiscUtil.getTranslatedOrFallback("config.name." + instance.getName().toLowerCase(), instance.getName());
        } else {
            return instance.getName();
        }
    }
}