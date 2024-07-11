package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.backportI18nSupport;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.tweakeroo.config.ConfigBooleanClient;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = ModId.tweakeroo, versionPredicates = "<0.11.1"))
@Mixin(value = ConfigBooleanClient.class, remap = false)
public class MixinConfigBooleanClient {
    @Dynamic
    @Redirect(
            method = "getConfigGuiDisplayName",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/tweakeroo/config/ConfigBooleanClient;getName()Ljava/lang/String;"
            )
    )
    private String i18nDisplayName(ConfigBooleanClient instance) {
        if (Configs.backportI18nSupport.getBooleanValue()) {
            return MiscUtil.getTranslatedOrFallback("config.name." + instance.getName().toLowerCase(), instance.getName());
        } else {
            return instance.getName();
        }
    }
}
