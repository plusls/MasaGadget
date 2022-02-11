package com.plusls.MasaGadget.mixin.malilib.backportI18nSupport;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.tweakeroo.config.ConfigBooleanClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = ConfigBooleanClient.class, remap = false)
public abstract class MixinConfigBooleanClient {

    @Redirect(method = "getConfigGuiDisplayName", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/config/ConfigBooleanClient;getName()Ljava/lang/String;", ordinal = 0))
    private String i18nDisplayName(ConfigBooleanClient instance) {
        if (Configs.Malilib.BACKPORT_I18N_SUPPORT.getBooleanValue()) {
            return MiscUtil.getTranslatedOrFallback("config.name." + instance.getName().toLowerCase(), instance.getName());
        } else {
            return instance.getName();
        }
    }
}
