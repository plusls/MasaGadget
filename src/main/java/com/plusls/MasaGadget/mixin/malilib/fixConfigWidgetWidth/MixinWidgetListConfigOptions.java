package com.plusls.MasaGadget.mixin.malilib.fixConfigWidgetWidth;

import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions {

    @Redirect(method = "getMaxNameLengthWrapped", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/config/IConfigBase;getName()Ljava/lang/String;", ordinal = 0))
    private String fixWidth(IConfigBase instance) {
        if (Configs.Malilib.FIX_CONFIG_WIDGET_WIDTH.getBooleanValue()) {
            return instance.getConfigGuiDisplayName();
        }
        return instance.getName();
    }

}

